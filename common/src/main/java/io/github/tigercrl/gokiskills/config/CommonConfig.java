package io.github.tigercrl.gokiskills.config;

import com.google.gson.JsonObject;
import io.github.tigercrl.gokiskills.skill.SkillManager;

import java.util.HashMap;
import java.util.Map;

public class CommonConfig implements GokiConfig {
    public Multiplier multiplier = new Multiplier();
    public LostLevelOnDeath lostLevelOnDeath = new LostLevelOnDeath();
    public Map<String, JsonObject> skills = SkillManager.getDefaultConfigs();

    public static class Multiplier {
        public double costMultiplier = 1.0;
        public double downgradeReturnFactor = 0.8;
        public double bonusMultiplier = 1.0;
        public double maxLevelMultiplier = 1.0;
    }

    public static class LostLevelOnDeath {
        public boolean enabled = false;
        public double chance = 0.5;
        public int minLevel = 1;
        public int maxLevel = 1;
    }

    @Override
    public void validatePostLoad() throws ConfigException {
        if (multiplier.costMultiplier < 0.0)
            throw new ConfigException("Cost multiplier cannot be negative");
        if (multiplier.downgradeReturnFactor < 0.0)
            throw new ConfigException("Downgrade return factor cannot be negative");
        if (multiplier.bonusMultiplier < 0.0)
            throw new ConfigException("Bonus multiplier cannot be negative");
        if (multiplier.maxLevelMultiplier <= 0.0)
            throw new ConfigException("Max level multiplier cannot be negative or zero");
        if (lostLevelOnDeath.chance < 0.0 || lostLevelOnDeath.chance > 1.0)
            throw new ConfigException("Lost level on death chance must be between 0.0 and 1.0");
        if (lostLevelOnDeath.minLevel < 0 || lostLevelOnDeath.maxLevel < 0)
            throw new ConfigException("Lost level on death levels cannot be negative");
        if (lostLevelOnDeath.minLevel > lostLevelOnDeath.maxLevel)
            throw new ConfigException("Lost level on death min level cannot be greater than max level");
        skills = new HashMap<>(skills);
        SkillManager.getDefaultConfigs().forEach((key, value) -> {
            this.skills.putIfAbsent(key, value);
        });
        skills = Map.copyOf(skills);
    }
}
