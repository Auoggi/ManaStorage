package me.auoggi.manastorage;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, ManaStorage.MODID);

    public static final RegistryObject<Item> testItem = items.register("test_item",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.CREATIVE_MODE_TAB)));

    public static void register(IEventBus eventBus) {
        items.register(eventBus);
    }
}
