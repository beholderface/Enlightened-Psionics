package net.beholderface.hexpsi.items;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import net.beholderface.hexpsi.HexPsi;
import net.beholderface.hexpsi.HexPsiApi;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ISocketedMediaItem extends MediaHolderItem {

    public default ItemStack getComponentStack(ItemStack stack, EnumCADComponent component){
        if (stack.getItem() instanceof ICAD cad){
            return cad.getComponentInSlot(stack, component);
        }
        ItemStack componentStack = ItemStack.EMPTY;
        Player foundPlayer = null;
        if (stack.getEntityRepresentation() instanceof Player player){
            foundPlayer = player;
        } else {
            if (HexPsi.getCachedServer() != null){
                UUID uuid = HexPsiApi.getUUID(stack);
                if (uuid != null){
                    foundPlayer = HexPsi.getCachedServer().getPlayerList().getPlayer(uuid);
                }
            } else {
                foundPlayer = Minecraft.getInstance().player;
            }
        }
        if (foundPlayer != null){
            ItemStack cadStack = stack.getItem() instanceof ICAD ? stack : PsiAPI.getPlayerCAD(foundPlayer);
            if (!cadStack.isEmpty()){
                ICAD cadItem = (ICAD) cadStack.getItem();
                componentStack = cadItem.getComponentInSlot(cadStack, component);
            }
        }
        return componentStack;
    }

    @Override
    default long getMedia(ItemStack stack) {
        long foundMedia = 0L;
        boolean checkAll = false;
        ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
        if (batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
            foundMedia += mediaHolderItem.getMedia(batteryStack);
            checkAll = true;
        }
        if (checkAll){
            for (ItemStack checkedStack : HexPsiApi.getAllBullets(stack)){
                if (checkedStack.getItem() instanceof MediaHolderItem mediaHolderItem){
                    foundMedia += mediaHolderItem.getMedia(checkedStack);
                }
            }
        } else {
            ItemStack socketedBullet = HexPsiApi.getSelectedBullet(stack);
            if (socketedBullet != null && socketedBullet.getItem() instanceof MediaHolderItem mediaHolderItem){
                foundMedia += mediaHolderItem.getMedia(socketedBullet);
            }
        }
        return foundMedia;
    }

    @Override
    default long getMaxMedia(ItemStack stack) {
        ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
        long foundMedia = 0L;
        boolean checkAll = false;
        if (batteryStack.getItem() instanceof MediaHolderItem mediaHolderItem){
            foundMedia += mediaHolderItem.getMaxMedia(batteryStack);
            checkAll = true;
        }
        if (checkAll){
            for (ItemStack checkedStack : HexPsiApi.getAllBullets(stack)){
                if (checkedStack.getItem() instanceof MediaHolderItem mediaHolderItem){
                    foundMedia += mediaHolderItem.getMaxMedia(checkedStack);
                }
            }
        } else {
            ItemStack socketedBullet = HexPsiApi.getSelectedBullet(stack);
            if (socketedBullet != null && socketedBullet.getItem() instanceof MediaHolderItem mediaHolderItem){
                foundMedia += mediaHolderItem.getMaxMedia(socketedBullet);
            }
        }
        return foundMedia;
    }

    @Override
    default long withdrawMedia(ItemStack stack, long cost, boolean simulate) {
        //only allow withdrawing from the phial, not the bullets
        long mediaHere = 0L;
        ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
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

    @Override
    default long insertMedia(ItemStack stack, long amount, boolean simulate) {
        //insert to bullets before charging phial
        long mediaHere = this.getMedia(stack);
        long emptySpace = this.getMaxMedia(stack) - mediaHere;
        HexPsi.LOGGER.info("stack contains " + mediaHere + ", out of a maximum of " + this.getMaxMedia(stack));
        if (emptySpace <= 0L) {
            return 0L;
        } else {
            if (amount < 0L) {
                amount = emptySpace;
            }

            long inserting = Math.min(amount, emptySpace);
            if (!simulate) {
                long currentRemaining = inserting;
                ItemStack batteryStack = this.getComponentStack(stack, EnumCADComponent.BATTERY);
                List<ItemStack> stacksToCharge = new ArrayList<>();
                ItemStack selectedBullet = HexPsiApi.getSelectedBullet(stack);
                if (selectedBullet != null){
                    stacksToCharge.add(selectedBullet);
                }
                if (batteryStack.getItem() instanceof MediaHolderItem){
                    List<ItemStack> allBullets = HexPsiApi.getAllSocketStacks(stack);
                    //selected bullet should be prioritized
                    int selected = HexPsiApi.getSelectedSlot(stack);
                    if (selected >= 0){
                        allBullets.remove(selected);
                    }
                    stacksToCharge.addAll(allBullets);
                    stacksToCharge.add(batteryStack);
                }
                for (ItemStack charged : stacksToCharge){
                    if (charged != null && (!charged.isEmpty()) && charged.getItem() instanceof MediaHolderItem mediaHolderItem){
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
    default void setMedia(ItemStack itemStack, long l) {
        //the media should always be in sockets/component slots, not in the main itemstack
    }

    @Override
    default boolean canProvideMedia(ItemStack itemStack) {
        return false;
    }

    default boolean canAccessPhial(ItemStack stack){
        return this.getComponentStack(stack, EnumCADComponent.BATTERY).getItem() instanceof MediaHolderItem;
    }

    @Override
    default boolean canRecharge(ItemStack stack) {
        return true;
    }
}
