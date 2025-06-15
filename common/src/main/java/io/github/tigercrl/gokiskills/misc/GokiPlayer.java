package io.github.tigercrl.gokiskills.misc;

import io.github.tigercrl.gokiskills.skill.SkillInfo;

public interface GokiPlayer {
    int getPlayerTotalXp();

    SkillInfo getSkillInfo();

    void setSkillInfo(SkillInfo skillInfo);
}
