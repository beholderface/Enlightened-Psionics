package net.beholderface.hexpsi.mixin;

import net.beholderface.hexpsi.items.ISocketedMediaItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.item.tool.ItemPsimetalAxe;
import vazkii.psi.common.item.tool.ItemPsimetalPickaxe;
import vazkii.psi.common.item.tool.ItemPsimetalShovel;
import vazkii.psi.common.item.tool.ItemPsimetalSword;

import java.util.UUID;

import static net.beholderface.hexpsi.HexPsiApi.TAG_UUID;

@Mixin(value = {ItemPsimetalArmor.class, ItemPsimetalPickaxe.class, ItemPsimetalAxe.class, ItemPsimetalShovel.class, ItemPsimetalSword.class})
public class MediaToolMixin implements ISocketedMediaItem {

    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    public void putUUID(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo ci){
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(TAG_UUID)){
            UUID existingUUID = nbt.getUUID(TAG_UUID);
            if (entityIn.getUUID().equals(existingUUID)){
                return;
            }
        }
        nbt.putUUID(TAG_UUID, entityIn.getUUID());
    }
}
