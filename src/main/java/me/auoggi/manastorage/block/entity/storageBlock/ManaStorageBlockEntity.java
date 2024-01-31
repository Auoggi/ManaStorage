package me.auoggi.manastorage.block.entity.storageBlock;

import me.auoggi.manastorage.ModPackets;
import me.auoggi.manastorage.base.BaseBlockEntity;
import me.auoggi.manastorage.base.HasManaStorage;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import me.auoggi.manastorage.screen.ManaStorageBlockMenu;
import me.auoggi.manastorage.util.ModManaStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaStorageBlockEntity extends BaseBlockEntity implements HasManaStorage {
    private final String displayName;
    private final ModManaStorage manaStorage;

    public ManaStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, String displayName, long capacity) {
        super(blockEntityType, blockPos, blockState);

        this.displayName = displayName;
        this.manaStorage = new ModManaStorage(capacity) {
            @Override
            public void onManaChanged() {
                setChanged();
                ModPackets.sendToClients(new ManaSyncS2C(getManaStored(), getBlockPos()));
            }
        };
    }

    @Override
    protected void saveNbt(CompoundTag nbt) {
        nbt.putLong("Mana", manaStorage.getManaStored());
    }

    @Override
    protected void loadNbt(CompoundTag nbt) {
        manaStorage.setMana(nbt.getLong("Mana"));
        manaStorage.onManaChanged();
    }

    @Override
    protected <T> LazyOptional<T> returnCapability(Capability<T> cap, Direction side) {
        return null;
    }

    @Override
    protected void loadCapabilities() {}

    @Override
    protected void invalidateCapabilities() {}

    @Override
    protected void tick(Level level, BlockPos blockPos, BlockState blockState) {}

    @Override
    public ModManaStorage getManaStorage() {
        return manaStorage;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent(displayName);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ManaStorageBlockMenu(id, inventory, this);
    }
}
