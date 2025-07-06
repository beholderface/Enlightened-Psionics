package net.beholderface.hexpsi.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import net.beholderface.hexpsi.HexPsiApi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.common.item.ItemCAD;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemCAD.class)
public abstract class MediaCADMixin implements MediaHolderItem {

    @Shadow public abstract ItemStack getComponentInSlot(ItemStack stack, EnumCADComponent type);

    @Override
    public long getMedia(ItemStack itemStack) {
        ICAD cadItem = (ICAD) itemStack.getItem();
        ItemStack batteryStack = cadItem.getComponentInSlot(itemStack, EnumCADComponent.BATTERY);
        long foundMedia = 0L;
        boolean checkAll = false;
        if (batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
            foundMedia += mediaHolderItem.getMedia(batteryStack);
            checkAll = true;
        }
        if (checkAll){
            for (ItemStack stack : HexPsiApi.getAllBullets(itemStack)){
                if (stack.getItem() instanceof MediaHolderItem mediaHolderItem){
                    foundMedia += mediaHolderItem.getMedia(stack);
                }
            }
        } else {
            ItemStack socketedBullet = HexPsiApi.getSelectedBullet(itemStack);
            if (socketedBullet != null && socketedBullet.getItem() instanceof MediaHolderItem mediaHolderItem){
                foundMedia += mediaHolderItem.getMedia(socketedBullet);
            }
        }
        return foundMedia;
    }

    @Override
    public long getMaxMedia(ItemStack itemStack) {
        ICAD cadItem = (ICAD) itemStack.getItem();
        ItemStack batteryStack = cadItem.getComponentInSlot(itemStack, EnumCADComponent.BATTERY);
        long foundMedia = 0L;
        boolean checkAll = false;
        if (batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
            foundMedia += mediaHolderItem.getMaxMedia(batteryStack);
            checkAll = true;
        }
        if (checkAll){
            for (ItemStack stack : HexPsiApi.getAllBullets(itemStack)){
                if (stack.getItem() instanceof MediaHolderItem mediaHolderItem){
                    foundMedia += mediaHolderItem.getMaxMedia(stack);
                }
            }
        } else {
            ItemStack socketedBullet = HexPsiApi.getSelectedBullet(itemStack);
            if (socketedBullet != null && socketedBullet.getItem() instanceof MediaHolderItem mediaHolderItem){
                foundMedia += mediaHolderItem.getMaxMedia(socketedBullet);
            }
        }
        return foundMedia;
    }

    @Override
    public void setMedia(ItemStack itemStack, long l) {

    }

    @Override
    public long withdrawMedia(ItemStack stack, long cost, boolean simulate) {
        //only allow withdrawing from the phial, not the bullets
        long mediaHere = 0L;
        ICAD cadItem = (ICAD) stack.getItem();
        ItemStack batteryStack = cadItem.getComponentInSlot(stack, EnumCADComponent.BATTERY);
        MediaHolderItem holderItem = null;
        if (batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
            mediaHere += mediaHolderItem.getMedia(batteryStack);
            holderItem = mediaHolderItem;
        }
        if (cost < 0L) {
            cost = mediaHere;
        }

        if (!simulate) {
            long mediaLeft = mediaHere - cost;
            if (holderItem != null){
                holderItem.setMedia(batteryStack, mediaLeft);
            }
        }

        return Math.min(cost, mediaHere);
    }

    public long insertMedia(ItemStack stack, long amount, boolean simulate) {
        //insert to bullets before charging phial
        long mediaHere = this.getMedia(stack);
        long emptySpace = this.getMaxMedia(stack) - mediaHere;
        if (emptySpace <= 0L) {
            return 0L;
        } else {
            if (amount < 0L) {
                amount = emptySpace;
            }

            long inserting = Math.min(amount, emptySpace);
            if (!simulate) {
                long currentRemaining = inserting;
                ItemStack batteryStack = this.getComponentInSlot(stack, EnumCADComponent.BATTERY);
                List<ItemStack> stacksToCharge = new ArrayList<>();
                stacksToCharge.add(HexPsiApi.getSelectedBullet(stack));
                if (batteryStack.getItem() instanceof MediaHolderItem){
                    List<ItemStack> allBullets = HexPsiApi.getAllBullets(stack);
                    //selected bullet should be prioritized
                    allBullets.remove(HexPsiApi.getSelectedSlot(stack));
                    stacksToCharge.addAll(allBullets);
                    stacksToCharge.add(batteryStack);
                }
                for (ItemStack charged : stacksToCharge){
                    if (charged.getItem() instanceof MediaHolderItem mediaHolderItem){
                        currentRemaining -= mediaHolderItem.insertMedia(charged, currentRemaining, false);
                        if (currentRemaining <= 0){
                            break;
                        }
                    }
                }
            }
            return inserting;
        }
    }

    @Override
    public boolean canProvideMedia(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack itemStack) {
        boolean hasPhialBattery = itemStack.getItem() instanceof ICAD icad && icad.getComponentInSlot(itemStack, EnumCADComponent.BATTERY).getItem() instanceof MediaHolderItem;
        ItemStack bulletStack = HexPsiApi.getSelectedBullet(itemStack);
        boolean hasRechargeableBulletSelected = bulletStack != null && bulletStack.getItem() instanceof MediaHolderItem;
        return hasPhialBattery || hasRechargeableBulletSelected;
    }

    public int getBarColor(ItemStack stack) {
        ItemStack batteryStack = this.getComponentInSlot(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof ItemMediaHolder itemMediaHolder){
            long media = itemMediaHolder.getMedia(batteryStack);
            long maxMedia = itemMediaHolder.getMaxMedia(batteryStack);
            return MediaHelper.mediaBarColor(media, maxMedia);
        }
        return 0;
    }

    public int getBarWidth(ItemStack stack) {
        ItemStack batteryStack = this.getComponentInSlot(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof ItemMediaHolder itemMediaHolder){
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
