package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.base.BaseContainerMenu;
import me.auoggi.manastorage.block.entity.storageBlock.ManaStorageBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ManaStorageBlockMenu<E extends ManaStorageBlockEntity> extends BaseContainerMenu<E> {
    public ManaStorageBlockMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        super(id, inventory, 0, ModMenuTypes.manaStorageBlock.get(), friendlyByteBuf);
    }

    public ManaStorageBlockMenu(int id, Inventory inventory, E blockEntity) {
        super(id, inventory, 0, ModMenuTypes.manaStorageBlock.get(), blockEntity);
    }
}
