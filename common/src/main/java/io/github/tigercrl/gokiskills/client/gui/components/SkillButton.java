package io.github.tigercrl.gokiskills.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tigercrl.gokiskills.client.gui.screens.SkillsMenuScreen;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.skill.Skill;
import io.github.tigercrl.gokiskills.skill.SkillHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class SkillButton extends Button {
    public static final int DEFAULT_WIDTH = 24;
    public static final int DEFAULT_HEIGHT = 24;
    public static final int DEFAULT_ICON_PADDING = 4;
    private static final Component LOADING = Component.translatable("gui.gokiskills.loading.skill");
    private static final Component DISABLED = Component.translatable("gui.gokiskills.disabled")
            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
    private static final Component NO_DOWNGRADE = Component.translatable("gui.gokiskills.downgrade.no")
            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    private static final Component DOWNGRADE = Component.translatable("gui.gokiskills.downgrade")
            .withStyle(Style.EMPTY.withColor(-13658630));
    private static final Component NO_UPGRADE = Component.translatable("gui.gokiskills.upgrade.no")
            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    private static final Component UPGRADE = Component.translatable("gui.gokiskills.upgrade")
            .withStyle(Style.EMPTY.withColor(-11535825));

    public static boolean hasControlDown = false;
    public static boolean hasShiftDown = false;
    public static boolean hasAltDown = false;

    private final Skill skill;
    private boolean waitForUpdate = false;
    public int level = 0;
    public boolean enabled = true;

    public SkillButton(int x, int y, Skill skill) {
        super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, CommonComponents.EMPTY, b -> {
        }, DEFAULT_NARRATION);
        this.skill = skill;
        updateLevel();
    }

    @Override
    public void onPress() {
        if (hasAltDown) {
            waitForUpdate = true;
            GokiNetwork.sendSkillToggle(skill.getLocation());
        } else {
            int[] result = SkillHelper.calcOperation(skill, level, SkillsMenuScreen.playerXp, !hasControlDown, hasShiftDown);
            if (!waitForUpdate && result[0] != 0) {
                if (hasControlDown && hasShiftDown) {
                    if (level > skill.getMinLevel()) {
                        waitForUpdate = true;
                        GokiNetwork.sendSkillFastDowngrade(skill.getLocation());
                    }
                } else if (hasControlDown) {
                    if (level > skill.getMinLevel()) {
                        waitForUpdate = true;
                        GokiNetwork.sendSkillDowngrade(skill.getLocation());
                    }
                } else if (hasShiftDown) {
                    if (level < skill.getMaxLevel()) {
                        waitForUpdate = true;
                        GokiNetwork.sendSkillFastUpgrade(skill.getLocation());
                    }
                } else {
                    if (level < skill.getMaxLevel()) {
                        waitForUpdate = true;
                        GokiNetwork.sendSkillUpgrade(skill.getLocation());
                    }
                }
            }
        }
    }

    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        // button
        boolean isHovered = this.isHovered();
        boolean maxLevel = SkillHelper.getClientInfo().getLevel(skill)
                == skill.getMaxLevel();
        boolean operation = hasControlDown || hasShiftDown || hasAltDown;

        RenderSystem.enableBlend(); // enable transparency
        // bg
        guiGraphics.blit(
                skill.getBackground().getItem(isHovered, maxLevel, operation),
                this.getX(), this.getY(),
                0, 0, 0, width, height,
                skill.getBackground().getTextureWidth(),
                skill.getBackground().getTextureHeight()
        );
        // overlay
        guiGraphics.blit(
                skill.getOverlay().getItem(isHovered, maxLevel, operation),
                this.getX(), this.getY(),
                0, 0, 0, width, height,
                skill.getOverlay().getTextureWidth(),
                skill.getOverlay().getTextureHeight()
        );
        // icon
        guiGraphics.blit(
                skill.getIcon().getItem(isHovered, maxLevel, operation),
                this.getX() + DEFAULT_ICON_PADDING, this.getY() + DEFAULT_ICON_PADDING,
                0, 0, width - DEFAULT_ICON_PADDING * 2, height - DEFAULT_ICON_PADDING * 2,
                skill.getIcon().getTextureWidth(),
                skill.getIcon().getTextureHeight()
        );
        // frame
        guiGraphics.blit(
                skill.getFrame().getItem(isHovered, maxLevel, operation),
                this.getX() - 1, this.getY() - 1,
                0, 0, 0, width + 2, height + 2,
                skill.getFrame().getTextureWidth(),
                skill.getFrame().getTextureHeight()
        );
        RenderSystem.disableBlend();

        // level
        guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                waitForUpdate ? LOADING : (enabled ? Component.literal(level + "/" + skill.getMaxLevel()) : DISABLED),
                getX() + width / 2,
                getY() + height + 3,
                (!waitForUpdate && maxLevel) ? -9145 : 16777215
        );
    }

    public void renderTooltip(GuiGraphics guiGraphics, int i, int j) {
        boolean maxLevel = SkillHelper.getClientInfo().getLevel(skill)
                == skill.getMaxLevel();
        if (isHovered) {
            Component click = null;
            Component cost = null;
            int[] result = SkillHelper.calcOperation(skill, level, SkillsMenuScreen.playerXp, !hasControlDown, hasShiftDown);

            if (hasAltDown) {
                if (enabled) click = Component.translatable("gui.gokiskills.toggle.off")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
                else click = Component.translatable("gui.gokiskills.toggle.on")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
            } else if (hasControlDown) {
                if (result[0] == 0)
                    click = NO_DOWNGRADE;
                else if (hasShiftDown) {
                    click = Component.translatable("gui.gokiskills.downgrade.fast", -result[0])
                            .withStyle(Style.EMPTY.withColor(-13658630));
                    cost = Component.translatable("gui.gokiskills.return", result[1])
                            .withStyle(Style.EMPTY.withColor(-8405510));
                } else {
                    click = DOWNGRADE;
                    cost = Component.translatable("gui.gokiskills.return", result[1])
                            .withStyle(Style.EMPTY.withColor(-8405510));
                }
            } else if (!maxLevel) {
                if (result[0] == 0)
                    click = NO_UPGRADE;
                else if (hasShiftDown) {
                    click = Component.translatable("gui.gokiskills.upgrade.fast", result[0])
                            .withStyle(Style.EMPTY.withColor(-11535825));
                    cost = Component.translatable("gui.gokiskills.cost", -result[1])
                            .withStyle(Style.EMPTY.withColor(-6291570));
                } else {
                    click = UPGRADE;
                    cost = Component.translatable("gui.gokiskills.cost", -result[1])
                            .withStyle(Style.EMPTY.withColor(-6291570));
                }
            }

            List<Component> tooltipComponents = new ArrayList<>();
            tooltipComponents.add(
                    skill.getName().copy()
                            .append(Component.literal(" "))
                            .append(maxLevel ? Component.translatable("gui.gokiskills.max_level")
                                    .withStyle(Style.EMPTY.withColor(enabled ? -9145 : 11184810))
                                    : Component.literal("Lv" + level)
                            )
                            .withStyle(Style.EMPTY.withColor(enabled ? (maxLevel ? -13312 : 16777215) : 11184810))
            );
            tooltipComponents.add(
                    skill.getDescription(level, skill.calcBonus(level)).copy()
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
            );
            if (click != null) tooltipComponents.add(click);
            if (cost != null) tooltipComponents.add(cost);

            guiGraphics.renderTooltip(Minecraft.getInstance().font, tooltipComponents, Optional.empty(), i, j);
        }
    }

    public void updateLevel() {
        level = SkillHelper.getClientInfo().getLevel(skill);
        enabled = SkillHelper.getClientInfo().isEnabled(skill.getLocation());
        waitForUpdate = false;
    }
}
