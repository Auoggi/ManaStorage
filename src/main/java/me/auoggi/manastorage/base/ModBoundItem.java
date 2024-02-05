package me.auoggi.manastorage.base;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.ICoordBoundItem;

import javax.annotation.Nullable;

public interface ModBoundItem extends ICoordBoundItem {
    GlobalPos getBinding();

    @Nullable
    static ModBoundItem of(ItemStack stack) {
        return stack.getCapability(BotaniaForgeCapabilities.COORD_BOUND_ITEM).orElse(null) instanceof ModBoundItem modBoundItem ? modBoundItem : null;
    }
}
