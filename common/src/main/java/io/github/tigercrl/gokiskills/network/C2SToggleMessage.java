package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SKILL_TOGGLE;

public class C2SToggleMessage extends BaseC2SMessage {
    public final ResourceLocation location;

    public C2SToggleMessage(ResourceLocation location) {
        this.location = location;
    }

    public C2SToggleMessage(FriendlyByteBuf buf) {
        location = buf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return SKILL_TOGGLE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(location);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER) {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            SkillInfo info = SkillManager.getInfo(player);
            info.toggle(location);
            info.sync(player);
        }
    }
}
