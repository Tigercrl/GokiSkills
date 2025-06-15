package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiEvents;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.network.S2CSyncSkillInfoMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerSkillInfo extends SkillInfo {
    public final ServerPlayer player;

    public ServerSkillInfo(ServerPlayer player) {
        this(new HashMap<>(), new HashSet<>(), player);
    }

    protected ServerSkillInfo(Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled, ServerPlayer player) {
        super(levels, disabled);
        this.player = player;
    }

    @Override
    public void setLevel(ISkill skill, int level) {
        levels.put(skill.getLocation(), level);
        GokiEvents.UPDATE.invoker().update(skill, player, level, getLevel(skill), this);
        sync();
    }

    @Override
    public void toggle(ISkill skill) {
        if (isEnabled(skill.getLocation())) {
            disabled.add(skill.getLocation());
        } else {
            disabled.remove(skill.getLocation());
        }
        GokiEvents.TOGGLE.invoker().toggle(skill, player, isEnabled(skill), this);
        sync();
    }

    @Override
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
                        sync();
                    }
                }
            });
        }
    }

    public void sync() {
        new S2CSyncSkillInfoMessage(this).sendTo(player);
    }

    @Nullable
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

    public static SkillInfo fromNbt(CompoundTag compoundTag, ServerPlayer player) {
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
        return new ServerSkillInfo(levels, disabled, player);
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
