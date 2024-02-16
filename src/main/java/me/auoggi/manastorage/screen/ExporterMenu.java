package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.base.BaseContainerMenu;
import me.auoggi.manastorage.block.entity.ExporterEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ExporterMenu extends BaseContainerMenu<ExporterEntity> {
    public ExporterMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        super(id, inventory, true, ModMenuTypes.exporter.get(), friendlyByteBuf);
    }

    public ExporterMenu(int id, Inventory inventory, ExporterEntity blockEntity) {
        super(id, inventory, true, ModMenuTypes.exporter.get(), blockEntity);
    }
}
