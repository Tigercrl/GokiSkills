package io.github.tigercrl.gokiskills.client.gui.screens;

import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.client.gui.components.SkillButton;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SkillsMenuScreen extends Screen {
    private static final Component LOADING = Component.translatable("gui.gokiskills.loading.menu");
    private static final Component LEFT_BOTTOM_1 = System.getProperty("os.name").toLowerCase().contains("mac") ? Component.translatable("gui.gokiskills.help.1.macos") : Component.translatable("gui.gokiskills.help.1");
    private static final Component LEFT_BOTTOM_2 = Component.translatable("gui.gokiskills.help.2");
    public static final int HORIZONTAL_SPACING = 11;
    public static final int VERTICAL_SPACING = 20;

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
        List<List<ISkill>> skills = SkillManager.getSortedSkills();
        List<Integer> lineHeight = skills.stream()
                .map(row ->
                        row.stream()
                                .mapToInt(s -> s.getWidgetSize()[1])
                                .max().orElse(SkillButton.DEFAULT_HEIGHT)
                ).toList();
        int height = (skills.size() - 1) * VERTICAL_SPACING + lineHeight.stream().reduce(0, Integer::sum);
        int yStart = (this.height - height) / 2;
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
                addRenderableWidget(skill.getWidget(x, y));
            }
        }
    }

    public void tick() {
        super.tick();
        if (!loaded && GokiSkillsClient.serverConfig != null && GokiSkillsClient.playerInfo != null) {
            loaded = true;
            onLoaded();
        }
        playerXp = GokiUtils.getPlayerTotalXp(minecraft.player);
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

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        SkillButton.hasControlDown = hasControlDown();
        SkillButton.hasShiftDown = hasShiftDown();
        SkillButton.hasAltDown = hasAltDown();

        if (!loaded) {
            this.renderBackground(guiGraphics, i, j, f);
            String s = LoadingDotsText.get(Util.getMillis());
            guiGraphics.drawCenteredString(font, s, width / 2, height / 2 - 6, 8421504);
            guiGraphics.drawCenteredString(font, LOADING, width / 2, height / 2 + 6, 16777215);
        } else {
            super.render(guiGraphics, i, j, f);
            Component RIGHT_BOTTOM = Component.translatable("gui.gokiskills.xp", GokiUtils.getPlayerTotalXp(minecraft.player));
            guiGraphics.drawString(font, LEFT_BOTTOM_1, 4, height - font.lineHeight * 2 - 4, 16777215);
            guiGraphics.drawString(font, LEFT_BOTTOM_2, 4, height - font.lineHeight - 4, 16777215);
            guiGraphics.drawString(font, RIGHT_BOTTOM, width - font.width(RIGHT_BOTTOM) - 4, height - font.lineHeight - 4, 16777215);
            // tooltip
            for (int k = 0; k < children().size(); k++) {
                if (children().get(k) instanceof SkillButton button) {
                    button.renderTooltip(guiGraphics, i, j);
                }
            }
        }

        guiGraphics.drawCenteredString(font, title, width / 2, 15, 16777215);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return super.keyPressed(i, j, k);
    }
}
