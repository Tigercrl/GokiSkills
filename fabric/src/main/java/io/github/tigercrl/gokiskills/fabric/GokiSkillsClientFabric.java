package io.github.tigercrl.gokiskills.fabric;

import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.network.payloads.S2CConfigSyncPayload;
import io.github.tigercrl.gokiskills.network.payloads.S2CSkillInfoSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class GokiSkillsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        GokiSkillsClient.init();

        ClientPlayNetworking.registerGlobalReceiver(
                S2CConfigSyncPayload.TYPE,
                (payload, context) -> GokiNetwork.handleConfigSync(payload)
        );
        ClientPlayNetworking.registerGlobalReceiver(
                S2CSkillInfoSyncPayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillInfoSync(payload)
        );
    }
}
