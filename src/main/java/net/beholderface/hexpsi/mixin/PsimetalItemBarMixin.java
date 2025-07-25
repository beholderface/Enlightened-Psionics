package net.beholderface.hexpsi.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.utils.MediaHelper;
import net.beholderface.hexpsi.HexPsiApi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.item.tool.ItemPsimetalAxe;
import vazkii.psi.common.item.tool.ItemPsimetalPickaxe;
import vazkii.psi.common.item.tool.ItemPsimetalShovel;
import vazkii.psi.common.item.tool.ItemPsimetalSword;

@Mixin(value = {ItemPsimetalArmor.class, ItemPsimetalPickaxe.class, ItemPsimetalAxe.class, ItemPsimetalShovel.class, ItemPsimetalSword.class})
public class PsimetalItemBarMixin {

    public int getBarColor(ItemStack stack) {
        ItemStack bullet = HexPsiApi.getSelectedBullet(stack);
        if (bullet != null && !bullet.isEmpty()){
            if (bullet.getItem() instanceof MediaHolderItem itemMediaHolder){
                long media = itemMediaHolder.getMedia(bullet);
                long maxMedia = itemMediaHolder.getMaxMedia(bullet);
                return MediaHelper.mediaBarColor(media, maxMedia);
            }
        }
        return Items.IRON_BOOTS.getBarColor(stack);
    }

    public boolean isBarVisible(ItemStack stack){
        boolean hasMedia = false;
        ItemStack bullet = HexPsiApi.getSelectedBullet(stack);
        if (bullet != null && !bullet.isEmpty()){
            if (bullet.getItem() instanceof MediaHolderItem itemMediaHolder){
                hasMedia = itemMediaHolder.getMaxMedia(bullet) > 0;
            }
        }
        return hasMedia || Items.IRON_BLOCK.isBarVisible(stack);
    }
}
