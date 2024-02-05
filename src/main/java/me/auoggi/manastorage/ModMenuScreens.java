package me.auoggi.manastorage;

import me.auoggi.manastorage.screen.BasicImporterScreen;
import me.auoggi.manastorage.screen.CoreScreen;
import me.auoggi.manastorage.screen.ManaStorageBlockScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModMenuScreens {
    public static void register() {
        MenuScreens.register(ModMenuTypes.basicImporter.get(), BasicImporterScreen::new);

        MenuScreens.register(ModMenuTypes.core.get(), CoreScreen::new);

        MenuScreens.register(ModMenuTypes.manaStorageBlock.get(), ManaStorageBlockScreen::new);
    }
}
