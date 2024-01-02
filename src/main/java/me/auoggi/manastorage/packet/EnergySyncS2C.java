package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.block.entity.BasicImporterBlockEntity;
import me.auoggi.manastorage.screen.BasicImporterMenu;
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
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof BasicImporterBlockEntity blockEntity) {
                blockEntity.setEnergy(energy);

                if(Minecraft.getInstance().player.containerMenu instanceof BasicImporterMenu menu && menu.getBlockEntity().getBlockPos().equals(blockPos)) {
                    menu.getBlockEntity().setEnergy(energy);
                }
            }
        });
        return true;
    }
}
