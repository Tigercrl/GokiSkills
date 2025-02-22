package io.github.tigercrl.gokiskills.client.gui;

import io.github.tigercrl.gokiskills.GokiSkills;
import net.minecraft.resources.ResourceLocation;

public class SkillTextures {
    private static final SkillTexture.Builder DEFAULT_FRAME_BUILDER = new SkillTexture.Builder()
            .setHoverImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/frame/hover.png"))
            .setMaxLevelImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/frame/max_level.png"))
            .setOperationImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/frame/operation.png"))
            .setOperationHoverImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/frame/operation_hover.png"))
            .setTextureSize(26);
    public static final SkillTexture DEFAULT_OVERLAY = new SkillTexture.Builder()
            .setDefaultImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/overlay/default.png"))
            .setMaxLevelImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/overlay/max_level.png"))
            .setOperationImage(new ResourceLocation(GokiSkills.MOD_ID, "textures/gui/overlay/operation.png"))
            .setTextureSize(24)
            .build();

    public static SkillTexture getFrame(FrameColor color) {
        return DEFAULT_FRAME_BUILDER
                .setDefaultImage(
                        new ResourceLocation(
                                GokiSkills.MOD_ID,
                                "textures/gui/frame/" + color.name().toLowerCase() + ".png"
                        )
                ).build();
    }

    public enum FrameColor {
        BLACK,
        BLUE,
        BROWN,
        CYAN,
        GRAY,
        GREEN,
        LIGHT_BLUE,
        LIGHT_GRAY,
        LIME,
        MAGENTA,
        ORANGE,
        PINK,
        PURPLE,
        RED,
        WHITE,
        YELLOW,
        RAINBOW
    }
}
