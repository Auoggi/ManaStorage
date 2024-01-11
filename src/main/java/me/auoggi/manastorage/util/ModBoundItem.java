package me.auoggi.manastorage.util;

import net.minecraft.core.GlobalPos;
import vazkii.botania.api.item.ICoordBoundItem;

public interface ModBoundItem extends ICoordBoundItem {
    GlobalPos getBinding();
}
