package me.auoggi.manastorage;

import me.auoggi.manastorage.packet.EnergySyncS2C;
import me.auoggi.manastorage.packet.CoreDataSyncS2C;
import me.auoggi.manastorage.packet.ManaSyncS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static SimpleChannel instance;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ManaStorage.MODID, "packets"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        instance = net;

        net.messageBuilder(EnergySyncS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EnergySyncS2C::new)
                .encoder(EnergySyncS2C::toBytes)
                .consumer(EnergySyncS2C::handle)
                .add();

        net.messageBuilder(ManaSyncS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ManaSyncS2C::new)
                .encoder(ManaSyncS2C::toBytes)
                .consumer(ManaSyncS2C::handle)
                .add();

        net.messageBuilder(CoreDataSyncS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CoreDataSyncS2C::new)
                .encoder(CoreDataSyncS2C::toBytes)
                .consumer(CoreDataSyncS2C::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        instance.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        instance.send(PacketDistributor.ALL.noArg(), message);
    }
}
