package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.network.S2CSyncSkillInfoMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillInfo {
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
                        sync(player);
                    }
                }
            });
        }
    }

    public ServerSkillInfo toServerSkillInfo(ServerPlayer player) {
        return new ServerSkillInfo(levels, disabled, player);
    }

    public void sync(ServerPlayer player) {
        new S2CSyncSkillInfoMessage(this).sendTo(player);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag levelTag = new CompoundTag();
        levels.forEach((key, value) -> levelTag.putInt(key.toString(), value));
        compoundTag.put("levels", levelTag);
        ListTag disabledTag = new ListTag();
        disabled.forEach(key -> disabledTag.add(StringTag.valueOf(key.toString())));
        compoundTag.put("disabled", disabledTag);
        return compoundTag;
    }

    public void writeBuf(FriendlyByteBuf buf) {
        buf.writeMap(levels, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeVarInt);
        buf.writeCollection(disabled, FriendlyByteBuf::writeResourceLocation);
    }

    public static SkillInfo fromBuf(FriendlyByteBuf buf) {
        return new SkillInfo(
                new HashMap<>(buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readVarInt)),
                buf.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation)
        );
    }

    public static SkillInfo fromNbt(CompoundTag compoundTag) {
        Map<ResourceLocation, Integer> levels = new HashMap<>();
        Set<ResourceLocation> disabled = new HashSet<>();
        // v1.0.1+
        if (compoundTag.contains("levels") && compoundTag.contains("disabled")) {
            CompoundTag levelTag = compoundTag.getCompound("levels");
            levelTag.getAllKeys().forEach(key -> {
                if (!ResourceLocation.tryParse(key).getNamespace().equals("minecraft"))
                    levels.put(ResourceLocation.tryParse(key), compoundTag.getInt(key));
            });
            compoundTag.getList("disabled", Tag.TAG_STRING).forEach(tag -> disabled.add(ResourceLocation.tryParse(tag.getAsString())));
        } else { // v1.0.0
            compoundTag.getAllKeys().forEach(key -> levels.put(ResourceLocation.tryParse(key), compoundTag.getInt(key)));
        }
        return new SkillInfo(levels, disabled);
    }
}
