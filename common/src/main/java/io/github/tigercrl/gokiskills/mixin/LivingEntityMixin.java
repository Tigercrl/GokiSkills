package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.misc.GokiTags;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import io.github.tigercrl.gokiskills.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static io.github.tigercrl.gokiskills.skill.Skills.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract boolean onClimbable();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract void setHealth(float f);

    @Shadow
    @Final
    private AttributeMap attributes;
    @Unique
    private static final List<LivingEntity> gokiskills$ignoreEntityHurt = new ArrayList<>();

    @Unique
    private static Float gokiskills$savedHealth = null;

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readHealth(CompoundTag compoundTag, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player) {
            if (compoundTag.contains("Health", Tag.TAG_ANY_NUMERIC)) {
                float health = compoundTag.getFloat("Health");
                if (getMaxHealth() < health && health <= attributes.getValue(Attributes.MAX_HEALTH))
                    gokiskills$savedHealth = health;
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void initHealth(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player &&
                gokiskills$savedHealth != null &&
                getMaxHealth() >= gokiskills$savedHealth
        ) {
            setHealth(gokiskills$savedHealth);
            gokiskills$savedHealth = null;
        }
    }

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"), cancellable = true)
    public void climbBonus(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            SkillInfo info = SkillManager.getInfo(player);
            if (info.isEnabled(Skills.CLIMBING) &&
                    (entity.horizontalCollision || jumping) &&
                    (
                            onClimbable() || player.getInBlockState().is(Blocks.POWDER_SNOW) &&
                                    PowderSnowBlock.canEntityWalkOnPowderSnow(player)
                    )
            ) {
                double bonus = info.getBonus(Skills.CLIMBING);
                Vec3 vec = player.getDeltaMovement();
                if (bonus > 0 && vec.y > 0) {
                    cir.setReturnValue(new Vec3(vec.x, 0.2 * (1 + bonus), vec.z));
                }
            }
        }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    public void jumpBonus(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            SkillInfo info = SkillManager.getInfo(player);
            if (player.isSprinting()) {
                double jumpBoostBonus = info.isEnabled(Skills.JUMP_BOOST) ? info.getBonus(Skills.JUMP_BOOST) : 0;
                double leaperBonus = info.isEnabled(Skills.LEAPER) ? info.getBonus(Skills.LEAPER) : 0;
                player.setDeltaMovement(
                        player.getDeltaMovement()
                                .multiply(leaperBonus + 1, jumpBoostBonus + 1, leaperBonus + 1)
                );
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        float old = amount;
        if (gokiskills$ignoreEntityHurt.contains(entity)) {
            gokiskills$ignoreEntityHurt.remove(entity);
            return;
        }
        // profession
        if (source.getEntity() instanceof ServerPlayer player) {
            SkillInfo info = SkillManager.getInfo(player);
            ItemStack item = player.getMainHandItem();
            if (info.isEnabled(ONE_HIT)) {
                double bonus = info.getBonus(ONE_HIT);
                if (entity.getHealth() < entity.getMaxHealth() * 0.4 * bonus && Math.random() < bonus) {
                    gokiskills$hurtEntity(entity, source, Float.MAX_VALUE);
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
                    cir.setReturnValue(true);
                }
            }
            if (info.isEnabled(NINJA) && player.isCrouching()) {
                double bonus = info.getBonus(NINJA);
                if (bonus > 0) amount *= (float) (1 + (bonus * 0.25));
            }
            if (info.isEnabled(ARCHER) && item.getItem() instanceof ProjectileWeaponItem) {
                double bonus = info.getBonus(ARCHER);
                if (bonus > 0) amount *= (float) (1 + bonus);
            } else if (info.isEnabled(BOXING) && item.isEmpty()) {
                double bonus = info.getBonus(BOXING);
                if (bonus > 0) amount *= (float) (1 + bonus);
            } else if (info.isEnabled(FENCING) && item.is(ItemTags.SWORDS)) {
                double bonus = info.getBonus(FENCING);
                if (bonus > 0) amount *= (float) (1 + bonus);
            }
        }
        // protection
        if (entity instanceof ServerPlayer player && !player.isInvulnerableTo(source) && !player.gameMode.isCreative()) {
            SkillInfo info = SkillManager.getInfo(player);
            if (info.isEnabled(DODGE) && source.is(GokiTags.CAN_DODGE)) {
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
                    cir.setReturnValue(false);
                }
            }
            if (info.isEnabled(BLAST_PROTECTION) && source.is(DamageTypeTags.IS_EXPLOSION)) {
                double bonus = info.getBonus(BLAST_PROTECTION);
                if (bonus > 0) amount = (float) (amount * (1 - bonus));
            } else if (info.isEnabled(ENDOTHERMY) && (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING))) {
                double bonus = info.getBonus(ENDOTHERMY);
                if (bonus > 0) amount = (float) (amount * (1 - bonus));
            } else if (info.isEnabled(FEATHER_FALLING) && source.is(DamageTypeTags.IS_FALL)) {
                double bonus = info.getBonus(FEATHER_FALLING);
                if (bonus > 0) amount = Mth.floor(amount * (1 - bonus));
            } else if (info.isEnabled(PROTECTION) && source.is(GokiTags.CAN_PROTECT)) {
                double bonus = info.getBonus(PROTECTION);
                if (bonus > 0) amount = (float) (amount * (1 - bonus));
            }
        }
        if (amount == old) {
            return;
        }
        gokiskills$hurtEntity(entity, source, amount);
        cir.setReturnValue(true);
    }

    @Inject(method = "calculateFallDamage", at = @At("RETURN"), cancellable = true)
    public void jumpBoostDamage(float f, float g, CallbackInfoReturnable<Integer> cir) {
        if ((LivingEntity) (Object) this instanceof Player p && cir.getReturnValue() > 0) {
            SkillInfo info = SkillManager.getInfo(p);
            if (info.isEnabled(Skills.JUMP_BOOST)) {
                double bonus = info.getBonus(Skills.JUMP_BOOST);
                if (bonus > 0) {
                    MobEffectInstance mobEffectInstance = p.getEffect(MobEffects.JUMP);
                    int h = mobEffectInstance == null ? 0 : mobEffectInstance.getAmplifier() + 1;
                    cir.setReturnValue(Mth.ceil((f - 3 - h - 3.5 * bonus) * g));
                }
            }
        }
    }

    @Unique
    private static void gokiskills$hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        gokiskills$ignoreEntityHurt.add(entity);
        entity.hurt(source, amount);
    }

//    @Inject(method = "getFluidFallingAdjustedMovement", at = @At("RETURN"), cancellable = true)
//    public void swimBonus(double d, boolean bl, Vec3 vec3, CallbackInfoReturnable<Vec3> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof Player player && player.isSwimming() && Skills.SWIMMING.isEnabled()) {
//            SkillInfo info = SkillManager.getInfo(player);
//            double bonus = info.getBonus(Skills.SWIMMING) * 0.25;
//            if (bonus > 0)
//                cir.setReturnValue(cir.getReturnValue().multiply(bonus + 1, bonus + 1, bonus + 1));
//        }
//    }
}
