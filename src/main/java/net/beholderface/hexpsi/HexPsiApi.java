package net.beholderface.hexpsi;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import net.beholderface.hexpsi.mixin.CADInvokerMixin;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;

import java.util.ArrayList;
import java.util.List;

public class HexPsiApi {

    public static List<ItemStack> getAllBullets(ItemStack cadStack){
        List<ItemStack> stackList = new ArrayList<>();
        if (cadStack.getItem() instanceof ICAD icad){
            ISocketable socketData = ((CADInvokerMixin) icad).publicGetSocketable(cadStack);
            for (int i = 0; i < socketData.getLastSlot() + 1; i++){
                ItemStack socketedBullet = socketData.getBulletInSocket(i);
                if (!socketedBullet.isEmpty()){
                    stackList.add(socketData.getBulletInSocket(i));
                }
            }
        }
        return stackList;
    }

    public static List<ItemStack> getAllSocketStacks(ItemStack cadStack){
        List<ItemStack> stackList = new ArrayList<>();
        if (cadStack.getItem() instanceof ICAD icad){
            ISocketable socketData = ((CADInvokerMixin) icad).publicGetSocketable(cadStack);
            for (int i = 0; i < socketData.getLastSlot() + 1; i++){
                stackList.add(socketData.getBulletInSocket(i));
            }
        }
        return stackList;
    }

    @Nullable
    public static ItemStack getSelectedBullet(ItemStack cadStack){
        if (cadStack.getItem() instanceof ICAD icad){
            ISocketable socketData = ((CADInvokerMixin) icad).publicGetSocketable(cadStack);
            ItemStack bullet = socketData.getSelectedBullet();
            if (!bullet.isEmpty()){
                return bullet;
            }
        }
        return null;
    }

    public static int getSelectedSlot(ItemStack cadStack){
        if (cadStack.getItem() instanceof ICAD icad){
            ISocketable socketData = ((CADInvokerMixin) icad).publicGetSocketable(cadStack);
            return socketData.getSelectedSlot();
        } else {
            return -1;
        }
    }
}
