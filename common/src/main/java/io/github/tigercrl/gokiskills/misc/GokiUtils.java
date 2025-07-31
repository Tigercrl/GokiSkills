package io.github.tigercrl.gokiskills.misc;

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

    public static int getXpNeededForNextLevel(int level) {
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
}
