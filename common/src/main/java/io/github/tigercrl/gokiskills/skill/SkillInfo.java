package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.network.S2CSyncSkillInfoMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SkillInfo {
    private final Map<ResourceLocation, Integer> levels;

    public SkillInfo() {
        this(new HashMap<>());
    }

    protected SkillInfo(Map<ResourceLocation, Integer> levels) {
        this.levels = levels;
    }

    public int getLevel(ISkill skill) {
        return getLevel(skill.getResourceLocation(), skill.getDefaultLevel());
    }

    public int getLevel(ResourceLocation location, int defaultLevel) {
        if (!levels.containsKey(location)) {
            levels.put(location, defaultLevel);
        }
        return levels.get(location);
    }

    @Nullable
    public Double getBonus(ISkill skill) {
        return skill.calcBonus(getLevel(skill));
    }

    public void setLevel(ISkill skill, int level) {
        setLevel(skill.getResourceLocation(), level);
    }

    public void setLevel(ResourceLocation location, int level) {
        levels.put(location, level);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        levels.forEach((key, value) -> compoundTag.putInt(key.toString(), value));
        return compoundTag;
    }

    public void writeBuf(FriendlyByteBuf buf) {
        buf.writeMap(levels, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);
    }

    public static SkillInfo fromBuf(FriendlyByteBuf buf) {
        return new SkillInfo(new HashMap<>(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt)));
    }

    public static SkillInfo fromNbt(CompoundTag compoundTag) {
        Map<ResourceLocation, Integer> levels = new HashMap<>();
        compoundTag.getAllKeys().forEach(key -> levels.put(new ResourceLocation(key), compoundTag.getInt(key)));
        return new SkillInfo(levels);
    }

    public void onDeath() {
        if (GokiSkills.config.lostLevelOnDeath.enabled) {
            levels.forEach((key, value) -> {
                boolean lost = Math.random() < GokiSkills.config.lostLevelOnDeath.chance;
                if (lost) {
                    ISkill s = SkillManager.SKILL.get(key);
                    int lostLevel = Math.min(
                            GokiUtils.randomInt(
                                    GokiSkills.config.lostLevelOnDeath.minLevel,
                                    GokiSkills.config.lostLevelOnDeath.maxLevel + 1
                            ), value - s.getMinLevel()
                    );
                    if (lostLevel > 0) {
                        levels.put(key, value - lostLevel);
                    }
                }
            });
        }
    }

    public ServerSkillInfo toServerSkillInfo(ServerPlayer player) {
        return new ServerSkillInfo(levels, player);
    }

    public void sync(ServerPlayer player) {
        new S2CSyncSkillInfoMessage(this).sendTo(player);
    }
}
