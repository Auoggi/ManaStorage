package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.base.BaseContainerMenu;
import me.auoggi.manastorage.block.entity.CoreEntity;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class CoreMenu extends BaseContainerMenu<CoreEntity> {
    public CoreMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        super(id, inventory, 0, ModMenuTypes.core.get(), friendlyByteBuf);
    }

    public CoreMenu(int id, Inventory inventory, CoreEntity blockEntity) {
        super(id, inventory, 0, ModMenuTypes.core.get(), blockEntity);
    }
}
