package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class ServerSkillInfo extends SkillInfo {
    public static final Event<SkillInfoUpdate> UPDATE = EventFactory.createLoop(SkillInfoUpdate.class);
    private final ServerPlayer player;

    public ServerSkillInfo(ServerPlayer player) {
        super();
        this.player = player;
    }

    protected ServerSkillInfo(Map<ResourceLocation, Integer> levels, ServerPlayer player) {
        super(levels);
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

    public void sync() {
        super.sync(player);
    }

    @Override
    public void sync(ServerPlayer player) {
        sync();
    }

    public interface SkillInfoUpdate {
        void update(ISkill skill, ServerPlayer p, int newLevel, int oldLevel, ServerSkillInfo skillInfo);
    }
}
