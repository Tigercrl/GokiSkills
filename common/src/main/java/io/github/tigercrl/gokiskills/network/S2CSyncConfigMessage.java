package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.config.CommonConfig;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SYNC_CONFIG;

public class S2CSyncConfigMessage extends BaseS2CMessage {
    public final CommonConfig config;

    public S2CSyncConfigMessage(CommonConfig config) {
        this.config = config;
    }

    public S2CSyncConfigMessage(FriendlyByteBuf buf) {
        this(ConfigUtils.deserialize(buf.readUtf(), CommonConfig.class));
    }

    @Override
    public MessageType getType() {
        return SYNC_CONFIG;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(ConfigUtils.serialize(config));
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.CLIENT)
            GokiSkillsClient.serverConfig = config;
    }
}
