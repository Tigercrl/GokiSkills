package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SKILL_FAST_UPGRADE;

public class C2SFastUpgradeMessage extends BaseC2SMessage {
    public final ResourceLocation location;

    public C2SFastUpgradeMessage(ResourceLocation location) {
        this.location = location;
    }

    public C2SFastUpgradeMessage(FriendlyByteBuf buf) {
        location = buf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return SKILL_FAST_UPGRADE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(location);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER) {
            GokiNetwork.handleLevelOperation((ServerPlayer) context.getPlayer(), location, true, true);
        }
    }
}
