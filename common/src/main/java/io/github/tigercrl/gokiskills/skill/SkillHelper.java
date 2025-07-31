package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.misc.GokiPlayer;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class SkillHelper {
    public static SkillInfo getInfoOrNull(Player player) {
        return ((GokiPlayer) player).getSkillInfo();
    }

    public static SkillInfo getInfo(Player player) {
        SkillInfo info = getInfoOrNull(player);
        return info == null ? new SkillInfo(player) : info;
    }

    public static int getTotalXp(Player player) {
        return ((GokiPlayer) player).getPlayerTotalXp();
    }

    public static void setSkillInfo(Player player, SkillInfo skillInfo) {
        ((GokiPlayer) player).setSkillInfo(skillInfo);
    }

    public static void updateSkill(Player player, ResourceLocation location, boolean upgrade, boolean fast) {
        ((GokiServerPlayer) player).updateSkill(location, upgrade, fast);
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

    @Environment(EnvType.CLIENT)
    public static SkillInfo getClientInfoOrNull() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return getInfoOrNull(player);
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static SkillInfo getClientInfo() {
        SkillInfo info = getClientInfoOrNull();
        return info == null ? new SkillInfo(Minecraft.getInstance().player) : info;
    }

    @Environment(EnvType.CLIENT)
    public static int getClientTotalXp() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return getTotalXp(player);
        }
        return 0;
    }

    @Environment(EnvType.CLIENT)
    public static void setClientSkillInfo(SkillInfo skillInfo) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            setSkillInfo(player, skillInfo);
        }
    }
}
