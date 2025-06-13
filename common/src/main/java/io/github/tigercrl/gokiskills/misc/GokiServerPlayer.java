package io.github.tigercrl.gokiskills.misc;

import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface GokiServerPlayer extends GokiPlayer {
    @NotNull SkillInfo getSkillInfo();

    void updateSkill(ISkill skill, boolean upgrade, boolean fast);

    default void updateSkill(ResourceLocation location, boolean upgrade, boolean fast) {
        updateSkill(SkillManager.SKILL.get(location), upgrade, fast);
    }
}
