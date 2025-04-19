package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

import static io.github.tigercrl.gokiskills.skill.Skills.HEALTH;
import static io.github.tigercrl.gokiskills.skill.Skills.KNOCKBACK_RESISTANCE;

public class SkillEvents {
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("ac28fc4a-8ee1-4998-87b6-cdfe8754b2d6");
    public static final UUID KNOCKBACK_RESISTANCE_MODIFIER_UUID = UUID.fromString("44352496-cb58-4ce3-a261-facd73f08190");
    public static final UUID NINJA_SPEED_MODIFIER_UUID = UUID.fromString("a1e60be0-0511-45a3-aa37-5b217b23c9ad");

    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(SkillEvents::updateAttributes);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd) -> {
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
                    KNOCKBACK_RESISTANCE_MODIFIER_UUID,
                    "GokiSkills knockback resistance",
                    AttributeModifier.Operation.ADD_VALUE
            );
        else if (skill == HEALTH)
            updateAttribute(
                    player, info,
                    HEALTH,
                    Attributes.MAX_HEALTH,
                    HEALTH_MODIFIER_UUID,
                    "GokiSkills health",
                    AttributeModifier.Operation.ADD_VALUE
            );
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Holder<Attribute> attribute, UUID uuid, String name, AttributeModifier.Operation operation) {
        updateAttribute(player, info, skill, attribute, uuid, name, operation, true);
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Holder<Attribute> attribute, UUID uuid, String name, AttributeModifier.Operation operation, boolean condition) {
        double bonus = info.getBonus(skill);
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier oldModifier = instance.getModifier(uuid);
        if (condition && info.isEnabled(skill) && bonus > 0) {
            if (oldModifier == null || oldModifier.amount() != bonus) {
                instance.removeModifier(uuid);
                instance.addTransientModifier(new AttributeModifier(
                        uuid,
                        name,
                        bonus,
                        operation
                ));
            }
        } else if (oldModifier != null) {
            instance.removeModifier(uuid);
        }
    }
}
