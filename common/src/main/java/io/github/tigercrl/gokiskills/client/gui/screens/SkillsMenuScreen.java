package io.github.tigercrl.gokiskills.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.client.gui.components.SkillButton;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SkillsMenuScreen extends Screen {
    private static final Component LOADING = Component.translatable("gui.gokiskills.loading.menu");
    private static final Component LEFT_BOTTOM = System.getProperty("os.name").toLowerCase().contains("mac") ? Component.translatable("gui.gokiskills.help.macos") : Component.translatable("gui.gokiskills.help");
    public static final int HORIZONTAL_SPACING = 10;
    public static final int VERTICAL_SPACING = HORIZONTAL_SPACING + 7;

    public static int playerXp = 0;

    private final Screen parent;
    private long lastUpdated = -1;
    private boolean loaded = false;

    public SkillsMenuScreen(Screen parent) {
        super(Component.translatable("gui.gokiskills.title"));
        this.parent = parent;
    }

    public void onClose() {
        minecraft.setScreen(parent);
    }

    protected void init() {
        // resize window
        if (loaded)
            onLoaded();
    }

    protected void onLoaded() {
        List<List<ISkill>> skills = SkillRegistry.getSortedSkills();
        List<Integer> lineHeight = skills.stream()
                .map(row ->
                        row.stream()
                                .mapToInt(s -> s.getWidgetSize()[1])
                                .max().orElse(SkillButton.DEFAULT_HEIGHT)
                ).toList();
        int height = (skills.size() - 1) * VERTICAL_SPACING + lineHeight.stream().reduce(0, Integer::sum);
        int yStart = (this.height - height) / 2 - 5;
        for (int i = 0; i < skills.size(); i++) {
            List<ISkill> row = skills.get(i);
            int y = yStart + i * VERTICAL_SPACING + lineHeight.stream().limit(i).reduce(0, Integer::sum);
            List<Integer> widths = row.stream()
                    .map(s -> s.getWidgetSize()[0])
                    .toList();
            int width = (row.size() - 1) * HORIZONTAL_SPACING + widths.stream().reduce(0, Integer::sum);
            int xStart = (this.width - width) / 2;
            for (int j = 0; j < row.size(); j++) {
                int x = xStart + j * HORIZONTAL_SPACING + widths.stream().limit(j).reduce(0, Integer::sum);
                ISkill skill = row.get(j);
                addRenderableWidget(skill.getWidget(this, x, y));
            }
        }
    }

    public void tick() {
        super.tick();
        if (!loaded && GokiSkillsClient.serverConfig != null && SkillHelper.getClientInfoOrNull() != null) {
            loaded = true;
            onLoaded();
        }
        playerXp = SkillHelper.getClientTotalXp();
        // update info
        if (GokiSkillsClient.lastPlayerInfoUpdated > lastUpdated) {
            lastUpdated = GokiSkillsClient.lastPlayerInfoUpdated;
            for (int i = 0; i < children().size(); i++) {
                if (children().get(i) instanceof SkillButton button) {
                    button.updateLevel();
                }
            }
        }
    }

    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, font, title, width / 2, 15, 16777215);

        SkillButton.hasControlDown = hasControlDown();
        SkillButton.hasShiftDown = hasShiftDown();
        SkillButton.hasAltDown = hasAltDown();

        if (!loaded) {
            String s = LoadingDotsText.get(Util.getMillis());
            drawCenteredString(poseStack, font, s, width / 2, height / 2 - 6, 8421504);
            drawCenteredString(poseStack, font, LOADING, width / 2, height / 2 + 6, 16777215);
        } else {
            super.render(poseStack, i, j, f);
            Component[] leftBottoms = Arrays.stream(LEFT_BOTTOM.getString().split("\n")).map(Component::literal).toArray(Component[]::new);
            for (int k = 0; k < leftBottoms.length; k++) {
                drawString(
                        poseStack, font, leftBottoms[k], 5,
                        height - font.lineHeight * (leftBottoms.length - k) - 4,
                        16777215
                );
            }
            Component RIGHT_BOTTOM = Component.translatable("gui.gokiskills.xp", SkillHelper.getClientTotalXp());
            drawString(poseStack, font, RIGHT_BOTTOM, width - font.width(RIGHT_BOTTOM) - 4, height - font.lineHeight - 4, 16777215);
            // tooltip
            for (int k = 0; k < children().size(); k++) {
                if (children().get(k) instanceof SkillButton button) {
                    button.renderTooltip(poseStack, i, j);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return super.keyPressed(i, j, k);
    }
}
