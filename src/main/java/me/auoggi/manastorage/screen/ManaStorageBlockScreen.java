package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.base.BaseContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ManaStorageBlockScreen extends BaseContainerScreen<ManaStorageBlockMenu> {
    public ManaStorageBlockScreen(ManaStorageBlockMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component, "textures/gui/mana_storage_block.png");
    }
}
