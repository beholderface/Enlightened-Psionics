package net.beholderface.hexpsi;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.lib.HexCreativeTabs;
import at.petrak.hexcasting.common.lib.HexRegistries;
import com.mojang.logging.LogUtils;
import net.beholderface.hexpsi.registry.HexPsiItems;
import net.beholderface.hexpsi.registry.HexPsiPatterns;
import net.beholderface.hexpsi.registry.HexPsiPieces;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import vazkii.psi.common.core.PsiCreativeTab;

@Mod(HexPsi.MODID)
public class HexPsi
{
    public static final String MODID = "hexpsi";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<ActionRegistryEntry> ACTIONS = DeferredRegister.create(HexRegistries.ACTION, MODID);

    public HexPsi(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        initRegistries(modEventBus);
        modEventBus.addListener(this::addToCreative);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.COMMON, HexPsiConfig.SPEC);
        HexPsiPieces.init();
    }

    private void addToCreative(BuildCreativeModeTabContentsEvent event){
        Item[] psiTabItems = {HexPsiItems.HELMET_SENSOR_FOCUS.get(), HexPsiItems.TRINKET_SPELL_BULLET.get()};
        Item[] hexTabItems = {HexPsiItems.PSI_CORE_STAFF.get()};
        if (event.getTabKey() == PsiCreativeTab.PSI_CREATIVE_TAB){
            for (Item i : psiTabItems){
                event.accept(i);
            }
        }
        if (event.getTab() == HexCreativeTabs.HEX){
            for (Item i : hexTabItems){
                event.accept(i);
            }
        }
    }

    public static void initRegistries(IEventBus bus){
        bus.addListener((RegisterEvent event) -> {
            if (!HexPsiPatterns.isRegistered()){
                HexPsiPatterns.ACTIONS.register(bus);
                HexPsiPatterns.notifyRegistration();
            }
        });
        HexPsiItems.init(bus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private static MinecraftServer cachedServer = null;
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Happy psexcasting :)");
        cachedServer = event.getServer();
    }

    @Nullable
    public static MinecraftServer getCachedServer(){
        return cachedServer;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
