package me.auoggi.manastorage.block;

import me.auoggi.manastorage.base.BaseBlockEntityBlock;
import me.auoggi.manastorage.block.entity.ManaStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaStorageBlock extends BaseBlockEntityBlock {
    private final BlockEntityType<ManaStorageBlockEntity> blockEntityType;
    private final String displayName;
    private final long capacity;

    public ManaStorageBlock(Properties properties, BlockEntityType<ManaStorageBlockEntity> blockEntityType, String displayName, long capacity) {
        super(properties);

        this.blockEntityType = blockEntityType;
        this.displayName = displayName;
        this.capacity = capacity;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ManaStorageBlockEntity(blockEntityType, blockPos, blockState, displayName, capacity);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return null;
    }
}
