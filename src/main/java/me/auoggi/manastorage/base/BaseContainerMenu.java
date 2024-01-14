package me.auoggi.manastorage.base;

import me.auoggi.manastorage.ModBlocks;
import me.auoggi.manastorage.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BaseContainerMenu<E extends BaseBlockEntity> extends AbstractContainerMenu {
    private final E blockEntity;
    private final Level level;

    private final int slotCount;

    public BaseContainerMenu(int id, Inventory inventory, int slotCount, FriendlyByteBuf friendlyByteBuf) {
        //noinspection unchecked
        this(id, inventory, slotCount, (E) inventory.player.level.getBlockEntity(friendlyByteBuf.readBlockPos()));
    }

    public BaseContainerMenu(int id, Inventory inventory, int slotCount, E blockEntity) {
        super(ModMenuTypes.basicImporter.get(), id);
        this.blockEntity = (E) blockEntity;
        level = inventory.player.level;
        this.slotCount = slotCount;
        addPlayerInventory(inventory);

        if(slotCount > 0) {
            checkContainerSize(inventory, slotCount);
            this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> addSlot(new SlotItemHandler(handler, 0, 80, 37)));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if(slotCount == 0) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = slot.getItem();

        if((index < 36 && !moveItemStackTo(sourceStack, 36, 36 + slotCount, false)) || (index < 36 + slotCount && !moveItemStackTo(sourceStack, 0, 36, false)) || index >= 36 + slotCount) {
            return ItemStack.EMPTY;
        }

        if(sourceStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, sourceStack);
        return sourceStack.copy();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.basicImporter.get());
    }

    public E getBlockEntity() {
        return blockEntity;
    }
}
