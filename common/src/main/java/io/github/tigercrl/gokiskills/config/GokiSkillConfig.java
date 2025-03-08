package io.github.tigercrl.gokiskills.config;

public class GokiSkillConfig implements GokiConfig {
    public boolean enabled = true;
    public double costMultiplier = 1.0;
    public double downgradeReturnFactor = 0.8;
    public double bonusMultiplier = 1.0;
    public int minLevel = 0;
    public int defaultLevel = 0;
    public int maxLevel = 25;

    @Override
    public void validatePostLoad() throws ConfigException {
        if (costMultiplier < 0.0)
            throw new ConfigException("Cost multiplier cannot be negative");
        if (downgradeReturnFactor < 0.0)
            throw new ConfigException("Downgrade return factor cannot be negative");
        if (bonusMultiplier < 0.0)
            throw new ConfigException("Bonus multiplier cannot be negative");
        if (minLevel < defaultLevel)
            throw new ConfigException("Min level cannot be less than default level");
        if (minLevel < 0)
            throw new ConfigException("Min level cannot be negative");
        if (maxLevel < minLevel)
            throw new ConfigException("Max level cannot be less than min level");
    }
}
