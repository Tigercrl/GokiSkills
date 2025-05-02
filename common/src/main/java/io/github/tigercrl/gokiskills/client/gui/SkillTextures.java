package io.github.tigercrl.gokiskills.client.gui;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public class SkillTextures {
    private static final SkillTexture.Builder DEFAULT_FRAME_BUILDER = new SkillTexture.Builder()
            .setHoverImage(resource("textures/gui/frame/hover.png"))
            .setMaxLevelImage(resource("textures/gui/frame/max_level.png"))
            .setOperationImage(resource("textures/gui/frame/operation.png"))
            .setOperationHoverImage(resource("textures/gui/frame/operation_hover.png"))
            .setTextureSize(26);
    public static final SkillTexture DEFAULT_OVERLAY = new SkillTexture.Builder()
            .setDefaultImage(resource("textures/gui/overlay/default.png"))
            .setMaxLevelImage(resource("textures/gui/overlay/max_level.png"))
            .setOperationImage(resource("textures/gui/overlay/operation.png"))
            .setTextureSize(24)
            .build();

    public static SkillTexture getFrame(FrameColor color) {
        return DEFAULT_FRAME_BUILDER
                .setDefaultImage(resource("textures/gui/frame/" + color.name().toLowerCase() + ".png")).build();
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
