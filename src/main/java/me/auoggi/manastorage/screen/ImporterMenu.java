package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.base.BaseContainerMenu;
import me.auoggi.manastorage.block.entity.ImporterEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ImporterMenu extends BaseContainerMenu<ImporterEntity> {
    public ImporterMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        super(id, inventory, true, ModMenuTypes.importer.get(), friendlyByteBuf);
    }

    public ImporterMenu(int id, Inventory inventory, ImporterEntity blockEntity) {
        super(id, inventory, true, ModMenuTypes.importer.get(), blockEntity);
    }
}
