package me.auoggi.manastorage.block.entity;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModBlockEntities;
import me.auoggi.manastorage.ModItems;
import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.block.BasicImporterBlock;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import me.auoggi.manastorage.screen.BasicImporterMenu;
import me.auoggi.manastorage.util.ManaStorageCoreClientData;
import me.auoggi.manastorage.util.ModEnergyStorage;
import me.auoggi.manastorage.util.ModItemStorage;
import me.auoggi.manastorage.util.ModManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class BasicImporterBlockEntity extends BlockEntity implements MenuProvider {
    private final ModManaStorage manaStorage = new ModManaStorage(500000) {
        @Override
        public void onManaChanged() {
            setChanged();
            ModPackets.sendToClients(new ManaSyncS2C(getManaStored(), getBlockPos()));
        }
    };

    private final ModItemStorage itemStorage = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && stack.getItem() == ModItems.testItem.get();
        }
    };

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(ManaStorage.basicEnergyCapacity) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModPackets.sendToClients(new EnergySyncS2C(getEnergyStored(), getBlockPos()));
        }
    };

    public ModManaStorage getManaStorage() {
        return manaStorage;
    }

    public ModEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public BasicImporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.basicImporter.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.putLong("Mana", manaStorage.getManaStored());
        tag.put("Inventory", itemStorage.serializeNBT());
        tag.putInt("Energy", energyStorage.getEnergyStored());

        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        manaStorage.setMana(tag.getInt("Mana"));
        manaStorage.onManaChanged();
        itemStorage.deserializeNBT(tag.getCompound("Inventory"));
        energyStorage.setEnergy(tag.getInt("Energy"));
        energyStorage.onEnergyChanged();

        super.load(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        //Make sure capabilities don't work on the front of the block
        if(getBlockState().getValue(BasicImporterBlock.FACING) != side) {
            if(cap == CapabilityEnergy.ENERGY) {
                return energyStorage.lazy.cast();
            }

            if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return itemStorage.lazy.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        itemStorage.lazy = LazyOptional.of(() -> itemStorage);
        energyStorage.lazy = LazyOptional.of(() -> energyStorage);
        super.onLoad();
    }

    @Override
    public void invalidateCaps() {
        itemStorage.lazy.invalidate();
        energyStorage.lazy.invalidate();
        super.invalidateCaps();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BasicImporterBlockEntity basicImporterBlockEntity) {
        if(level.isClientSide()) return;

        basicImporterBlockEntity.tick(level, blockPos, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        ModPackets.sendToClients(new ManaSyncS2C(manaStorage.getManaStored(), blockPos));
        ModPackets.sendToClients(new EnergySyncS2C(energyStorage.getEnergyStored(), blockPos));

        ManaStorage.pendingCoreServerDataMap.put(GlobalPos.of(level.dimension(), blockPos), ManaStorageCoreClientData.of(this));

        if(energyStorage.extractEnergy(ManaStorage.basicEnergyUsage, false) >= ManaStorage.basicEnergyUsage && manaStorage.getRemainingCapacity() != 0)
            importMana(level, blockPos, blockState, manaStorage.receiveMana(importMana(level, blockPos, blockState, ManaStorage.basicSpeed, true), false), false);
    }

    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(itemStorage.getSlots());
        for(int i = 0; i < itemStorage.getSlots(); i++) {
            inventory.setItem(i, itemStorage.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, inventory);
    }

    public long importMana(Level level, BlockPos blockPos, BlockState blockState, long amount, boolean simulate) {
        BlockEntity facing = level.getBlockEntity(blockPos.relative(blockState.getValue(BasicImporterBlock.FACING)));
        if(facing instanceof TileEntityGeneratingFlower flower) {
            amount = Math.min(flower.getMana(), amount);
            if(!simulate) {
                flower.addMana((int) -amount);
                flower.sync();
            }
            return amount;
        } else if(facing instanceof TilePool pool) {
            amount = Math.min(pool.getCurrentMana(), amount);
            if(!simulate) pool.receiveMana((int) -amount);
            return amount;
        }

        return 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Basic Mana Importer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new BasicImporterMenu(id, inventory, this);
    }
}
