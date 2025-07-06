package net.beholderface.hexpsi.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.common.item.ItemCAD;

import java.util.List;

@Mixin(ItemCAD.class)
public abstract class CADTooltipMixin {
    @Shadow public abstract ItemStack getComponentInSlot(ItemStack stack, EnumCADComponent type);

    @Inject(method = "appendHoverText", at = @At(value = "TAIL", remap = false), remap = false)
    public void addMediaContent(ItemStack stack, Level playerin, List<Component> tooltip, TooltipFlag advanced, CallbackInfo ci){
        ItemStack batteryStack = this.getComponentInSlot(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof ItemMediaHolder mediaHolderItem){
            mediaHolderItem.appendHoverText(batteryStack, playerin, tooltip, advanced);
        }
    }
}
