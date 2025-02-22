package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.client.gui.SkillTexture;
import io.github.tigercrl.gokiskills.client.gui.SkillTextures;
import io.github.tigercrl.gokiskills.client.gui.components.SkillButton;
import io.github.tigercrl.gokiskills.config.GokiSkillConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class Skill implements ISkill {
    public static final Function<Integer, Integer> DEFAULT_CALC_COST = (level) ->
            Math.toIntExact(Math.round((Math.pow(level, 1.6) + 6 + level) * GokiSkills.getConfig().multiplier.costMultiplier));

    public static final Function<Integer, Integer> DEFAULT_CALC_RETURN = (level) ->
            Math.toIntExact(Math.round(DEFAULT_CALC_COST.apply(level - 1) * GokiSkills.getConfig().multiplier.downgradeReturnFactor));

    public static final Function<Integer, Double> DEFAULT_CALC_BONUS = (level) ->
            0.04 * level * GokiSkills.getConfig().multiplier.bonusMultiplier;

    private final ResourceLocation location;
    private final ResourceLocation category;
    private final int maxLevel;
    private final int defaultLevel;
    private final int minLevel;
    private final Function<Integer, Integer> calcCost;
    private final Function<Integer, Integer> calcReturn;
    private final @Nullable Function<Integer, Double> calcBonus;
    private final SkillTexture icon;
    private final SkillTexture frame;
    private final SkillTexture overlay;
    private final SkillTexture background;
    private final Component name;
    private final SkillDescription description;
    private final Class<? extends GokiSkillConfig> configClass;

    public Skill(
            ResourceLocation category,
            int maxLevel,
            int defaultLevel,
            int minLevel,
            Function<Integer, Integer> calcCost,
            Function<Integer, Integer> calcReturn,
            @Nullable Function<Integer, Double> calcBonus,
            SkillTexture icon,
            SkillTexture frame,
            SkillTexture overlay,
            SkillTexture background,
            Component name,
            SkillDescription description,
            Class<? extends GokiSkillConfig> configClass
    ) {
        if (minLevel > defaultLevel || maxLevel < defaultLevel)
            throw new IllegalArgumentException("Invalid level");
        this.category = category;
        this.maxLevel = maxLevel;
        this.defaultLevel = defaultLevel;
        this.minLevel = minLevel;
        this.calcCost = calcCost;
        this.calcReturn = calcReturn;
        this.calcBonus = calcBonus;
        this.icon = icon;
        this.frame = frame;
        this.overlay = overlay;
        this.background = background;
        this.name = name;
        this.description = description;
        this.configClass = configClass;
        this.location = SkillManager.SKILL.getKey(this);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return SkillManager.SKILL.getKey(this);
    }

    @Override
    public boolean isEnabled() {
        return GokiSkills.getConfig() != null && getConfig().enabled;
    }

    @Override
    public ResourceLocation getCategory() {
        return category;
    }

    @Override
    public int getMaxLevel() {
        return Math.max((int) Math.round(maxLevel * GokiSkills.getConfig().multiplier.maxLevelMultiplier), 1);
    }

    @Override
    public int getDefaultLevel() {
        return defaultLevel;
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    @Override
    public int calcCost(int level) {
        return calcCost.apply(level);
    }

    @Override
    public int calcReturn(int level) {
        return calcReturn.apply(level);
    }

    @Nullable
    @Override
    public Double calcBonus(int level) {
        if (calcBonus == null) return null;
        return calcBonus.apply(level);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AbstractWidget getWidget(int x, int y) {
        return new SkillButton(x, y, this);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int[] getWidgetSize() {
        return new int[]{SkillButton.DEFAULT_WIDTH, SkillButton.DEFAULT_HEIGHT};
    }

    public SkillTexture getIcon() {
        return icon;
    }

    public SkillTexture getFrame() {
        return frame;
    }

    public SkillTexture getOverlay() {
        return overlay;
    }

    public SkillTexture getBackground() {
        return background;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription(int level, @Nullable Double bonus) {
        return description.getDescription(level, bonus);
    }

    @Override
    public Class<? extends GokiSkillConfig> getConfigClass() {
        return configClass;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Skill) obj;
        return Objects.equals(this.location, that.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public String toString() {
        return "Skill[" +
                "resourceLocation=" + location + ", " +
                "category=" + category + ", " +
                "maxLevel=" + maxLevel + ", " +
                "defaultLevel=" + defaultLevel + ", " +
                "minLevel=" + minLevel + ", " +
                "calcCost=" + calcCost + ", " +
                "calcReturn=" + calcReturn + ", " +
                "calcBonus=" + calcBonus + ", " +
                "icon=" + icon + ", " +
                "frame=" + frame + ", " +
                "overlay=" + overlay + ", " +
                "background=" + background + ", " +
                "name=" + name + ", " +
                "description=" + description + ", " +
                "configClass=" + configClass + ']';
    }

    public static class Builder {
        private ResourceLocation category;
        private int maxLevel = 25;
        private int defaultLevel = 0;
        private int minLevel = 0;
        private Function<Integer, Integer> calcCost = DEFAULT_CALC_COST;
        private Function<Integer, Integer> calcReturn = DEFAULT_CALC_RETURN;
        @Nullable
        private Function<Integer, Double> calcBonus = DEFAULT_CALC_BONUS;
        private SkillTexture icon;
        private SkillTexture frame;
        private SkillTexture overlay = SkillTextures.DEFAULT_OVERLAY;
        private SkillTexture background;
        //        private SkillResource<Integer> iconBorder = new SkillResource<>.Builder<Integer>()
//                .setDefaultItem(FastColor.ARGB32.color(255, 255, 255, 255))
//                .build();
        private Component name;
        private SkillDescription description;
        private Class<? extends GokiSkillConfig> configClass = GokiSkillConfig.class;

        public Builder setCategory(ResourceLocation category) {
            this.category = category;
            return this;
        }

        public Builder setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder setDefaultLevel(int defaultLevel) {
            this.defaultLevel = defaultLevel;
            return this;
        }

        public Builder setMinLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder setCalcCost(Function<Integer, Integer> calcCost) {
            this.calcCost = calcCost;
            return this;
        }

        public Builder setCalcReturn(Function<Integer, Integer> calcReturn) {
            this.calcReturn = calcReturn;
            return this;
        }

        public Builder setCalcBonus(@Nullable Function<Integer, Double> calcBonus) {
            this.calcBonus = calcBonus;
            return this;
        }

        public Builder setIcon(SkillTexture icon) {
            this.icon = icon;
            return this;
        }

//        public Builder setIcon(SkillImage icon, SkillResource<Integer> iconBorder) {
//            this.icon = icon;
//            this.iconBorder = iconBorder;
//            return this;
//        }

        public Builder setFrame(SkillTexture frame) {
            this.frame = frame;
            return this;
        }

        public Builder setOverlay(SkillTexture overlay) {
            this.overlay = overlay;
            return this;
        }

        public Builder setBackground(SkillTexture background) {
            this.background = background;
            return this;
        }

//        public Builder setIconBorder(SkillResource<Integer> iconBorder) {
//            this.iconBorder = iconBorder;
//            return this;
//        }

        public Builder setName(Component name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(SkillDescription description) {
            this.description = description;
            return this;
        }

        public Builder setConfigClass(Class<? extends GokiSkillConfig> configClass) {
            this.configClass = configClass;
            return this;
        }

        public Skill build() {
            if (category == null) throw new IllegalStateException("Category must be set");
            if (icon == null) throw new IllegalStateException("Icon must be set");
            if (frame == null) throw new IllegalStateException("Frame must be set");
            if (background == null) throw new IllegalStateException("Background must be set");
            if (name == null) throw new IllegalStateException("Name must be set");
            if (description == null) throw new IllegalStateException("Description must be set");
            if (configClass == null) throw new IllegalStateException("Config class must be set");
            return new Skill(
                    category,
                    maxLevel,
                    defaultLevel,
                    minLevel,
                    calcCost,
                    calcReturn,
                    calcBonus,
                    icon,
                    frame,
                    overlay,
                    background,
//                    iconBorder,
                    name,
                    description,
                    configClass
            );
        }
    }

    public interface SkillDescription {
        Component getDescription(int level, @Nullable Double bonus);
    }
}
