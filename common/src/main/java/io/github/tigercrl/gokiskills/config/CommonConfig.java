package io.github.tigercrl.gokiskills.config;

import com.google.gson.JsonObject;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class CommonConfig implements GokiConfig {
    public LostLevelOnDeath lostLevelOnDeath = new LostLevelOnDeath();
    public Map<String, JsonObject> skills = SkillManager.getDefaultConfigs();

    public static class LostLevelOnDeath {
        public boolean enabled = false;
        public double chance = 0.5;
        public int minLevel = 1;
        public int maxLevel = 1;
    }

    @Override
    public void validatePostLoad() throws ConfigException {
        if (lostLevelOnDeath.chance < 0.0 || lostLevelOnDeath.chance > 1.0)
            throw new ConfigException("Lost level on death chance must be between 0.0 and 1.0");
        if (lostLevelOnDeath.minLevel < 0 || lostLevelOnDeath.maxLevel < 0)
            throw new ConfigException("Lost level on death levels cannot be negative");
        if (lostLevelOnDeath.minLevel > lostLevelOnDeath.maxLevel)
            throw new ConfigException("Lost level on death min level cannot be greater than max level");
        skills = new HashMap<>(skills);
        SkillManager.getDefaultConfigs().forEach((key, value) -> this.skills.putIfAbsent(key, value));
        skills.forEach((key, value) -> {
            try {
                ISkill skill = SkillManager.SKILL.get(ResourceLocation.tryParse(key));
                GokiSkillConfig config = ConfigUtils.fromJsonObject(value, skill.getConfigClass());

                // v1.0.0
                if (!value.has("minLevel")) {
                    GokiSkillConfig defaultConfig = skill.getDefaultConfig();
                    boolean enabled = config.enabled;
                    config = defaultConfig;
                    config.enabled = enabled;
                }

                config.validatePostLoad();
                skills.put(key, ConfigUtils.toJsonObject(config));
            } catch (ConfigException e) {
                throw new ConfigException("Invalid skill config for " + key + ": " + e.getMessage());
            }
        });
        skills = Map.copyOf(skills);
    }
}
