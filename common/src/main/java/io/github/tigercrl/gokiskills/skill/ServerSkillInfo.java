package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;

public class ServerSkillInfo extends SkillInfo {
    public static final Event<SkillInfoUpdate> UPDATE = EventFactory.createLoop(SkillInfoUpdate.class);
    public static final Event<SkillInfoToggle> TOGGLE = EventFactory.createLoop(SkillInfoToggle.class);
    private final ServerPlayer player;

    public ServerSkillInfo(ServerPlayer player) {
        super();
        this.player = player;
    }

    protected ServerSkillInfo(Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled, ServerPlayer player) {
        super(levels, disabled);
        this.player = player;
    }

    @Override
    public void setLevel(ISkill skill, int level) {
        int oldLevel = getLevel(skill);
        super.setLevel(skill, level);
        UPDATE.invoker().update(skill, player, level, oldLevel, this);
    }

    @Override
    public void setLevel(ResourceLocation location, int level) {
        ISkill skill = SkillManager.SKILL.get(location);
        int oldLevel = getLevel(skill);
        super.setLevel(location, level);
        UPDATE.invoker().update(skill, player, level, oldLevel, this);
    }

    @Override
    public void toggle(ResourceLocation location) {
        ISkill skill = SkillManager.SKILL.get(location);
        super.toggle(location);
        TOGGLE.invoker().toggle(skill, player, isEnabled(skill), this);
    }

    public void onDeath() {
        super.onDeath(player);
    }

    @Override
    public void onDeath(ServerPlayer player) {
        onDeath();
    }

    public interface SkillInfoUpdate {
        void update(ISkill skill, ServerPlayer p, int newLevel, int oldLevel, ServerSkillInfo skillInfo);
    }

    public interface SkillInfoToggle {
        void toggle(ISkill skill, ServerPlayer p, boolean newState, ServerSkillInfo skillInfo);
    }
}
