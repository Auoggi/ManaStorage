package me.auoggi.manastorage.block.entity;

import me.auoggi.manastorage.ModItems;
import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.base.*;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.screen.ExporterMenu;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ModEnergyStorage;
import me.auoggi.manastorage.util.ModItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

public class ExporterEntity extends BaseBlockEntity implements HasEnergyStorage, HasItemStorage {
    private final ModEnergyStorage energyStorage;

    private final ModItemStorage itemStorage = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && stack.getItem() == ModItems.linker.get();
        }
    };

    private final String displayName;
    private final int energyUsage;
    private final long speed;

    public ExporterEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, long speed, int energyUsage, int energyCapacity, String displayName) {
        super(blockEntityType, blockPos, blockState);

        energyStorage = new ModEnergyStorage(energyCapacity) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                ModPackets.sendToClients(new EnergySyncS2C(getEnergyStored(), getBlockPos()));
            }
        };

        this.displayName = displayName;
        this.energyUsage = energyUsage;
        this.speed = speed;
    }

    @Override
    protected CompoundTag saveNbt(CompoundTag nbt) {
        nbt.putInt("Energy", energyStorage.getEnergyStored());
        nbt.put("Inventory", itemStorage.serializeNBT());
        return nbt;
    }

    @Override
    protected void loadNbt(CompoundTag nbt) {
        energyStorage.setEnergy(nbt.getInt("Energy"));
        energyStorage.onEnergyChanged();
        itemStorage.deserializeNBT(nbt.getCompound("Inventory"));
    }

    @Override
    protected <T> LazyOptional<T> returnCapability(Capability<T> cap, Direction side) {
        if(getBlockState().getValue(BlockStateProperties.FACING) != side) {
            if(cap == CapabilityEnergy.ENERGY) {
                return energyStorage.lazy.cast();
            }

            if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return itemStorage.lazy.cast();
            }
        }

        return null;
    }

    @Override
    protected void loadCapabilities() {
        energyStorage.lazy = LazyOptional.of(() -> energyStorage);
        itemStorage.lazy = LazyOptional.of(() -> itemStorage);
    }

    @Override
    protected void invalidateCapabilities() {
        energyStorage.lazy.invalidate();
        itemStorage.lazy.invalidate();
    }

    @Override
    protected void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if(ModBoundItem.of(itemStorage.getStackInSlot(0)) instanceof BaseBoundItem.BoundItem boundItem) {
            GlobalPos boundPos = boundItem.getBindingLoadedAndPowered(level.getServer());

            if(boundPos != null && LevelUtil.getBlockEntity(level.getServer().getLevel(boundPos.dimension()), boundPos.pos()) instanceof CoreEntity core && energyStorage.extractEnergy(energyUsage(), false) >= energyUsage() && core.getManaStorage().getManaStored() != 0) {
                exportMana(level, blockPos, blockState, core.getManaStorage().extractMana(exportMana(level, blockPos, blockState, speed, true), false), false);
            }
        }
    }

    private long exportMana(Level level, BlockPos blockPos, BlockState blockState, long amount, boolean simulate) {
        BlockEntity facing = level.getBlockEntity(blockPos.relative(blockState.getValue(BlockStateProperties.FACING)));
        if(facing instanceof TileEntityFunctionalFlower flower) {
            if(amount == -1) amount = flower.getMaxMana() - flower.getMana();
            amount = Math.min(flower.getMaxMana() - flower.getMana(), amount);
            if(!simulate) {
                flower.addMana((int) amount);
                flower.sync();
            }
            return amount;
        } else if(facing instanceof IManaReceiver manaReceiver && facing instanceof ISparkAttachable receiver) {
            if(amount == -1) amount = receiver.getAvailableSpaceForMana();
            amount = Math.min(receiver.getAvailableSpaceForMana(), amount);
            if(!simulate) manaReceiver.receiveMana((int) amount);
            return amount;
        }

        return 0;
    }

    @Override
    public ModEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public int energyUsage() {
        return energyUsage;
    }

    @Override
    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemStorage.getStackInSlot(0));

        //noinspection DataFlowIssue
        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent(displayName);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ExporterMenu(id, inventory, this);
    }
}
