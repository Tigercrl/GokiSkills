package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.GokiSkills;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.REQUEST_CONFIG;

public class C2SRequestConfigMessage extends BaseC2SMessage {
    @Override
    public MessageType getType() {
        return REQUEST_CONFIG;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER)
            new S2CSyncConfigMessage(GokiSkills.config).sendTo((ServerPlayer) context.getPlayer());
    }
}
