package io.github.tigercrl.gokiskills.misc;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class GokiUtils {
    public static String doubleToString(double d, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", d);
    }

    public static String floatToString(float f, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", f);
    }

    public static int randomInt(int min, int max) { // [min, max)
        return min + (int) (Math.random() * (max - min));
    }

    private static int getXpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public static int getTotalXpNeededForLevel(int level) {
        int sum = 0;
        for (int i = 0; i < level; i++) {
            sum += getXpNeededForNextLevel(i);
        }
        return sum;
    }

    public static int getPlayerTotalXp(Player player) {
        return getTotalXpNeededForLevel(player.experienceLevel) +
                Mth.floor(player.experienceProgress * getXpNeededForNextLevel(player.experienceLevel));
    }
}
