package io.github.tigercrl.gokiskills.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.ServerSkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface GokiNetwork {
    SimpleNetworkManager NET = SimpleNetworkManager.create(GokiSkills.MOD_ID);
    MessageType REQUEST_CONFIG = NET.registerC2S("request_config", buf -> new C2SRequestConfigMessage());
    MessageType SYNC_CONFIG = NET.registerS2C("sync_config", S2CSyncConfigMessage::new);
    MessageType REQUEST_SKILL_INFO = NET.registerC2S("request_skill_info", buf -> new C2SRequestSkillInfoMessage());
    MessageType SYNC_SKILL_INFO = NET.registerS2C("sync_skill_info", S2CSyncSkillInfoMessage::new);
    MessageType SKILL_UPGRADE = NET.registerC2S("skill_upgrade", C2SUpgradeMessage::new);
    MessageType SKILL_FAST_UPGRADE = NET.registerC2S("skill_fast_upgrade", C2SFastUpgradeMessage::new);
    MessageType SKILL_DOWNGRADE = NET.registerC2S("skill_downgrade", C2SDowngradeMessage::new);
    MessageType SKILL_FAST_DOWNGRADE = NET.registerC2S("skill_fast_downgrade", C2SFastDowngradeMessage::new);

    static void handleLevelOperation(ServerPlayer p, ResourceLocation location, boolean upgrade, boolean fast) {
        ISkill skill = SkillManager.SKILL.get(location);
        SkillInfo skillInfo = SkillManager.getInfo(p);
        int level = skillInfo.getLevel(skill);

        int[] result = SkillManager.calcOperation(skill, level, GokiUtils.getPlayerTotalXp(p), upgrade, fast);

        skillInfo.setLevel(location, level + result[0]);
        p.giveExperiencePoints(result[1]);
        ((ServerSkillInfo) SkillManager.getInfo(p)).sync();
        p.connection.send(
                new ClientboundSetExperiencePacket(
                        p.experienceProgress,
                        p.totalExperience,
                        p.experienceLevel
                )
        );
    }
}
