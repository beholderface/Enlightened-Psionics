package net.beholderface.hexpsi.items;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.HexHolderItem;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MathUtils;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.msgs.MsgNewSpiralPatternsS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.hexpsi.hex.environment.TrinketBulletCastEnv;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.common.item.ItemSpellBullet;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageSpellError;

import javax.annotation.Nonnull;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static at.petrak.hexcasting.common.items.magic.ItemMediaHolder.HEX_COLOR;

public class TrinketSpellBulletItem extends ItemSpellBullet implements HexHolderItem, MediaHolderItem {
    public TrinketSpellBulletItem(Properties properties) {
        super(properties);
        //psi forcing a max stack size is such a bruh moment
        this.maxStackSize = 1;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean hasHex(ItemStack itemStack) {
        return NBTHelper.hasList(itemStack, "patterns", (byte)10);
    }

    @Override
    public @Nullable List<Iota> getHex(ItemStack itemStack, ServerLevel serverLevel) {
        ListTag patsTag = NBTHelper.getList(itemStack, "patterns", 10);
        if (patsTag == null) {
            return null;
        } else {
            ArrayList<Iota> out = new ArrayList();

            for(Tag patTag : patsTag) {
                CompoundTag tag = NBTHelper.getAsCompound(patTag);
                out.add(IotaType.deserialize(tag, serverLevel));
            }

            return out;
        }
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> program, @Nullable FrozenPigment pigment, long media) {
        ListTag patsTag = new ListTag();

        for(Iota pat : program) {
            patsTag.add(IotaType.serialize(pat));
        }

        NBTHelper.putList(stack, "patterns", patsTag);
        if (pigment != null) {
            NBTHelper.putCompound(stack, "pigment", pigment.serializeToNBT());
        }

        NBTHelper.putLong(stack, "hexcasting:media", media);
        NBTHelper.putLong(stack, "hexcasting:start_media", media);
    }

    @Override
    public void clearHex(ItemStack stack) {
        NBTHelper.remove(stack, "patterns");
        NBTHelper.remove(stack, "pigment");
        NBTHelper.remove(stack, "hexcasting:media");
        NBTHelper.remove(stack, "hexcasting:start_media");
    }

    @Override
    public @Nullable FrozenPigment getPigment(ItemStack itemStack) {
        CompoundTag ctag = NBTHelper.getCompound(itemStack, "pigment");
        return ctag == null ? null : FrozenPigment.fromNBT(ctag);
    }

    @Override
    public long getMedia(ItemStack stack) {
        return NBTHelper.hasInt(stack, "hexcasting:media") ? (long)NBTHelper.getInt(stack, "hexcasting:media") : NBTHelper.getLong(stack, "hexcasting:media");
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return NBTHelper.hasInt(stack, "hexcasting:start_media") ? (long)NBTHelper.getInt(stack, "hexcasting:start_media") : NBTHelper.getLong(stack, "hexcasting:start_media");
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        NBTHelper.putLong(stack, "hexcasting:media", MathUtils.clamp(media, 0L, this.getMaxMedia(stack)));
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return this.hasHex(pStack);
    }

    public int getBarColor(ItemStack pStack) {
        long media = this.getMedia(pStack);
        long maxMedia = this.getMaxMedia(pStack);
        return MediaHelper.mediaBarColor(media, maxMedia);
    }

    public int getBarWidth(ItemStack pStack) {
        long media = this.getMedia(pStack);
        long maxMedia = this.getMaxMedia(pStack);
        return MediaHelper.mediaBarWidth(media, maxMedia);
    }

    @Override
    public boolean canProvideMedia(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack itemStack) {
        return true;
    }


    public static final String TAG_SUPPRESS_REPORT = "hexpsi:suppress_media_report";
    public static final String TAG_CAST_TIMESTAMP = "lastCastStamp";

    @Override
    public ArrayList<Entity> castSpell(ItemStack stack, SpellContext context) {
        boolean shouldCastHex = false;
        //a lot of this is copied from base psi's CompiledSpell#safeExecute method
        if (!context.caster.getCommandSenderWorld().isClientSide) {
            try {
                if (context.actions == null) {
                    context.actions = (Stack)context.cspell.actions.clone();
                }
                if (context.cspell.execute(context)) {
                    PsiAPI.internalHandler.delayContext(context);
                }
                if ((!context.stopped) && this.hasHex(stack)) {
                    long lastArmorCast = -100L;
                    if (context.tool != null && context.tool.getItem() instanceof ItemPsimetalArmor){
                        CompoundTag data = stack.getOrCreateTag();
                        if (data.contains(TAG_CAST_TIMESTAMP)){
                            lastArmorCast = data.getLong(TAG_CAST_TIMESTAMP);
                        }
                    }
                    shouldCastHex = context.caster.level().getGameTime() - lastArmorCast >= 20;
                }
            } catch (SpellRuntimeException e) {
                if (!context.shouldSuppressErrors()) {
                    context.caster.sendSystemMessage(Component.translatable(e.getMessage()).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                    int x = context.cspell.currentAction.piece.x + 1;
                    int y = context.cspell.currentAction.piece.y + 1;
                    MessageSpellError message = new MessageSpellError("psi.spellerror.position", x, y);
                    MessageRegister.sendToPlayer(message, context.caster);
                }
            }
            if (shouldCastHex){
                //If the spell doesn't intentionally halt, error, or pause, cast the bullet's hex.
                assert context.caster instanceof ServerPlayer;
                ServerPlayer caster = (ServerPlayer) context.caster;
                long originalMedia = this.getMedia(stack);
                try {
                    this.castHex(caster.serverLevel(), caster, stack, context.castFrom, context);
                } catch (Exception idk){
                    //ignored
                } finally {
                    CompoundTag data = stack.getOrCreateTag();
                    data.putLong(TAG_CAST_TIMESTAMP, caster.level().getGameTime());
                }
                long postCastMedia = this.getMedia(stack);
                if (!context.cspell.metadata.getFlag(TAG_SUPPRESS_REPORT) && originalMedia != postCastMedia){
                    caster.displayClientMessage(this.getHoverText(stack, false), true);
                }
            }
        }
        return new ArrayList<>();
    }

    public static final String TAG_SUPPRESS_FX = "hexpsi:suppress_fx";

    public void castHex(ServerLevel level, ServerPlayer caster, ItemStack stack, InteractionHand hand, SpellContext context){
        TrinketBulletCastEnv env = new TrinketBulletCastEnv(caster, hand, stack, context);
        CastingVM harness = CastingVM.empty(env);
        List<Iota> instrs = this.getHex(stack, level);
        assert instrs != null;
        ExecutionClientView clientView = harness.queueExecuteAndWrapIotas(instrs, level);
        if (!context.cspell.metadata.getFlag(TAG_SUPPRESS_FX)){
            List<HexPattern> patterns = instrs.stream().filter((i) -> i instanceof PatternIota).map((i) -> ((PatternIota)i).getPattern()).toList();
            MsgNewSpiralPatternsS2C packet = new MsgNewSpiralPatternsS2C(caster.getUUID(), patterns, 20);
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(caster, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(caster, packet);
            if (clientView.getResolutionType().getSuccess()) {
                (new ParticleSpray(caster.position(), new Vec3((double)0.0F, (double)1.5F, (double)0.0F), 0.4, (Math.PI / 3D), 30)).sprayParticles(caster.serverLevel(), env.getPigment());
            }
            SoundEvent sound = env.getSound().sound();
            if (sound != null) {
                Vec3 soundPos = caster.position();
                caster.level().playSound((Player)null, soundPos.x, soundPos.y, soundPos.z, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    public double getCostModifier(ItemStack stack) {
        return 1.25;
    }

    private static final DecimalFormat PERCENTAGE = new DecimalFormat("####");
    private static final DecimalFormat DUST_AMOUNT;

    static {
        PERCENTAGE.setRoundingMode(RoundingMode.DOWN);
        DUST_AMOUNT = new DecimalFormat("###,###.##");
    }

    private Component getHoverText(ItemStack stack, boolean tooltip){
        long maxMedia = this.getMaxMedia(stack);
        long media = this.getMedia(stack);
        float fullness = this.getMediaFullness(stack);
        TextColor color = TextColor.fromRgb(MediaHelper.mediaBarColor(media, maxMedia));
        MutableComponent mediamount = Component.literal(DUST_AMOUNT.format((double)((float)media / 10000.0F)));
        MutableComponent percentFull = Component.literal(PERCENTAGE.format((double)(100.0F * fullness)) + "%");
        MutableComponent maxCapacity = Component.translatable("hexcasting.tooltip.media", new Object[]{DUST_AMOUNT.format((double)((float)maxMedia / 10000.0F))});
        mediamount.withStyle((style) -> style.withColor(HEX_COLOR));
        maxCapacity.withStyle((style) -> style.withColor(HEX_COLOR));
        percentFull.withStyle((style) -> style.withColor(color));
        if (tooltip){
            return Component.translatable("hexcasting.tooltip.media_amount.advanced", new Object[]{mediamount, maxCapacity, percentFull});
        } else {
            Component spellName = null;
            if (stack.getItem() instanceof TrinketSpellBulletItem trinketSpellBullet){
                spellName = super.getName(stack);
            } else {
                spellName = Component.translatable("hexpsi.tooltip.bullet_name_default");
            }
            return Component.translatable("hexpsi.tooltip.bullet_media", new Object[]{spellName, mediamount, maxCapacity, percentFull});
        }
    }

    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        Component bulletName = super.getName(stack);
        if (ISpellAcceptor.hasSpell(stack) && this.hasHex(stack)) {
            float fullness = this.getMediaFullness(stack);
            String rawPercentage = String.valueOf(fullness * 100.0f);
            String formattedPercentage = (fullness == 1.0f ? "100" : rawPercentage.substring(0, Math.min(rawPercentage.length(), 4))) + "%";
            Component percentage = Component.literal(" (").append(Component.literal(formattedPercentage).withStyle((style)->style.withColor(HEX_COLOR))).append(Component.literal(")"));
            return bulletName.copy().append(percentage);
        } else {
            return bulletName;
        }
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced){
        long maxMedia = this.getMaxMedia(pStack);
        if (maxMedia > 0L) {
            pTooltipComponents.add(this.getHoverText(pStack, true));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
