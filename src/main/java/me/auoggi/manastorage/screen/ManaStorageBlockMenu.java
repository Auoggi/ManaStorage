package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.base.BaseContainerMenu;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ManaStorageBlockMenu extends BaseContainerMenu<ManaStorageBlockEntity> {
    public ManaStorageBlockMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        super(id, inventory, false, ModMenuTypes.manaStorageBlock.get(), friendlyByteBuf);
    }

    public ManaStorageBlockMenu(int id, Inventory inventory, ManaStorageBlockEntity blockEntity) {
        super(id, inventory, false, ModMenuTypes.manaStorageBlock.get(), blockEntity);
    }
}
