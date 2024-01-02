package me.auoggi.manastorage.block.entity;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.ModBlockEntities;
import me.auoggi.manastorage.ModItems;
import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.block.BasicImporterBlock;
import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import me.auoggi.manastorage.screen.BasicImporterMenu;
import me.auoggi.manastorage.util.ModEnergyStorage;
import me.auoggi.manastorage.util.ModManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.tile.mana.TilePool;

public class BasicImporterBlockEntity extends BlockEntity implements MenuProvider {
    private final ModManaStorage manaStorage = new ModManaStorage(500000) {
        @Override
        public void onManaChanged() {
            setChanged();
            ModPackets.sendToClients(new ManaSyncS2C(mana, getBlockPos()));
        }
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && stack.getItem() == ModItems.testItem.get();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(ManaStorage.basicEnergyCapacity) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ModPackets.sendToClients(new EnergySyncS2C(energy, getBlockPos()));
        }
    };
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public ModManaStorage getManaStorage() {
        return manaStorage;
    }

    public void setMana(int mana) {
        manaStorage.setMana(mana);
    }

    public ModEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public void setEnergy(int energy) {
        energyStorage.setEnergy(energy);
    }

    public BasicImporterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.basicImporter.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.putInt("Mana", manaStorage.getManaStored());
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("Energy", energyStorage.getEnergyStored());

        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        manaStorage.setMana(tag.getInt("Mana"));
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        energyStorage.setEnergy(tag.getInt("Energy"));

        super.load(tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        //Make sure capabilities don't work on the front of the block
        if(getBlockState().getValue(BasicImporterBlock.FACING) != side) {
            if(cap == CapabilityEnergy.ENERGY) {
                return lazyEnergyStorage.cast();
            }

            if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return lazyItemHandler.cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        super.onLoad();
    }

    @Override
    public void invalidateCaps() {
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
        super.invalidateCaps();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BasicImporterBlockEntity basicImporterBlockEntity) {
        if(level.isClientSide()) return;

        basicImporterBlockEntity.tick(level, blockPos, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if(energyStorage.extractEnergy(ManaStorage.basicEnergyUsage, false) >= ManaStorage.basicEnergyUsage && manaStorage.getRemainingCapacity() != 0)
            importMana(level, blockPos, blockState, manaStorage.receiveMana(importMana(level, blockPos, blockState, ManaStorage.importerSpeed, true), false), false);
    }

    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(level, worldPosition, inventory);
    }

    public int importMana(Level level, BlockPos blockPos, BlockState blockState, int amount, boolean simulate) {
        BlockEntity facing = level.getBlockEntity(blockPos.relative(blockState.getValue(BasicImporterBlock.FACING)));
        if (facing instanceof TileEntityGeneratingFlower flower) {
            amount = Math.min(flower.getMana(), amount);
            if (!simulate) {
                flower.addMana(-amount);
                flower.sync();
            }
            return amount;
        } else if (facing instanceof TilePool pool) {
            amount = Math.min(pool.getCurrentMana(), amount);
            if (!simulate) pool.receiveMana(-amount);
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
