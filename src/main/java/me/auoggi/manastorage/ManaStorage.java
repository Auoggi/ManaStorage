package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.packet.ManaStorageCoreClientDataS2C;
import me.auoggi.manastorage.screen.BasicImporterScreen;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ManaStorageCoreClientData;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO give gui texture outline of Network Linker
@Mod("manastorage")
public class ManaStorage {
    public static final String MODID = "manastorage";

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
        if(!event.world.isClientSide && event.world instanceof ServerLevel serverLevel) {
            List<GlobalPos> loadedBlockEntities = new ArrayList<>();

            if(!pendingLoadedBlockEntities.isEmpty()) {
                loadedBlockEntities.addAll(pendingLoadedBlockEntities);
                pendingLoadedBlockEntities.clear();
            }

            if(!pendingCoreServerDataMap.isEmpty()) {
                coreServerDataMap.putAll(pendingCoreServerDataMap);
                pendingCoreServerDataMap.clear();
            }

            Map<GlobalPos, ManaStorageCoreClientData> tempCoreServerDataMap = coreServerDataMap; //Create temp map because removing elements from map while iterating over it is not possible

            for(Map.Entry<GlobalPos, ManaStorageCoreClientData> entry : coreClientDataMap.entrySet()) {
                ServerLevel level = serverLevel.getServer().getLevel(entry.getKey().dimension());
                if(level == null) continue;

                if(!(LevelUtil.getBlockEntity(level, entry.getKey().pos()) instanceof BasicImporterBlockEntity) || !loadedBlockEntities.contains(entry.getKey())) {
                    tempCoreServerDataMap.remove(entry.getKey());
                }
            }
            coreServerDataMap = tempCoreServerDataMap;
            ModPackets.sendToClients(new ManaStorageCoreClientDataS2C(coreServerDataMap));
        }
    }
}
