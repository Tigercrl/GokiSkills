package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.skill.ServerSkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.REQUEST_SKILL_INFO;

public class C2SRequestSkillInfoMessage extends BaseC2SMessage {
    @Override
    public MessageType getType() {
        return REQUEST_SKILL_INFO;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER)
            ((ServerSkillInfo) SkillManager.getInfo(context.getPlayer())).sync();
    }
}
