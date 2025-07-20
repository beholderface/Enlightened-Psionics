package net.beholderface.hexpsi.registry;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakePackagedSpell;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.hexpsi.HexPsi;
import net.beholderface.hexpsi.hex.actions.OpReadHelmet;
import net.beholderface.hexpsi.hex.actions.OpReadPsiMeter;
import net.beholderface.hexpsi.hex.actions.OpWriteHelmet;
import net.beholderface.hexpsi.hex.actions.spells.OpMakeTrinketBullet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;

public class HexPsiPatterns {

    private static boolean registered = false;

    public static final DeferredRegister<ActionRegistryEntry> ACTIONS = DeferredRegister.create(IXplatAbstractions.INSTANCE.getActionRegistry().key(), HexPsi.MODID);

    public static final HexPattern WRITE_HELMET = register(HexPattern.fromAngles("ewqweedwwwwqwa", HexDir.NORTH_EAST), "write_helmet", new OpWriteHelmet());
    public static final HexPattern READ_HELMET = register(HexPattern.fromAngles("ewqdaqaewwqwa", HexDir.NORTH_EAST), "read_helmet", new OpReadHelmet());
    public static final HexPattern READ_PSI_METER = register(HexPattern.fromAngles("ewqwdedqwwqwa", HexDir.NORTH_EAST), "read_psi_meter", new OpReadPsiMeter());
    public static final HexPattern CRAFT_TRINKET_BULLET = register(HexPattern.fromAngles("eqqqqqawwdeqdqwdqdwqdqe", HexDir.EAST), "craft_trinket_bullet", new OpMakeTrinketBullet());

    public static HexPattern register(HexPattern pattern, String name, Action action){
        ACTIONS.register(name, ()->{
            return Registry.register(HexActions.REGISTRY, new ResourceLocation(HexPsi.MODID, name), new ActionRegistryEntry(pattern, action));
        });
        return pattern;
    }

    public static boolean isRegistered(){
        return registered;
    }
    public static void notifyRegistration(){
        registered = true;
    }
}
