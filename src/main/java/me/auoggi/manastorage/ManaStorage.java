package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.packet.ManaStorageCoreClientDataS2C;
import me.auoggi.manastorage.screen.BasicImporterScreen;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ManaStorageCoreClientData;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//TODO give gui texture outline of Network Linker
@Mod("manastorage")
public class ManaStorage {
    public static final String MODID = "manastorage";

    private static final List<GlobalPos> loadedBlockEntities = new ArrayList<>();

    public static final List<GlobalPos> pendingLoadedBlockEntities = new ArrayList<>();

    public static Map<GlobalPos, ManaStorageCoreClientData> coreClientDataMap = new HashMap<>();

    private static Map<GlobalPos, ManaStorageCoreClientData> coreServerDataMap = new HashMap<>();

    public static final Map<GlobalPos, ManaStorageCoreClientData> pendingCoreServerDataMap = new HashMap<>();

    public static final int importerSpeed = 400;
    public static final int exporterSpeed = 800;
    public static final int basicEnergyUsage = 1600;
    public static final int advancedEnergyUsage = 16000;
    public static final int basicEnergyCapacity = basicEnergyUsage * 100;
    public static final int advancedEnergyCapacity = advancedEnergyUsage * 100;

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
    public void tick(@NotNull TickEvent.WorldTickEvent event) {
        //Make sure we only execute once and at the end of every tick and only when we are on the server
        if(event.phase.equals(TickEvent.Phase.END) && event.world instanceof ServerLevel serverLevel && serverLevel.dimension().toString().equals("ResourceKey[minecraft:dimension / minecraft:overworld]")) {
            if(!pendingLoadedBlockEntities.isEmpty()) {
                loadedBlockEntities.addAll(pendingLoadedBlockEntities);
                pendingLoadedBlockEntities.clear();
            }
            if(!pendingCoreServerDataMap.isEmpty()) {
                coreServerDataMap.putAll(pendingCoreServerDataMap);
                pendingCoreServerDataMap.clear();
            }

            Map<GlobalPos, ManaStorageCoreClientData> tempCoreServerDataMap = coreServerDataMap; //Create a temporary map because removing elements from a map while iterating over it is unsafe

            for(Map.Entry<GlobalPos, ManaStorageCoreClientData> entry : coreServerDataMap.entrySet()) {
                ServerLevel level = serverLevel.getServer().getLevel(entry.getKey().dimension());
                if(level == null) continue;

                if(!(LevelUtil.getBlockEntity(level, entry.getKey().pos()) instanceof BasicImporterBlockEntity) || !loadedBlockEntities.contains(entry.getKey())) {
                    tempCoreServerDataMap.remove(entry.getKey());
                }
            }
            coreServerDataMap = tempCoreServerDataMap;
            loadedBlockEntities.clear();
            ModPackets.sendToClients(new ManaStorageCoreClientDataS2C(coreServerDataMap));
        }
    }
}