package io.github.tigercrl.gokiskills.neoforge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlatformImpl {
    public static void sendC2SPayload(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendS2CPayload(CustomPacketPayload payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
