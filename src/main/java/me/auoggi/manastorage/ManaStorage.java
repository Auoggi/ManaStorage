package me.auoggi.manastorage;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.packet.ManaStorageCoreClientDataS2C;
import me.auoggi.manastorage.screen.BasicImporterScreen;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ManaStorageCoreClientData;
import me.auoggi.manastorage.util.ModManaItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.NotNull;
import vazkii.botania.client.gui.ManaBarTooltipComponent;

import java.util.*;

//TODO give gui texture outline of Network Linker
@Mod("manastorage")
public class ManaStorage {
    public static final String MODID = "manastorage";

    public static Map<GlobalPos, ManaStorageCoreClientData> coreClientDataMap = new HashMap<>();

    public static final List<GlobalPos> pendingLoadedBlockEntities = new ArrayList<>();

    public static final Map<GlobalPos, ManaStorageCoreClientData> pendingCoreServerDataMap = new HashMap<>();

    public static final int basicSpeed = 320;
    public static final int basicEnergyUsage = 16000;
    public static final int advancedEnergyUsage = 160000;
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

        //Make tooltip image render if ItemStack has ModManaItem capability
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, (RenderTooltipEvent.Color e) -> {
            if (ModManaItem.of(e.getItemStack()) != null) {
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

    @SubscribeEvent
    public void tick(@NotNull TickEvent.WorldTickEvent event) {
        //Make sure we only execute once and at the end of every tick and only when we are on the server
        if(event.phase.equals(TickEvent.Phase.END) && event.world instanceof ServerLevel serverLevel && serverLevel.dimension().toString().equals("ResourceKey[minecraft:dimension / minecraft:overworld]")) {
            List<GlobalPos> loadedBlockEntities = new ArrayList<>();
            if(!pendingLoadedBlockEntities.isEmpty()) {
                loadedBlockEntities.addAll(pendingLoadedBlockEntities);
                pendingLoadedBlockEntities.clear();
            }

            Map<GlobalPos, ManaStorageCoreClientData> coreServerDataMap = new HashMap<>();
            if(!pendingCoreServerDataMap.isEmpty()) {
                coreServerDataMap.putAll(pendingCoreServerDataMap);
                pendingCoreServerDataMap.clear();
            }

            //Clone coreServerDataMap and make entrySet from clone as to not iterate over a set we are modifying
            Set<Map.Entry<GlobalPos, ManaStorageCoreClientData>> entrySet = new HashSet<>();
            if(!coreServerDataMap.isEmpty()) {
                Map<GlobalPos, ManaStorageCoreClientData> map = new HashMap<>(coreServerDataMap);
                entrySet.addAll(map.entrySet());
            }

            for(Map.Entry<GlobalPos, ManaStorageCoreClientData> entry : entrySet) {
                ServerLevel level = serverLevel.getServer().getLevel(entry.getKey().dimension());
                if(level == null) continue;

                if(!(LevelUtil.getBlockEntity(level, entry.getKey().pos()) instanceof BasicImporterBlockEntity) || !loadedBlockEntities.contains(entry.getKey())) {
                    coreServerDataMap.remove(entry.getKey());
                }
            }
            ModPackets.sendToClients(new ManaStorageCoreClientDataS2C(coreServerDataMap));
        }
    }
}