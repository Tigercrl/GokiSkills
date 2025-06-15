package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import net.minecraft.nbt.CompoundTag;
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

    protected final Map<ResourceLocation, Integer> levels;
    protected final Set<ResourceLocation> disabled;

    public SkillInfo() {
        this(new HashMap<>(), new HashSet<>());
    }

    protected SkillInfo(Map<ResourceLocation, Integer> levels, Set<ResourceLocation> disabled) {
        this.levels = levels;
        this.disabled = disabled;
    }

    public int getLevel(ISkill skill) {
        levels.putIfAbsent(skill.getLocation(), skill.getDefaultLevel());
        return levels.get(skill.getLocation());
    }

    public int getLevel(ResourceLocation location) {
        return getLevel(SkillManager.SKILL.get(location));
    }

    @Nullable
    public Double getBonus(ISkill skill) {
        return skill.calcBonus(isEnabled(skill) ? getLevel(skill) : skill.getDefaultLevel());
    }

    @Nullable
    public Double getBonus(ResourceLocation location) {
        return getBonus(SkillManager.SKILL.get(location));
    }

    public void setLevel(ISkill skill, int level) {
    }

    public void setLevel(ResourceLocation location, int level) {
        setLevel(SkillManager.SKILL.get(location), level);
    }

    public boolean isEnabled(ISkill skill) {
        return skill.isEnabled() && !disabled.contains(skill.getLocation());
    }

    public boolean isEnabled(ResourceLocation location) {
        return isEnabled(SkillManager.SKILL.get(location));
    }

    public void toggle(ISkill skill) {
    }

    public void toggle(ResourceLocation location) {
        toggle(SkillManager.SKILL.get(location));
    }

    public void onDeath() {
    }

    public void sync() {
    }

    @Nullable
    public CompoundTag toNbt() {
        return null;
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

    /**
     * Calculate the cost of upgrading / downgrading skill
     * @param skill skill
     * @param level current skill level
     * @param xp current experience points
     * @param upgrade is upgrade / downgrade
     * @param fast is fast upgrade / downgrade
     * @return [addLevel, addXp]
     */
    public static int[] calcOperation(ISkill skill, int level, int xp, boolean upgrade, boolean fast) {
        int addXp = 0;
        int addLevel = 0;
        if (upgrade) {
            if (fast) {
                while (level + addLevel < skill.getMaxLevel()) {
                    int thisCost = skill.calcCost(level + addLevel);
                    if (-addXp + thisCost > xp) break;
                    addLevel++;
                    addXp -= thisCost;
                }
                return new int[]{addLevel, addXp};
            } else {
                addXp = skill.calcCost(level);
                if (addXp > xp || level + 1 > skill.getMaxLevel()) return new int[]{0, 0};
                else return new int[]{1, -addXp};
            }
        } else {
            if (fast) {
                while (level + addLevel > skill.getMinLevel()) {
                    addXp += skill.calcReturn(level + addLevel);
                    addLevel--;
                }
                return new int[]{addLevel, addXp};
            } else {
                if (level - 1 < skill.getMinLevel()) {
                    return new int[]{0, 0};
                } else {
                    addXp = skill.calcReturn(level);
                    return new int[]{-1, addXp};
                }
            }
        }
    }

    public static SkillInfo getInfo(Player player) {
        if (player.level().isClientSide())
            return GokiSkillsClient.playerInfo == null ? new SkillInfo() : GokiSkillsClient.playerInfo;
        return ((GokiServerPlayer) player).getSkillInfo();
    }
}
