package net.beholderface.hexpsi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.ISocketable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HexPsiApi {

    public static List<ItemStack> getAllBullets(ItemStack stack){
        List<ItemStack> stackList = new ArrayList<>();
        try {
            ISocketable socketData = getSocketable(stack);
            assert socketData != null;
            for (int i = 0; i < socketData.getLastSlot() + 1; i++){
                ItemStack socketedBullet = socketData.getBulletInSocket(i);
                if (!socketedBullet.isEmpty()){
                    stackList.add(socketData.getBulletInSocket(i));
                }
            }
        } catch (Exception exception){
            //nothing
        }
        return stackList;
    }

    public static List<ItemStack> getAllSocketStacks(ItemStack stack){
        List<ItemStack> stackList = new ArrayList<>();
        try {
            ISocketable socketData = getSocketable(stack);
            assert socketData != null;
            for (int i = 0; i < socketData.getLastSlot() + 1; i++){
                stackList.add(socketData.getBulletInSocket(i));
            }
        } catch (Exception exception){
            //nothing
        }
        return stackList;
    }

    @Nullable
    public static ItemStack getSelectedBullet(ItemStack stack){
        try {
            ISocketable socketData = getSocketable(stack);
            assert socketData != null;
            ItemStack bullet = socketData.getSelectedBullet();
            if (!bullet.isEmpty()){
                return bullet;
            }
        } catch (Exception exception){
            //nothing
        }
        return null;
    }

    public static int getSelectedSlot(ItemStack stack){
        try {
            ISocketable socketData = getSocketable(stack);
            assert socketData != null;
            return socketData.getSelectedSlot();
        } catch (Exception exception){
            return -1;
        }
    }

    @Nullable
    public static ISocketable getSocketable(@NotNull ItemStack stack){
        try {
            return ISocketable.socketable(stack);
        } catch (Exception exception){
            return null;
        }
    }

    public static final String TAG_UUID = "hexpsi:uuid";

    @Nullable
    public static UUID getUUID(ItemStack stack){
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(TAG_UUID)){
            return nbt.getUUID(TAG_UUID);
        } else {
            return null;
        }
    }
}
