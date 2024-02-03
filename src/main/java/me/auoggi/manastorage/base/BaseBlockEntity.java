package me.auoggi.manastorage.base;

import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntity extends BlockEntity implements MenuProvider {
    public BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        saveNbt(nbt);
        super.saveAdditional(nbt);
    }

    protected abstract void saveNbt(CompoundTag nbt);

    @Override
    public void load(@NotNull CompoundTag nbt) {
        loadNbt(nbt);
        super.load(nbt);
    }

    protected abstract void loadNbt(CompoundTag nbt);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        LazyOptional<T> capability = returnCapability(cap, side);
        return capability != null ? capability : super.getCapability(cap, side);
    }

    protected abstract <T> LazyOptional<T> returnCapability(Capability<T> cap, Direction side);

    @Override
    public void onLoad() {
        loadCapabilities();
        super.onLoad();
    }

    protected abstract void loadCapabilities();

    @Override
    public void invalidateCaps() {
        invalidateCapabilities();
        super.invalidateCaps();
    }

    protected abstract void invalidateCapabilities();

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BaseBlockEntity blockEntity) {
        blockEntity.tick(level, blockPos, blockState);
    }

    protected abstract void tick(Level level, BlockPos blockPos, BlockState blockState);
}
