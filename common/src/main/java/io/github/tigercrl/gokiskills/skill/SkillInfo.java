package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillInfo {
    public static final StreamCodec<ByteBuf, SkillInfo> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(SkillInfo::fromNbt, SkillInfo::toNbt);

    private static final int SCHEMA_VERSION = 1;

    private final Map<ResourceLocation, Integer> levels;
    private final Set<ResourceLocation> disabled;

    public SkillInfo() {
        this(new HashMap<>(), new HashSet<>());
    }

    protected SkillInfo(Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled) {
        this.levels = levels;
        this.disabled = disabled;
    }

    public int getLevel(ISkill skill) {
        return getLevel(skill.getResourceLocation(), skill.getDefaultLevel());
    }

    public int getLevel(ResourceLocation location, int defaultLevel) {
        levels.putIfAbsent(location, defaultLevel);
        return levels.get(location);
    }

    @Nullable
    public Double getBonus(ISkill skill) {
        return skill.calcBonus(isEnabled(skill) ? getLevel(skill) : skill.getDefaultLevel());
    }

    public void setLevel(ISkill skill, int level) {
        levels.put(skill.getResourceLocation(), level);
    }

    public void setLevel(ResourceLocation location, int level) {
        setLevel(SkillManager.SKILL.get(location), level);
    }

    public boolean isEnabled(ISkill skill) {
        return skill.isEnabled() && !disabled.contains(skill.getResourceLocation());
    }

    public boolean isEnabled(ResourceLocation location) {
        return isEnabled(SkillManager.SKILL.get(location));
    }

    public void toggle(ResourceLocation location) {
        if (isEnabled(location)) {
            disabled.add(location);
        } else {
            disabled.remove(location);
        }
    }

    public void onDeath(ServerPlayer player) {
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
                        GokiNetwork.sendSkillInfoSync(player);
                    }
                }
            });
        }
    }

    public ServerSkillInfo toServerSkillInfo(ServerPlayer player) {
        return new ServerSkillInfo(levels, disabled, player);
    }

    public static SkillInfo fromNbt(CompoundTag compoundTag) {
        Map<ResourceLocation, Integer> levels = new HashMap<>();
        Set<ResourceLocation> disabled = new HashSet<>();
        SkillManager.SKILL.entrySet().forEach(entry -> levels.put(entry.getKey().location(), entry.getValue().getDefaultLevel()));
        if (compoundTag.contains("schema")) {
            switch (compoundTag.getInt("schema")) {
                case 1:
                    readVer1(compoundTag, levels, disabled);
            }
        } else if (compoundTag.contains("levels")) {
            readVer1(compoundTag, levels, disabled);
        } else {
            readVer0(compoundTag, levels);
        }
        return new SkillInfo(levels, disabled);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag levelTag = new CompoundTag();
        levels.forEach((key, value) -> levelTag.putInt(key.toString(), value));
        compoundTag.put("levels", levelTag);
        ListTag disabledTag = new ListTag();
        disabled.forEach(key -> disabledTag.add(StringTag.valueOf(key.toString())));
        compoundTag.put("disabled", disabledTag);
        compoundTag.putInt("schema", SCHEMA_VERSION);
        return compoundTag;
    }

    private static void readVer0(CompoundTag compoundTag, Map<ResourceLocation, Integer> levels) {
        compoundTag.getAllKeys().forEach(key -> levels.put(ResourceLocation.tryParse(key), compoundTag.getInt(key)));
    }

    private static void readVer1(CompoundTag compoundTag, Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled) {
        CompoundTag levelTag = compoundTag.getCompound("levels");
        levelTag.getAllKeys().forEach(key -> levels.put(ResourceLocation.tryParse(key), levelTag.getInt(key)));
        if (compoundTag.contains("disabled"))
            compoundTag.getList("disabled", Tag.TAG_STRING).forEach(tag -> disabled.add(ResourceLocation.tryParse(tag.getAsString())));
    }
}
