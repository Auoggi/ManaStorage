package me.auoggi.manastorage;

import me.auoggi.manastorage.screen.BasicImporterMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> menus = DeferredRegister.create(ForgeRegistries.CONTAINERS, ManaStorage.MODID);

    public static final RegistryObject<MenuType<BasicImporterMenu>> basicImporter = menus.register("basic_mana_importer_menu", () -> IForgeMenuType.create(BasicImporterMenu::new));

    public static void register(IEventBus eventBus) {
        menus.register(eventBus);
    }
}
