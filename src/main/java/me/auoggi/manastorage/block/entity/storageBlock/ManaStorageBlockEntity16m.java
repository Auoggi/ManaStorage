package me.auoggi.manastorage.block.entity.storageBlock;

import me.auoggi.manastorage.ModBlockEntities;
import me.auoggi.manastorage.screen.ManaStorageBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaStorageBlockEntity16m extends ManaStorageBlockEntity {
    public ManaStorageBlockEntity16m(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.manaStorageBlock1m.get(), blockPos, blockState, "block.manastorage.16m_mana_storage_block", 16000000);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ManaStorageBlockMenu<ManaStorageBlockEntity16m>(id, inventory, this);
    }
}