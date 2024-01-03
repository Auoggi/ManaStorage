package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.screen.BasicImporterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaSyncS2C {
    private final int mana;
    private final BlockPos blockPos;

    public ManaSyncS2C(int mana, BlockPos blockPos) {
        this.mana = mana;
        this.blockPos = blockPos;
    }

    public ManaSyncS2C(FriendlyByteBuf friendlyByteBuf) {
        mana = friendlyByteBuf.readInt();
        blockPos = friendlyByteBuf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(mana);
        friendlyByteBuf.writeBlockPos(blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof BasicImporterBlockEntity blockEntity) {
                blockEntity.getManaStorage().setMana(mana);
            }
        });
        return true;
    }
}
