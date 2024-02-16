package me.auoggi.manastorage;

import me.auoggi.manastorage.base.ModManaItem;
import me.auoggi.manastorage.block.entity.CoreEntity;
import me.auoggi.manastorage.packet.CoreDataSyncS2C;
import me.auoggi.manastorage.util.CoreData;
import me.auoggi.manastorage.util.LevelUtil;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.client.gui.ManaBarTooltipComponent;

import java.util.*;

@Mod("manastorage")
public class ManaStorage {
    public static final String MODID = "manastorage";

    public static final Map<String, Map<BlockPos, CoreData>> clientCoreData = new HashMap<>();
    public static final Map<ResourceKey<Level>, List<BlockPos>> loadedBlockEntities = new HashMap<>();
    public static final Map<ResourceKey<Level>, List<BlockPos>> pendingLoadedBlockEntities = new HashMap<>();
    public static final Map<ResourceKey<Level>, Map<BlockPos, CoreData>> pendingCoreData = new HashMap<>();

    public static final long basicSpeed = 320;
    public static final int basicEnergyUsage = 16000;
    public static final int advancedEnergyUsage = basicEnergyUsage * 10;
    public static final int basicEnergyCapacity = basicEnergyUsage * 100;
    public static final int advancedEnergyCapacity = advancedEnergyUsage * 100;

    public ManaStorage() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModPackets.register();
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ModMenuScreens.register();

        //Make tooltip image render if ItemStack has ModManaItem capability
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, (RenderTooltipEvent.Color e) -> {
            if(ModManaItem.of(e.getItemStack()) != null) {
                int width = 0;
                ManaBarTooltipComponent manaBar = null;
                for(ClientTooltipComponent component : e.getComponents()) {
                    width = Math.max(width, component.getWidth(e.getFont()));
                    if(component instanceof ManaBarTooltipComponent c) {
                        manaBar = c;
                    }
                }
                if(manaBar != null) {
                    manaBar.setContext(e.getX(), e.getY(), width);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void tick(final TickEvent.WorldTickEvent event) {
        //Make sure we only execute at the end of every tick and only when we are on the server
        if(event.phase.equals(TickEvent.Phase.END) && event.world instanceof ServerLevel serverLevel) {
            ResourceKey<Level> dimension = serverLevel.dimension();

            List<BlockPos> loadedBlockEntities = new ArrayList<>();
            if(pendingLoadedBlockEntities.containsKey(dimension)) {
                loadedBlockEntities.addAll(pendingLoadedBlockEntities.get(dimension));
                pendingLoadedBlockEntities.remove(dimension);
            }

            Map<BlockPos, CoreData> serverCoreData = new HashMap<>();
            if(pendingCoreData.containsKey(dimension)) {
                serverCoreData.putAll(pendingCoreData.get(dimension));
                pendingCoreData.remove(dimension);
            }

            //Clone entrySet from serverCoreData as to not iterate over a set we are modifying
            Set<Map.Entry<BlockPos, CoreData>> entrySet = new HashSet<>();
            if(!serverCoreData.isEmpty()) entrySet.addAll(serverCoreData.entrySet());

            for(Map.Entry<BlockPos, CoreData> entry : entrySet) {
                if(!(LevelUtil.getBlockEntity(serverLevel, entry.getKey()) instanceof CoreEntity) || !loadedBlockEntities.contains(entry.getKey())) {
                    serverCoreData.remove(entry.getKey());
                }
            }
            ModPackets.sendToClients(new CoreDataSyncS2C(dimension.toString(), serverCoreData));
            ManaStorage.loadedBlockEntities.put(dimension, loadedBlockEntities);
        }
    }
}