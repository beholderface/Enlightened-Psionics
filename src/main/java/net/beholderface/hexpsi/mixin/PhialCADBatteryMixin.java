package net.beholderface.hexpsi.mixin;

import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICADComponent;

@Mixin(ItemMediaBattery.class)
public class PhialCADBatteryMixin implements ICADComponent {
    @Override
    public EnumCADComponent getComponentType(ItemStack itemStack) {
        return itemStack.getItem() == HexItems.BATTERY ? EnumCADComponent.BATTERY : null;
    }

    @Override
    public int getCADStatValue(ItemStack itemStack, EnumCADStat enumCADStat) {
        return enumCADStat == EnumCADStat.OVERFLOW && itemStack.getItem() == HexItems.BATTERY ? 300 : 0;
    }
}
