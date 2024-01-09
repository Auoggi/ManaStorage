package me.auoggi.manastorage.util;

import me.auoggi.manastorage.ModCapabilities;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.item.ICoordBoundItem;

import javax.annotation.Nullable;

public interface ModBoundItem extends ICoordBoundItem {
    GlobalPos getBinding();

    @Nullable
    static ModBoundItem of(ItemStack stack) {
        return stack.getCapability(ModCapabilities.boundItem).orElse(null);
    }
}
