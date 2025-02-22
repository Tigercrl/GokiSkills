package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static io.github.tigercrl.gokiskills.network.GokiNetwork.SKILL_FAST_DOWNGRADE;

public class C2SFastDowngradeMessage extends BaseC2SMessage {
    public final ResourceLocation location;

    public C2SFastDowngradeMessage(ResourceLocation location) {
        this.location = location;
    }

    public C2SFastDowngradeMessage(FriendlyByteBuf buf) {
        location = buf.readResourceLocation();
    }

    @Override
    public MessageType getType() {
        return SKILL_FAST_DOWNGRADE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(location);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        if (context.getEnv() == EnvType.SERVER) {
            GokiNetwork.handleLevelOperation((ServerPlayer) context.getPlayer(), location, false, true);
        }
    }
}
