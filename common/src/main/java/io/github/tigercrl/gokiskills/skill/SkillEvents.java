package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;
import static io.github.tigercrl.gokiskills.skill.Skills.HEALTH;
import static io.github.tigercrl.gokiskills.skill.Skills.KNOCKBACK_RESISTANCE;

public class SkillEvents {
    public static final ResourceLocation HEALTH_SKILL_MODIFIER = resource("health_skill_modifier");
    public static final ResourceLocation KNOCKBACK_RESISTANCE_SKILL_MODIFIER = resource("knockback_resistance_skill_modifier");
    public static final ResourceLocation NINJA_SKILL_MODIFIER = resource("ninja_skill_modifier");

    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(SkillEvents::updateAttributes);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd, reason) -> {
            updateAttributes(player);
            player.setHealth(player.getMaxHealth());
        });
        ServerSkillInfo.UPDATE.register((skill, player, newLevel, oldLevel, info) ->
                updateAttribute(player, info, skill));
        ServerSkillInfo.TOGGLE.register((skill, player, newState, info) ->
                updateAttribute(player, info, skill));
    }

    public static void updateAttributes(ServerPlayer player) {
        SkillInfo info = SkillManager.getInfo(player);
        updateAttribute(player, info, KNOCKBACK_RESISTANCE);
        updateAttribute(player, info, HEALTH);
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill) {
        if (skill == KNOCKBACK_RESISTANCE)
            updateAttribute(
                    player, info,
                    KNOCKBACK_RESISTANCE,
                    Attributes.KNOCKBACK_RESISTANCE,
                    KNOCKBACK_RESISTANCE_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_VALUE
            );
        else if (skill == HEALTH)
            updateAttribute(
                    player, info,
                    HEALTH,
                    Attributes.MAX_HEALTH,
                    HEALTH_SKILL_MODIFIER,
                    AttributeModifier.Operation.ADD_VALUE
            );
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Holder<Attribute> attribute, ResourceLocation location, AttributeModifier.Operation operation) {
        updateAttribute(player, info, skill, attribute, location, operation, true);
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Holder<Attribute> attribute, ResourceLocation location, AttributeModifier.Operation operation, boolean condition) {
        double bonus = info.getBonus(skill);
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier oldModifier = instance.getModifier(location);
        if (condition && info.isEnabled(skill) && bonus > 0) {
            if (oldModifier == null || oldModifier.amount() != bonus) {
                instance.removeModifier(location);
                instance.addTransientModifier(new AttributeModifier(
                        location,
                        bonus,
                        operation
                ));
            }
        } else if (oldModifier != null) {
            instance.removeModifier(location);
        }
    }
}
