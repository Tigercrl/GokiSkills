package io.github.tigercrl.gokiskills.neoforge;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.network.payloads.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(GokiSkills.MOD_ID)
@EventBusSubscriber(modid = GokiSkills.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class GokiSkillsNeoForge {
    public GokiSkillsNeoForge() {
        // Run our common setup.
        GokiSkills.init();
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.NETWORK);
        registrar.playToServer(
                C2SSkillDowngradePayload.TYPE,
                C2SSkillDowngradePayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillDowngrade(payload, context.player())
        );
        registrar.playToServer(
                C2SSkillUpgradePayload.TYPE,
                C2SSkillUpgradePayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillUpgrade(payload, context.player())
        );
        registrar.playToServer(
                C2SSkillFastDowngradePayload.TYPE,
                C2SSkillFastDowngradePayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillFastDowngrade(payload, context.player())
        );
        registrar.playToServer(
                C2SSkillFastUpgradePayload.TYPE,
                C2SSkillFastUpgradePayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillFastUpgrade(payload, context.player())
        );
        registrar.playToServer(
                C2SSkillTogglePayload.TYPE,
                C2SSkillTogglePayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillToggle(payload, context.player())
        );
        registrar.playToServer(
                C2SConfigRequestPayload.TYPE,
                C2SConfigRequestPayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.sendConfigSync(context.player())
        );
        registrar.playToServer(
                C2SSkillInfoRequestPayload.TYPE,
                C2SSkillInfoRequestPayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.sendSkillInfoSync(context.player())
        );
        registrar.playToClient(
                S2CConfigSyncPayload.TYPE,
                S2CConfigSyncPayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleConfigSync(payload)
        );
        registrar.playToClient(
                S2CSkillInfoSyncPayload.TYPE,
                S2CSkillInfoSyncPayload.STREAM_CODEC,
                (payload, context) -> GokiNetwork.handleSkillInfoSync(payload)
        );
    }
}
