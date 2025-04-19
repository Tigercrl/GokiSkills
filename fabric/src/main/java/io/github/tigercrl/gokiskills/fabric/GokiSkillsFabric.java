package io.github.tigercrl.gokiskills.fabric;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.network.payloads.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class GokiSkillsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // network
        registerReceivers();

        // Run our common setup.
        GokiSkills.init();
    }

    private void registerReceivers() {
        PayloadTypeRegistry.playC2S().register(
                C2SSkillDowngradePayload.TYPE,
                C2SSkillDowngradePayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SSkillUpgradePayload.TYPE,
                C2SSkillUpgradePayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SSkillFastDowngradePayload.TYPE,
                C2SSkillFastDowngradePayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SSkillFastUpgradePayload.TYPE,
                C2SSkillFastUpgradePayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SSkillTogglePayload.TYPE,
                C2SSkillTogglePayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SConfigRequestPayload.TYPE,
                C2SConfigRequestPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                C2SSkillInfoRequestPayload.TYPE,
                C2SSkillInfoRequestPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                S2CConfigSyncPayload.TYPE,
                S2CConfigSyncPayload.STREAM_CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                S2CSkillInfoSyncPayload.TYPE,
                S2CSkillInfoSyncPayload.STREAM_CODEC
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillDowngradePayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillDowngrade(payload, context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillUpgradePayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillUpgrade(payload, context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillFastDowngradePayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillFastDowngrade(payload, context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillFastUpgradePayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillFastUpgrade(payload, context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillTogglePayload.TYPE,
                (payload, context) -> GokiNetwork.handleSkillToggle(payload, context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SConfigRequestPayload.TYPE,
                (payload, context) -> GokiNetwork.sendConfigSync(context.player())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                C2SSkillInfoRequestPayload.TYPE,
                (payload, context) -> GokiNetwork.sendSkillInfoSync(context.player())
        );
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
