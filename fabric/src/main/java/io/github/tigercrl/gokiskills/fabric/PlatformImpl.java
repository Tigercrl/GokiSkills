package io.github.tigercrl.gokiskills.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class PlatformImpl {
    public static void sendC2SPayload(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void sendS2CPayload(CustomPacketPayload payload, ServerPlayer player) {
        ServerPlayNetworking.send(player, payload);
    }
}
