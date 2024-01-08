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

    private static final int blockEntitySlotCount = 1;

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if(!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = slot.getItem();

        if((index < 36 && !moveItemStackTo(sourceStack, 36, 36 + blockEntitySlotCount, false)) || (index < 36 + blockEntitySlotCount && !moveItemStackTo(sourceStack, 0, 36, false)) || index >= 36 + blockEntitySlotCount) {
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
