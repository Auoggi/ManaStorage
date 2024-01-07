package me.auoggi.manastorage.screen;

import me.auoggi.manastorage.ModBlocks;
import me.auoggi.manastorage.ModMenuTypes;
import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BasicImporterMenu extends AbstractContainerMenu {
    public final BasicImporterBlockEntity blockEntity;
    private final Level level;

    public BasicImporterMenu(int id, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(id, inventory, inventory.player.level.getBlockEntity(friendlyByteBuf.readBlockPos()));
    }

    public BasicImporterMenu(int id, Inventory inventory, BlockEntity blockEntity) {
        super(ModMenuTypes.basicImporter.get(), id);
        checkContainerSize(inventory, 1);
        this.blockEntity = (BasicImporterBlockEntity) blockEntity;
        level = inventory.player.level;

        addPlayerInventory(inventory);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> addSlot(new SlotItemHandler(handler, 0, 80, 37)));
    }

    //Credit to: diesieben07 | https://github.com/diesieben07/SevenCommons
    private static final int blockEntitySlotCount = 1;

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if(!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if(index < 36) {
            if(!moveItemStackTo(sourceStack, 36, 36 + blockEntitySlotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else if(index < 36 + blockEntitySlotCount) {
            if(!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        if(sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.basicImporter.get());
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

    public BasicImporterBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
