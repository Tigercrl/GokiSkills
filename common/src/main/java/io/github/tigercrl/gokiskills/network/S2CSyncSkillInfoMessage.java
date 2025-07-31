package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.fabricmc.api.EnvType;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SYNC_SKILL_INFO;

public class S2CSyncSkillInfoMessage extends BaseS2CMessage {
    public final SkillInfo info;

    public S2CSyncSkillInfoMessage(SkillInfo info) {
        this.info = info;
    }

    public S2CSyncSkillInfoMessage(Player player, FriendlyByteBuf buf) {
        this.info = SkillInfo.fromBuf(player, buf);
    }

    @Override
    public MessageType getType() {
        return SYNC_SKILL_INFO;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        info.writeBuf(buf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.CLIENT) {
            SkillHelper.setClientSkillInfo(info);
            GokiSkillsClient.lastPlayerInfoUpdated = Util.getMillis();
        }
    }
}
