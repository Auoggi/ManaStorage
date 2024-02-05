package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.base.BaseContainerScreen;
import me.auoggi.manastorage.util.EnergyInfoArea;
import me.auoggi.manastorage.util.ManaInfoArea;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CoreScreen extends BaseContainerScreen<CoreMenu> {
    public CoreScreen(CoreMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component, "textures/gui/storage_core.png");
    }

    @Override
    protected void assignInfoAreas() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        energyInfoArea = new EnergyInfoArea(x + 149, y + 12, menu.getBlockEntity().getEnergyStorage());
        manaInfoArea = new ManaInfoArea(x + 12, y + 12, menu.getBlockEntity().position());
    }
}
