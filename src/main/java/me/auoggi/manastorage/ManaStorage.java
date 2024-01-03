package me.auoggi.manastorage;

import me.auoggi.manastorage.screen.BasicImporterScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

//TODO give gui texture outline of Network Linker
@Mod("manastorage")
public class ManaStorage {
    public static final String MODID = "manastorage";

    public static final int importerSpeed = 400;
    public static final int exporterSpeed = 800;
    public static final int basicEnergyUsage = 1600;
    public static final int advancedEnergyUsage = 16000;
    public static final int basicEnergyCapacity = basicEnergyUsage * 200;
    public static final int advancedEnergyCapacity = advancedEnergyUsage * 200;

    public static MinecraftServer server;

    public ManaStorage() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlockEntities.register(eventBus);
        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModMenuTypes.register(eventBus);

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModPackets.register();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenuTypes.basicImporter.get(), BasicImporterScreen::new);
    }

    @SubscribeEvent
    public void tick(final TickEvent.WorldTickEvent event) {
        MinecraftServer server = event.world.getServer();
        if(!event.world.isClientSide && server != null) ManaStorage.server = server;
    }
}
