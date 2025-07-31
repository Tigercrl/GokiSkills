package io.github.tigercrl.gokiskills.network;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import io.github.tigercrl.gokiskills.network.payloads.*;
import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static io.github.tigercrl.gokiskills.Platform.sendC2SPayload;
import static io.github.tigercrl.gokiskills.Platform.sendS2CPayload;

public class GokiNetwork {
    public static void sendSkillDowngrade(ResourceLocation location) {
        sendC2SPayload(new C2SSkillDowngradePayload(location));
    }

    public static void sendSkillUpgrade(ResourceLocation location) {
        sendC2SPayload(new C2SSkillUpgradePayload(location));
    }

    public static void sendSkillFastDowngrade(ResourceLocation location) {
        sendC2SPayload(new C2SSkillFastDowngradePayload(location));
    }

    public static void sendSkillFastUpgrade(ResourceLocation location) {
        sendC2SPayload(new C2SSkillFastUpgradePayload(location));
    }

    public static void sendSkillToggle(ResourceLocation location) {
        sendC2SPayload(new C2SSkillTogglePayload(location));
    }

    public static void sendConfigRequest() {
        sendC2SPayload(new C2SConfigRequestPayload());
    }

    public static void sendSkillInfoRequest() {
        sendC2SPayload(new C2SSkillInfoRequestPayload());
    }

    public static void sendConfigSync(Player p) {
        sendS2CPayload(new S2CConfigSyncPayload(GokiSkills.config), (ServerPlayer) p);
    }

    public static void sendSkillInfoSync(ServerPlayer p, SkillInfo info) {
        sendS2CPayload(new S2CSkillInfoSyncPayload(info), p);
    }

    public static void handleSkillDowngrade(C2SSkillDowngradePayload payload, Player p) {
        ((GokiServerPlayer) p).updateSkill(payload.location(), false, false);
    }

    public static void handleSkillUpgrade(C2SSkillUpgradePayload payload, Player p) {
        ((GokiServerPlayer) p).updateSkill(payload.location(), true, false);
    }

    public static void handleSkillFastDowngrade(C2SSkillFastDowngradePayload payload, Player p) {
        ((GokiServerPlayer) p).updateSkill(payload.location(), false, true);
    }

    public static void handleSkillFastUpgrade(C2SSkillFastUpgradePayload payload, Player p) {
        ((GokiServerPlayer) p).updateSkill(payload.location(), true, true);
    }

    public static void handleSkillToggle(C2SSkillTogglePayload payload, Player p) {
        SkillHelper.getInfo(p).toggle(payload.location());
    }

    public static void handleConfigSync(S2CConfigSyncPayload payload) {
        GokiSkillsClient.serverConfig = payload.config();
    }

    public static void handleSkillInfoSync(S2CSkillInfoSyncPayload payload) {
        SkillHelper.setClientSkillInfo(payload.info());
        GokiSkillsClient.lastPlayerInfoUpdated = Util.getMillis();
    }
}
