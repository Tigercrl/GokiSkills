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

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Skill implements ISkill {
    public static final Function<Integer, Double> DEFAULT_CALC_COST = (level) -> Math.pow(level, 1.6) + 6 + level;

    public static final BiFunction<Integer, Function<Integer, Double>, Double> DEFAULT_CALC_RETURN = (level, calcCost) -> calcCost.apply(level - 1);

    public static final Function<Integer, Double> DEFAULT_CALC_BONUS = (level) -> 0.04 * level;

    private final ResourceLocation location;
    private final ResourceLocation category;
    private final Function<Integer, Double> calcCost;
    private final BiFunction<Integer, Function<Integer, Double>, Double> calcReturn;
    @Nullable
    private final Function<Integer, Double> calcBonus;
    private final SkillTexture icon;
    private final SkillTexture frame;
    private final SkillTexture overlay;
    private final SkillTexture background;
    private final Component name;
    private final SkillDescription description;
    private final Class<? extends GokiSkillConfig> configClass;
    private final GokiSkillConfig defaultConfig;

    public Skill(
            ResourceLocation category,
            Function<Integer, Double> calcCost,
            BiFunction<Integer, Function<Integer, Double>, Double> calcReturn,
            @Nullable Function<Integer, Double> calcBonus,
            SkillTexture icon,
            SkillTexture frame,
            SkillTexture overlay,
            SkillTexture background,
            Component name,
            SkillDescription description,
            Class<? extends GokiSkillConfig> configClass,
            GokiSkillConfig defaultConfig
    ) {
        this.category = category;
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
        this.defaultConfig = defaultConfig;
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
        return getConfig().maxLevel;
    }

    @Override
    public int getDefaultLevel() {
        return getConfig().defaultLevel;
    }

    @Override
    public int getMinLevel() {
        return getConfig().minLevel;
    }

    @Override
    public int calcCost(int level) {
        return Math.toIntExact(Math.round(calcCost.apply(level) * getConfig().costMultiplier));
    }

    @Override
    public int calcReturn(int level) {
        return Math.toIntExact(Math.round(calcReturn.apply(level, calcCost) * getConfig().downgradeReturnFactor));
    }

    @Nullable
    @Override
    public Double calcBonus(int level) {
        if (calcBonus == null) return null;
        return calcBonus.apply(level) * getConfig().bonusMultiplier;
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
    public GokiSkillConfig getDefaultConfig() {
        return defaultConfig;
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
                "category=" + category + ']';
    }

    public static class Builder {
        private ResourceLocation category;
        private int maxLevel = 25;
        private int defaultLevel = 0;
        private int minLevel = 0;
        private Function<Integer, Double> calcCost = DEFAULT_CALC_COST;
        private BiFunction<Integer, Function<Integer, Double>, Double> calcReturn = DEFAULT_CALC_RETURN;
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
        private GokiSkillConfig defaultConfig;

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

        public Builder setCalcCost(Function<Integer, Double> calcCost) {
            this.calcCost = calcCost;
            return this;
        }

        public Builder setCalcReturn(BiFunction<Integer, Function<Integer, Double>, Double> calcReturn) {
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

        public Builder setDefaultConfig(GokiSkillConfig defaultConfig) {
            this.defaultConfig = defaultConfig;
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
            if (defaultConfig == null) {
                try {
                    defaultConfig = configClass.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new IllegalStateException("Default config must be set");
                }
            }
            defaultConfig.defaultLevel = defaultLevel;
            defaultConfig.maxLevel = maxLevel;
            defaultConfig.minLevel = minLevel;
            return new Skill(
                    category,
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
                    configClass,
                    defaultConfig
            );
        }
    }

    public interface SkillDescription {
        Component getDescription(int level, @Nullable Double bonus);
    }
}
