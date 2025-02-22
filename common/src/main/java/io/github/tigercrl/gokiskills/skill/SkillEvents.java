package io.github.tigercrl.gokiskills.skill;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.tigercrl.gokiskills.misc.GokiTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.tigercrl.gokiskills.skill.Skills.*;

public class SkillEvents {
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("ac28fc4a-8ee1-4998-87b6-cdfe8754b2d6");
    public static final UUID KNOCKBACK_RESISTANCE_MODIFIER_UUID = UUID.fromString("44352496-cb58-4ce3-a261-facd73f08190");
    public static final UUID NINJA_SPEED_MODIFIER_UUID = UUID.fromString("a1e60be0-0511-45a3-aa37-5b217b23c9ad");
    public static final List<LivingEntity> ignoreEntityHurt = new ArrayList<>();

    public static void register() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            float old = amount;
            if (ignoreEntityHurt.contains(entity)) {
                ignoreEntityHurt.remove(entity);
                return EventResult.pass();
            }
            // profession
            if (source.getEntity() instanceof ServerPlayer player) {
                SkillInfo info = SkillManager.getInfo(player);
                ItemStack item = player.getMainHandItem();
                if (ONE_HIT.isEnabled()) {
                    double bonus = info.getBonus(ONE_HIT);
                    if (entity.getHealth() < entity.getMaxHealth() * 0.4 * bonus && Math.random() < bonus) {
                        hurtEntity(entity, source, Float.MAX_VALUE);
                        player.connection.send(
                                new ClientboundSetActionBarTextPacket(
                                        Component.translatable("skill.gokiskills.one_hit.message")
                                                .withStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                                )
                        );
                        player.level().playSound(
                                null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                                1.0f, 1.0f
                        );
                        player.playSound(SoundEvents.PLAYER_ATTACK_CRIT);
                        return EventResult.interruptFalse();
                    }
                }
                if (NINJA.isEnabled() && player.isCrouching()) {
                    double bonus = info.getBonus(NINJA);
                    if (bonus > 0) amount *= (float) (1 + (bonus * 0.25));
                }
                if (ARCHER.isEnabled() && item.getItem() instanceof ProjectileWeaponItem) {
                    double bonus = info.getBonus(ARCHER);
                    if (bonus > 0) amount *= (float) (1 + bonus);
                } else if (BOXING.isEnabled() && item.isEmpty()) {
                    double bonus = info.getBonus(BOXING);
                    if (bonus > 0) amount *= (float) (1 + bonus);
                } else if (FENCING.isEnabled() && item.is(ItemTags.SWORDS)) {
                    double bonus = info.getBonus(FENCING);
                    if (bonus > 0) amount *= (float) (1 + bonus);
                }
            }
            // protection
            if (entity instanceof ServerPlayer player && !player.isInvulnerableTo(source) && !player.gameMode.isCreative()) {
                SkillInfo info = SkillManager.getInfo(player);
                if (DODGE.isEnabled() && source.is(GokiTags.CAN_DODGE)) {
                    if (Math.random() < info.getBonus(DODGE)) {
                        player.connection.send(
                                new ClientboundSetActionBarTextPacket(
                                        Component.translatable("skill.gokiskills.dodge.message")
                                                .withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                                )
                        );
                        player.level().playSound(
                                null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS,
                                1.0f, 1.0f
                        );
                        return EventResult.interruptFalse();
                    }
                }
                if (BLAST_PROTECTION.isEnabled() && source.is(DamageTypeTags.IS_EXPLOSION)) {
                    double bonus = info.getBonus(BLAST_PROTECTION);
                    if (bonus > 0) amount = (float) (amount * (1 - bonus));
                } else if (ENDOTHERMY.isEnabled() && (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING))) {
                    double bonus = info.getBonus(ENDOTHERMY);
                    if (bonus > 0) amount = (float) (amount * (1 - bonus));
                } else if (FEATHER_FALLING.isEnabled() && source.is(DamageTypeTags.IS_FALL)) {
                    double bonus = info.getBonus(FEATHER_FALLING);
                    if (bonus > 0) amount = Mth.floor(amount * (1 - bonus));
                } else if (PROTECTION.isEnabled() && source.is(GokiTags.CAN_PROTECT)) {
                    double bonus = info.getBonus(PROTECTION);
                    if (bonus > 0) amount = (float) (amount * (1 - bonus));
                }
            }
            if (amount == old) {
                return EventResult.pass();
            }
            hurtEntity(entity, source, amount);
            return EventResult.interruptFalse();
        });
        PlayerEvent.PLAYER_JOIN.register(SkillEvents::updateAttributes);
        PlayerEvent.PLAYER_RESPAWN.register((player, conqueredEnd) -> {
            updateAttributes(player);
            player.setHealth(player.getMaxHealth());
        });
        ServerSkillInfo.UPDATE.register((skill, player, newLevel, oldLevel, info) -> {
            if (skill == KNOCKBACK_RESISTANCE)
                updateAttribute(
                        player, info,
                        KNOCKBACK_RESISTANCE,
                        Attributes.KNOCKBACK_RESISTANCE,
                        KNOCKBACK_RESISTANCE_MODIFIER_UUID,
                        "GokiSkills knockback resistance",
                        AttributeModifier.Operation.ADDITION
                );
            else if (skill == HEALTH)
                updateAttribute(
                        player, info,
                        HEALTH,
                        Attributes.MAX_HEALTH,
                        HEALTH_MODIFIER_UUID,
                        "GokiSkills health",
                        AttributeModifier.Operation.ADDITION
                );
        });
    }

    public static void updateAttributes(ServerPlayer player) {
        SkillInfo info = SkillManager.getInfo(player);
        updateAttribute(
                player, info,
                KNOCKBACK_RESISTANCE,
                Attributes.KNOCKBACK_RESISTANCE,
                KNOCKBACK_RESISTANCE_MODIFIER_UUID,
                "GokiSkills knockback resistance",
                AttributeModifier.Operation.ADDITION
        );
        updateAttribute(
                player, info,
                HEALTH,
                Attributes.MAX_HEALTH,
                HEALTH_MODIFIER_UUID,
                "GokiSkills health",
                AttributeModifier.Operation.ADDITION
        );
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Attribute attribute, UUID uuid, String name, AttributeModifier.Operation operation) {
        updateAttribute(player, info, skill, attribute, uuid, name, operation, true);
    }

    public static void updateAttribute(ServerPlayer player, SkillInfo info, ISkill skill, Attribute attribute, UUID uuid, String name, AttributeModifier.Operation operation, boolean condition) {
        double bonus = info.getBonus(skill);
        AttributeInstance instance = player.getAttribute(attribute);
        AttributeModifier oldModifier = instance.getModifier(uuid);
        if (condition && skill.isEnabled() && bonus > 0) {
            if (oldModifier == null || oldModifier.getAmount() != bonus) {
                instance.removeModifier(uuid);
                instance.addTransientModifier(new AttributeModifier(
                        uuid,
                        name,
                        bonus,
                        operation
                ));
            }
        } else if (oldModifier != null) {
            instance.removeModifier(oldModifier);
        }
    }

    public static void hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        ignoreEntityHurt.add(entity);
        entity.hurt(source, amount);
    }
}
