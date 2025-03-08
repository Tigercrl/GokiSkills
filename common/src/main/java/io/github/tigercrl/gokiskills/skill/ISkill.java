package io.github.tigercrl.gokiskills.skill;

import com.mojang.logging.LogUtils;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import io.github.tigercrl.gokiskills.config.GokiSkillConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface ISkill {
    Logger LOGGER = LogUtils.getLogger();

    ResourceLocation getResourceLocation();

    ResourceLocation getCategory(); // used to sort skills in a line

    boolean isEnabled();

    int getMaxLevel();

    int getDefaultLevel();

    int getMinLevel();

    int calcCost(int level); // curr level -> cost

    int calcReturn(int level); // curr level -> return

    @Nullable
    Double calcBonus(int level); // curr level -> bonus

    @Environment(EnvType.CLIENT)
    AbstractWidget getWidget(int x, int y);

    @Environment(EnvType.CLIENT)
    int[] getWidgetSize(); // [width, height]

    Component getName();

    Component getDescription(int level, @Nullable Double bonus);

    Class<? extends GokiSkillConfig> getConfigClass();

    GokiSkillConfig getDefaultConfig();

    ResourceLocation getLocation();

    default <T extends GokiSkillConfig> T getConfig() {
        return (T) ConfigUtils.fromJsonObject(GokiSkills.getConfig().skills.get(getResourceLocation().toString()), getConfigClass());
    }
}
