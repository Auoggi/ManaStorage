package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.util.CoreData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class CoreDataSyncS2C {
    private final String dimension;
    private final Map<BlockPos, CoreData> data;

    public CoreDataSyncS2C(String dimension, Map<BlockPos, CoreData> data) {
        this.dimension = dimension;
        this.data = data;
    }

    public CoreDataSyncS2C(FriendlyByteBuf friendlyByteBuf) {
        this.dimension = friendlyByteBuf.readUtf();
        this.data = friendlyByteBuf.readMap(FriendlyByteBuf::readBlockPos, (buf) -> new CoreData(buf.readBoolean(), buf.readLong(), buf.readLong(), buf.readDouble()));
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(dimension);
        friendlyByteBuf.writeMap(data, FriendlyByteBuf::writeBlockPos, (buf, data) -> {
            buf.writeBoolean(data.powered());
            buf.writeLong(data.mana());
            buf.writeLong(data.capacity());
            buf.writeDouble(data.manaFraction());
        });
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> ManaStorage.clientCoreData.put(dimension, data));
        return true;
    }
}
