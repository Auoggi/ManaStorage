package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergySyncS2C {
    private final int energy;
    private final BlockPos blockPos;

    public EnergySyncS2C(int energy, BlockPos blockPos) {
        this.energy = energy;
        this.blockPos = blockPos;
    }

    public EnergySyncS2C(FriendlyByteBuf friendlyByteBuf) {
        energy = friendlyByteBuf.readInt();
        blockPos = friendlyByteBuf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(energy);
        friendlyByteBuf.writeBlockPos(blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof BasicImporterBlockEntity blockEntity) {
                blockEntity.getEnergyStorage().setEnergy(energy);
            }
        });
        return true;
    }
}
