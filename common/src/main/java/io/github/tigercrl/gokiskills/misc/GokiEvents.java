package io.github.tigercrl.gokiskills.misc;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.minecraft.server.level.ServerPlayer;

public class GokiEvents {
    public static final Event<SkillInfoUpdate> UPDATE = EventFactory.createLoop(SkillInfoUpdate.class);
    public static final Event<SkillInfoToggle> TOGGLE = EventFactory.createLoop(SkillInfoToggle.class);

    public interface SkillInfoUpdate {
        void update(ISkill skill, ServerPlayer p, int newLevel, int oldLevel, SkillInfo skillInfo);
    }

    public interface SkillInfoToggle {
        void toggle(ISkill skill, ServerPlayer p, boolean newState, SkillInfo skillInfo);
    }
}
