package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.base.BaseContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ImporterScreen extends BaseContainerScreen<ImporterMenu> {
    public ImporterScreen(ImporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component, "textures/gui/importer.png");
    }
}
