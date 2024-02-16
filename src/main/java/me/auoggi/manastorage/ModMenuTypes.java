package me.auoggi.manastorage;

import me.auoggi.manastorage.screen.CoreMenu;
import me.auoggi.manastorage.screen.ExporterMenu;
import me.auoggi.manastorage.screen.ImporterMenu;
import me.auoggi.manastorage.screen.ManaStorageBlockMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> menus = DeferredRegister.create(ForgeRegistries.CONTAINERS, ManaStorage.MODID);

    public static final RegistryObject<MenuType<CoreMenu>> core = menus.register("storage_core", () -> IForgeMenuType.create(CoreMenu::new));
    public static final RegistryObject<MenuType<ImporterMenu>> importer = menus.register("importer", () -> IForgeMenuType.create(ImporterMenu::new));
    public static final RegistryObject<MenuType<ExporterMenu>> exporter = menus.register("exporter", () -> IForgeMenuType.create(ExporterMenu::new));
    public static final RegistryObject<MenuType<ManaStorageBlockMenu>> manaStorageBlock = menus.register("mana_storage_block", () -> IForgeMenuType.create(ManaStorageBlockMenu::new));

    public static void register(IEventBus eventBus) {
        menus.register(eventBus);
    }
}
