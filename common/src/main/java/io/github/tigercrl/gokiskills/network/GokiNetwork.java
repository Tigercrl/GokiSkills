package io.github.tigercrl.gokiskills.network;

import com.mojang.logging.LogUtils;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.network.payloads.*;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

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

    public static void sendSkillInfoSync(Player p) {
        ServerPlayer sp = (ServerPlayer) p;
        sendS2CPayload(new S2CSkillInfoSyncPayload(SkillManager.getInfo(sp)), sp);
    }

    public static void handleSkillDowngrade(C2SSkillDowngradePayload payload, Player p) {
        handleLevelOperation((ServerPlayer) p, payload.location(), false, false);
    }

    public static void handleSkillUpgrade(C2SSkillUpgradePayload payload, Player p) {
        handleLevelOperation((ServerPlayer) p, payload.location(), true, false);
    }

    public static void handleSkillFastDowngrade(C2SSkillFastDowngradePayload payload, Player p) {
        handleLevelOperation((ServerPlayer) p, payload.location(), false, true);
    }

    public static void handleSkillFastUpgrade(C2SSkillFastUpgradePayload payload, Player p) {
        handleLevelOperation((ServerPlayer) p, payload.location(), true, true);
    }

    public static void handleSkillToggle(C2SSkillTogglePayload payload, Player p) {
        SkillInfo info = SkillManager.getInfo(p);
        info.toggle(payload.location());
        sendSkillInfoSync(p);
    }

    public static void handleConfigSync(S2CConfigSyncPayload payload) {
        GokiSkillsClient.serverConfig = payload.config();
    }

    public static void handleSkillInfoSync(S2CSkillInfoSyncPayload payload) {
        GokiSkillsClient.playerInfo = payload.info();
        GokiSkillsClient.lastPlayerInfoUpdated = Util.getMillis();
    }

    private static void handleLevelOperation(ServerPlayer p, ResourceLocation location, boolean upgrade, boolean fast) {
        ISkill skill = SkillManager.SKILL.get(location);
        SkillInfo skillInfo = SkillManager.getInfo(p);
        int level = skillInfo.getLevel(skill);

        int[] result = SkillManager.calcOperation(skill, level, GokiUtils.getPlayerTotalXp(p), upgrade, fast);

        skillInfo.setLevel(location, level + result[0]);
        p.giveExperiencePoints(result[1]);
        sendSkillInfoSync(p);
        p.connection.send(
                new ClientboundSetExperiencePacket(
                        p.experienceProgress,
                        p.totalExperience,
                        p.experienceLevel
                )
        );
    }
}
