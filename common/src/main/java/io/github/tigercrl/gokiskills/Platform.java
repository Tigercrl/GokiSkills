package io.github.tigercrl.gokiskills;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class Platform {
    @ExpectPlatform
    public static void sendC2SPayload(CustomPacketPayload payload) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendS2CPayload(CustomPacketPayload payload, ServerPlayer player) {
        throw new AssertionError();
    }
}
