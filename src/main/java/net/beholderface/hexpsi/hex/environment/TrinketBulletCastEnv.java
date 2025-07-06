package net.beholderface.hexpsi.hex.environment;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.item.ItemCAD;

public class TrinketBulletCastEnv extends PackagedItemCastEnv {

    public final ItemStack bulletStack;
    public final SpellContext psiContext;

    public TrinketBulletCastEnv(ServerPlayer caster, InteractionHand castingHand, ItemStack bullet, SpellContext context) {
        super(caster, castingHand);
        this.bulletStack = bullet;
        this.psiContext = context;
    }

    @Override
    public FrozenPigment getPigment() {
        ADHexHolder casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(bulletStack);
        if (casterHexHolder == null) {
            return IXplatAbstractions.INSTANCE.getPigment(this.caster);
        } else {
            FrozenPigment hexHolderPigment = casterHexHolder.getPigment();
            return hexHolderPigment != null ? hexHolderPigment : IXplatAbstractions.INSTANCE.getPigment(this.caster);
        }
    }

    @Override
    public long extractMediaEnvironment(long costLeft, boolean simulate) {
        if (this.caster.isCreative()) {
            return 0L;
        } else {
            ADHexHolder casterHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(bulletStack);
            if (casterHexHolder == null) {
                return costLeft;
            } else {
                ADMediaHolder bulletMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(bulletStack);
                if (bulletMediaHolder != null) {
                    long extracted = bulletMediaHolder.withdrawMedia((long)((int)costLeft), simulate);
                    costLeft -= extracted;
                }
                //if internal media was not enough to pay for cast, attempt to draw from CAD's phial
                if (costLeft > 0){
                    ItemStack cadStack = PsiAPI.getPlayerCAD(this.caster);
                    ItemCAD cadItem = (ItemCAD) cadStack.getItem();
                    ItemStack batteryStack = cadItem.getComponentInSlot(cadStack, EnumCADComponent.BATTERY);
                    ADMediaHolder cadMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(cadStack);
                    if (cadMediaHolder != null && batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
                        costLeft -= mediaHolderItem.withdrawMedia(batteryStack, costLeft, simulate);
                    }
                }

                return costLeft;
            }
        }
    }

}
