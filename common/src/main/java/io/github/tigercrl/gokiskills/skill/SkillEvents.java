package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class SkillEvents {
    public static final Event<SkillInfoUpdate> UPDATE = EventFactory.createLoop(SkillInfoUpdate.class);
    public static final Event<SkillInfoToggle> TOGGLE = EventFactory.createLoop(SkillInfoToggle.class);

    public interface SkillInfoUpdate {
        void update(ISkill skill, Player p, int newLevel, int oldLevel, SkillInfo skillInfo);
    }

    public interface SkillInfoToggle {
        void toggle(ISkill skill, Player p, boolean newState, SkillInfo skillInfo);
    }
}
