package me.auoggi.manastorage.util;

import me.auoggi.manastorage.ModCapabilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface ModManaItem {
    long getManaStored(MinecraftServer server);

    double getManaStoredFraction(MinecraftServer server);

    long getFullCapacity(MinecraftServer server);

    long getRemainingCapacity(MinecraftServer server);

    boolean isEmpty(MinecraftServer server);

    boolean isFull(MinecraftServer server);

    long receiveMana(long mana, boolean simulate, MinecraftServer server);

    long extractMana(long mana, boolean simulate, MinecraftServer server);

    boolean canReceiveManaFromPool(BlockEntity pool, MinecraftServer server);

    boolean canReceiveManaFromItem(ItemStack otherStack, MinecraftServer server);

    boolean canExportManaToPool(BlockEntity pool, MinecraftServer server);

    boolean canExportManaToItem(ItemStack otherStack, MinecraftServer server);

    @Nullable
    static ModManaItem of(ItemStack stack) {
        return stack.getCapability(ModCapabilities.manaItem).orElse(null);
    }
}
