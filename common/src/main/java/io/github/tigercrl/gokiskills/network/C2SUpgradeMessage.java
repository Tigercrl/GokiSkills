package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SKILL_UPGRADE;

public class C2SUpgradeMessage extends BaseC2SMessage {
    public final ResourceLocation location;

    public C2SUpgradeMessage(ResourceLocation location) {
        this.location = location;
    }

    public C2SUpgradeMessage(FriendlyByteBuf buf) {
        location = buf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return SKILL_UPGRADE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(location);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER) {
            ((GokiServerPlayer) context.getPlayer()).updateSkill(location, true, false);
        }
    }
}
