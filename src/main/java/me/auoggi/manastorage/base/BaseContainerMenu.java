package me.auoggi.manastorage.base;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BaseContainerMenu<E extends BaseBlockEntity> extends AbstractContainerMenu {
    private final E blockEntity;
    private final boolean hasSlot;

    @SuppressWarnings("unchecked")
    public BaseContainerMenu(int id, Inventory inventory, boolean hasSlot, MenuType<?> menuType, FriendlyByteBuf friendlyByteBuf) {
        this(id, inventory, hasSlot, menuType, (E) inventory.player.level.getBlockEntity(friendlyByteBuf.readBlockPos()));
    }

    public BaseContainerMenu(int id, Inventory inventory, boolean hasSlot, MenuType<?> menuType, E blockEntity) {
        super(menuType, id);
        this.blockEntity = blockEntity;
        this.hasSlot = hasSlot;
        addPlayerInventory(inventory);

        if(hasSlot) {
            checkContainerSize(inventory, 1);
            this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> addSlot(new SlotItemHandler(handler, 0, 80, 37)));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0; i < 3; ++i) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if(!hasSlot) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();

        //The 37's are 36 plus slot count
        if((index < 36 && !moveItemStackTo(stack, 36, 37, false)) || (index < 37 && !moveItemStackTo(stack, 0, 36, false)) || index >= 37) {
            return ItemStack.EMPTY;
        }

        if(stack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return stack.copy();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        //Check if player is too far from the block
        return player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, blockEntity.getBlockPos().getY() + 0.5, blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    public E getBlockEntity() {
        return blockEntity;
    }
}
