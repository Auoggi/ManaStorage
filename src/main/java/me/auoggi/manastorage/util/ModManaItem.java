package me.auoggi.manastorage.util;

import me.auoggi.manastorage.ModCapabilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface ModManaItem {
    boolean isEmpty(MinecraftServer server);

    boolean isFull(MinecraftServer server);

    int receiveMana(int mana, boolean simulate, MinecraftServer server);

    int extractMana(int mana, boolean simulate, MinecraftServer server);

    boolean canReceiveManaFromPool(BlockEntity pool, MinecraftServer server);

    boolean canReceiveManaFromItem(ItemStack otherStack, MinecraftServer server);

    boolean canExportManaToPool(BlockEntity pool, MinecraftServer server);

    boolean canExportManaToItem(ItemStack otherStack, MinecraftServer server);

    @Nullable
    static ModManaItem of(ItemStack stack) {
        return stack.getCapability(ModCapabilities.manaItem).orElse(null);
    }
}
