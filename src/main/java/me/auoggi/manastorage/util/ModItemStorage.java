package me.auoggi.manastorage.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class ModItemStorage extends ItemStackHandler {
    public LazyOptional<IItemHandler> lazy = LazyOptional.empty();

    public ModItemStorage(int slots) {
        super(slots);
    }

    @Override
    protected abstract void onContentsChanged(int slot);

    @Override
    public abstract boolean isItemValid(int slot, @NotNull ItemStack stack);
}
