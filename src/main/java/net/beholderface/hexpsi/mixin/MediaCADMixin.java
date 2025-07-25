package net.beholderface.hexpsi.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.utils.MediaHelper;
import net.beholderface.hexpsi.items.ISocketedMediaItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.common.item.ItemCAD;

@Mixin(ItemCAD.class)
public abstract class MediaCADMixin implements ISocketedMediaItem {

    public int getBarColor(ItemStack stack) {
        ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof MediaHolderItem itemMediaHolder){
            long media = itemMediaHolder.getMedia(batteryStack);
            long maxMedia = itemMediaHolder.getMaxMedia(batteryStack);
            return MediaHelper.mediaBarColor(media, maxMedia);
        }
        return 0;
    }

    public int getBarWidth(ItemStack stack) {
        ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof MediaHolderItem itemMediaHolder){
            long media = itemMediaHolder.getMedia(batteryStack);
            long maxMedia = itemMediaHolder.getMaxMedia(batteryStack);
            return MediaHelper.mediaBarWidth(media, maxMedia);
        }
        return 0;
    }

    public boolean isBarVisible(ItemStack stack){
        return this.getMaxMedia(stack) > 0L;
    }
}
