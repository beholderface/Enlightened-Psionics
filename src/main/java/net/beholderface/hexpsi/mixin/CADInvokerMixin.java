package net.beholderface.hexpsi.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.common.item.ItemCAD;

@Mixin(ItemCAD.class)
public interface CADInvokerMixin {
    @Invoker("getSocketable")
    ISocketable publicGetSocketable(ItemStack stack);
}
