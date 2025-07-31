package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.*;
import io.github.tigercrl.gokiskills.GokiSkills;
import net.minecraft.network.FriendlyByteBuf;

public interface GokiNetwork {
    SimpleNetworkManager NET = SimpleNetworkManager.create(GokiSkills.MOD_ID);
    MessageType REQUEST_CONFIG = NET.registerC2S("request_config", buf -> new C2SRequestConfigMessage());
    MessageType SYNC_CONFIG = NET.registerS2C("sync_config", S2CSyncConfigMessage::new);
    MessageType REQUEST_SKILL_INFO = NET.registerC2S("request_skill_info", buf -> new C2SRequestSkillInfoMessage());
    MessageType SYNC_SKILL_INFO = NET.registerS2C("sync_skill_info", new MessageDecoder<>() {
        @Override
        public BaseS2CMessage decode(FriendlyByteBuf buf) {
            return null;
        }

        @Override
        public NetworkManager.NetworkReceiver createReceiver() {
            return (buf, context) -> {
                Message packet = new S2CSyncSkillInfoMessage(context.getPlayer(), buf);
                context.queue(() -> packet.handle(context));
            };
        }
    });
    MessageType SKILL_UPGRADE = NET.registerC2S("skill_upgrade", C2SUpgradeMessage::new);
    MessageType SKILL_FAST_UPGRADE = NET.registerC2S("skill_fast_upgrade", C2SFastUpgradeMessage::new);
    MessageType SKILL_DOWNGRADE = NET.registerC2S("skill_downgrade", C2SDowngradeMessage::new);
    MessageType SKILL_FAST_DOWNGRADE = NET.registerC2S("skill_fast_downgrade", C2SFastDowngradeMessage::new);
    MessageType SKILL_TOGGLE = NET.registerC2S("skill_toggle", C2SToggleMessage::new);
}
