package me.auoggi.manastorage.base;

import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockEntityBlock extends BaseEntityBlock {
    public static final DirectionProperty facing = BlockStateProperties.FACING;

    protected BaseBlockEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(facing);
    }

    @Nullable
    @Override
    public abstract BlockState getStateForPlacement(BlockPlaceContext context);

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(facing, direction.rotate(state.getValue(facing)));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newBlockState, boolean isMoving) {
        if(blockState.getBlock() != newBlockState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if(blockEntity instanceof HasItemStorage entity) {
                entity.dropContents();
            }
        }
        super.onRemove(blockState, level, blockPos, newBlockState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult hitResult) {
        if(!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if(blockEntity instanceof BaseBlockEntity entity) {
                if(entity instanceof HasEnergyStorage entityWithEnergyStorage) {
                    ModPackets.sendToClients(new EnergySyncS2C(entityWithEnergyStorage.getEnergyStorage().getEnergyStored(), entity.getBlockPos()));
                }

                if(entity instanceof HasManaStorage entityWithManaStorage) {
                    ModPackets.sendToClients(new ManaSyncS2C(entityWithManaStorage.getManaStorage().getManaStored(), entity.getBlockPos()));
                }

                NetworkHooks.openGui((ServerPlayer) player, entity, blockPos);
            } else {
                throw new IllegalStateException("Container provider missing.");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);

    @Nullable
    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType);
}
