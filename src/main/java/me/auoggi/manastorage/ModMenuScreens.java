package me.auoggi.manastorage;

import me.auoggi.manastorage.screen.CoreScreen;
import me.auoggi.manastorage.screen.ExporterScreen;
import me.auoggi.manastorage.screen.ImporterScreen;
import me.auoggi.manastorage.screen.ManaStorageBlockScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModMenuScreens {
    public static void register() {
        MenuScreens.register(ModMenuTypes.core.get(), CoreScreen::new);

        MenuScreens.register(ModMenuTypes.importer.get(), ImporterScreen::new);

        MenuScreens.register(ModMenuTypes.exporter.get(), ExporterScreen::new);

        MenuScreens.register(ModMenuTypes.manaStorageBlock.get(), ManaStorageBlockScreen::new);
    }
}
