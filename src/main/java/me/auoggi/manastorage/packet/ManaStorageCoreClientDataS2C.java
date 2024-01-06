package me.auoggi.manastorage.packet;

import me.auoggi.manastorage.ManaStorage;
import me.auoggi.manastorage.util.ManaStorageCoreClientData;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ManaStorageCoreClientDataS2C {
    private final Map<GlobalPos, ManaStorageCoreClientData> data;

    public ManaStorageCoreClientDataS2C(Map<GlobalPos, ManaStorageCoreClientData> data) {
        this.data = data;
    }

    public ManaStorageCoreClientDataS2C(FriendlyByteBuf friendlyByteBuf) {
        this.data = friendlyByteBuf.readMap(
                (buf) -> GlobalPos.CODEC.parse(NbtOps.INSTANCE, buf.readNbt().get("GlobalPos")).result().filter(position -> position.pos().getY() != Integer.MIN_VALUE).orElse(null),
                (buf) -> new ManaStorageCoreClientData(buf.readBoolean(), buf.readDouble())
        );
    }

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(data, (buf, pos) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("GlobalPos", GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).get().orThrow());
            buf.writeNbt(compoundTag);
        }, (buf, data) -> {
            buf.writeBoolean(data.powered());
            buf.writeDouble(data.manaFraction());
        });
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(this::clientHandle);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void clientHandle() {
        ManaStorage.coreClientDataMap = data;
    }
}
