package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.base.HasManaStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ManaSyncS2C {
    private final long mana;
    private final BlockPos blockPos;

    public ManaSyncS2C(long mana, BlockPos blockPos) {
        this.mana = mana;
        this.blockPos = blockPos;
    }

    public ManaSyncS2C(FriendlyByteBuf friendlyByteBuf) {
        mana = friendlyByteBuf.readLong();
        blockPos = friendlyByteBuf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeLong(mana);
        friendlyByteBuf.writeBlockPos(blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof HasManaStorage blockEntity) {
                blockEntity.getManaStorage().setMana(mana);
            }
        });
        return true;
    }
}
