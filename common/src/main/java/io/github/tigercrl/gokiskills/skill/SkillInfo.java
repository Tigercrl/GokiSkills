package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillInfo {
    public final int SCHEMA_VERSION = 1;

    private final Player player;
    private final Map<ResourceLocation, Integer> levels;
    private final Set<ResourceLocation> disabled;

    public SkillInfo(Player player) {
        this(player, new HashMap<>(), new HashSet<>());
    }

    protected SkillInfo(Player player, Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled) {
        this.player = player;
        this.levels = levels;
        this.disabled = disabled;
        SkillRegistry.getSkills().forEach(skill -> levels.putIfAbsent(skill.getLocation(), skill.getDefaultLevel()));
    }

    public int getLevel(ISkill skill) {
        return levels.get(skill.getLocation());
    }

    public int getLevel(ResourceLocation location) {
        return getLevel(SkillRegistry.getSkill(location));
    }

    @Nullable
    public Double getBonus(ISkill skill) {
        return skill.calcBonus(isEnabled(skill) ? getLevel(skill) : skill.getDefaultLevel());
    }

    @Nullable
    public Double getBonus(ResourceLocation location) {
        return getBonus(SkillRegistry.getSkill(location));
    }

    public void setLevel(ISkill skill, int level) {
        levels.put(skill.getLocation(), level);
        SkillEvents.UPDATE.invoker().update(skill, player, level, getLevel(skill), this);
        sync();
    }

    public void setLevel(ResourceLocation location, int level) {
        setLevel(SkillRegistry.getSkill(location), level);
    }

    public boolean isEnabled(ISkill skill) {
        return skill.isEnabled() && !disabled.contains(skill.getLocation());
    }

    public boolean isEnabled(ResourceLocation location) {
        return isEnabled(SkillRegistry.getSkill(location));
    }

    public void toggle(ISkill skill) {
        if (isEnabled(skill.getLocation())) {
            disabled.add(skill.getLocation());
        } else {
            disabled.remove(skill.getLocation());
        }
        SkillEvents.TOGGLE.invoker().toggle(skill, player, isEnabled(skill), this);
        sync();
    }

    public void toggle(ResourceLocation location) {
        toggle(SkillRegistry.getSkill(location));
    }

    public void onDeath() {
        if (GokiSkills.config.lostLevelOnDeath.enabled) {
            levels.forEach((key, value) -> {
                boolean lost = Math.random() < GokiSkills.config.lostLevelOnDeath.chance;
                if (lost) {
                    ISkill s = SkillRegistry.getSkill(key);
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
        if (player instanceof GokiServerPlayer gp) {
            gp.syncSkillInfo();
        }
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

    public void writeBuf(FriendlyByteBuf buf) {
        buf.writeMap(levels, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);
        buf.writeCollection(disabled, FriendlyByteBuf::writeResourceLocation);
    }

    public static SkillInfo fromBuf(Player player, FriendlyByteBuf buf) {
        return new SkillInfo(
                player,
                new HashMap<>(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt)),
                buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation)
        );
    }

    public static SkillInfo fromNbt(Player player, CompoundTag compoundTag) {
        Map<ResourceLocation, Integer> levels = new HashMap<>();
        Set<ResourceLocation> disabled = new HashSet<>();
        if (compoundTag.contains("schema")) {
            switch (compoundTag.getInt("schema")) {
                case 1:
                    readVer1(compoundTag, levels, disabled);
            }
        } else {
            if (compoundTag.contains("levels")) {
                readVer1(compoundTag, levels, disabled);
            } else {
                readVer0(compoundTag, levels);
            }
        }
        return new SkillInfo(player, levels, disabled);
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
