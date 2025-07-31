package io.github.tigercrl.gokiskills.misc;

import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillRegistry;
import net.minecraft.resources.ResourceLocation;

public interface GokiServerPlayer {
    void syncSkillInfo();

    void updateSkill(ISkill skill, boolean upgrade, boolean fast);

    default void updateSkill(ResourceLocation location, boolean upgrade, boolean fast) {
        updateSkill(SkillRegistry.getSkill(location), upgrade, fast);
    }
}
