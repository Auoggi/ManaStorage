package me.auoggi.manastorage.block.entity;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModBlockEntities;
import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.base.BaseBlockEntity;
import me.auoggi.manastorage.base.HasEnergyStorage;
import me.auoggi.manastorage.base.HasManaStorage;
import me.auoggi.manastorage.base.ModBindable;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.screen.CoreMenu;
import me.auoggi.manastorage.util.CoreData;
import me.auoggi.manastorage.util.LevelUtil;
import me.auoggi.manastorage.util.ModEnergyStorage;
import me.auoggi.manastorage.util.ModManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CoreEntity extends BaseBlockEntity implements HasEnergyStorage, HasManaStorage, ModBindable {
    private final List<HasManaStorage> connectedStorages = new ArrayList<>();

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(ManaStorage.advancedEnergyCapacity) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModPackets.sendToClients(new EnergySyncS2C(getEnergyStored(), getBlockPos()));
        }
    };

    @Override
    public ModEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public int energyUsage() {
        return 0;
    }

    @Override
    public boolean powered() {
        return true;
    }

    private final ModManaStorage manaStorage = new ModManaStorage() {
        @Override
        public long getManaStored() {
            long manaStored = 0;
            for(HasManaStorage storage : connectedStorages) {
                manaStored += storage.getManaStorage().getManaStored();
            }

            return manaStored;
        }

        @Override
        public double getManaStoredFraction() {
            return (double) getManaStored() / (double) getFullCapacity();
        }

        @Override
        public long getFullCapacity() {
            long capacity = 0;
            for(HasManaStorage storage : connectedStorages) {
                capacity += storage.getManaStorage().getFullCapacity();
            }

            return capacity;
        }

        @Override
        public long getRemainingCapacity() {
            return getFullCapacity() - getManaStored();
        }

        @Override
        public boolean isEmpty() {
            return getManaStored() <= 0;
        }

        @Override
        public boolean isFull() {
            return getManaStored() >= getFullCapacity();
        }

        @Override
        public long receiveMana(long mana, boolean simulate) {
            long receivedMana = 0;

            //Sort by lowest to highest capacity
            connectedStorages.sort(Comparator.comparingLong(storage -> storage.getManaStorage().getFullCapacity()));
            for(HasManaStorage connectedStorage : connectedStorages) {
                receivedMana += connectedStorage.getManaStorage().receiveMana(mana - receivedMana, simulate);

                if(receivedMana >= mana) break;
            }
            return receivedMana;
        }

        @Override
        public long extractMana(long mana, boolean simulate) {
            long extractedMana = 0;

            //Sort by highest to lowest capacity
            connectedStorages.sort(Comparator.comparingLong(storage -> -storage.getManaStorage().getFullCapacity()));
            for(HasManaStorage connectedStorage : connectedStorages) {
                extractedMana += connectedStorage.getManaStorage().extractMana(mana - extractedMana, simulate);

                if(extractedMana >= mana) break;
            }
            return extractedMana;
        }

        @Override
        public void setMana(long mana) {
            //Doesn't do anything
        }

        @Override
        public void onManaChanged() {
            //Maybe do something here
        }
    };

    @Override
    public ModManaStorage getManaStorage() {
        return manaStorage;
    }

    @Override
    public GlobalPos position() {
        if(getLevel() != null) return GlobalPos.of(getLevel().dimension(), getBlockPos());
        return null;
    }

    public CoreEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.core.get(), blockPos, blockState);
    }

    @Override
    protected CompoundTag saveNbt(CompoundTag nbt) {
        nbt.putInt("Energy", energyStorage.getEnergyStored());
        return nbt;
    }

    @Override
    protected void loadNbt(CompoundTag nbt) {
        energyStorage.setEnergy(nbt.getInt("Energy"));
        energyStorage.onEnergyChanged();
    }

    @Override
    protected <T> LazyOptional<T> returnCapability(Capability<T> cap, Direction side) {
        if(cap == CapabilityEnergy.ENERGY) {
            return energyStorage.lazy.cast();
        }

        return null;
    }

    @Override
    protected void loadCapabilities() {
        energyStorage.lazy = LazyOptional.of(() -> energyStorage);
    }

    @Override
    protected void invalidateCapabilities() {
        energyStorage.lazy.invalidate();
    }

    @Override
    protected void tick(Level level, BlockPos blockPos, BlockState blockState) {
        connectedStorages.clear();
        connectedStorages.addAll(getConnectedStorages(level, blockPos, new ArrayList<>()));

        Map<BlockPos, CoreData> map = ManaStorage.pendingCoreData.containsKey(level.dimension()) ? ManaStorage.pendingCoreData.get(level.dimension()) : new HashMap<>();
        map.put(blockPos, CoreData.of(this));
        ManaStorage.pendingCoreData.put(level.dimension(), map);
    }

    private static List<HasManaStorage> getConnectedStorages(Level level, BlockPos pos, List<HasManaStorage> foundStorages) {
        List<HasManaStorage> storages = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            if(LevelUtil.getBlockEntity(level, pos.relative(direction)) instanceof HasManaStorage entity && !(entity instanceof CoreEntity) && !foundStorages.contains(entity)) {
                storages.add(entity);
                foundStorages.add(entity);
                storages.addAll(getConnectedStorages(level, pos.relative(direction), foundStorages));
            }
        }

        return storages;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.manastorage.storage_core");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new CoreMenu(id, inventory, this);
    }
}
