package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.Skills;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.phys.Vec3;
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

    @Unique
    private static final List<LivingEntity> gokiskills$ignoreEntityHurt = new ArrayList<>();

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"), cancellable = true)
    public void climbBonus(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            SkillInfo info = SkillHelper.getInfo(player);
            if (info.isEnabled(Skills.CLIMBING) &&
                    (entity.horizontalCollision || jumping) &&
                    (
                            onClimbable() || player.getFeetBlockState().is(Blocks.POWDER_SNOW) &&
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
            SkillInfo info = SkillHelper.getInfo(player);
            double jumpBoostBonus = info.isEnabled(Skills.JUMP_BOOST) ? info.getBonus(Skills.JUMP_BOOST) : 0;
            double leaperBonus = info.isEnabled(Skills.LEAPER) ? info.getBonus(Skills.LEAPER) : 0;
            player.setDeltaMovement(
                    player.getDeltaMovement()
                            .multiply(leaperBonus + 1, jumpBoostBonus + 1, leaperBonus + 1)
            );
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
            SkillInfo info = SkillHelper.getInfo(player);
            ItemStack item = player.getMainHandItem();
            if (info.isEnabled(ONE_HIT)) {
                double bonus = info.getBonus(ONE_HIT);
                if (entity.getHealth() < entity.getMaxHealth() * 0.4 * bonus && Math.random() < bonus) {
                    entity.setHealth(0);
                    entity.die(source);
                    player.connection.send(
                            new ClientboundSetActionBarTextPacket(
                                    new TranslatableComponent("skill.gokiskills.one_hit.message")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                            )
                    );
                    player.level.playSound(
                            null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS,
                            1.0f, 1.0f
                    );
                    player.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1, 1);
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
            } else if (info.isEnabled(FENCING) && item.getItem() instanceof SwordItem) {
                double bonus = info.getBonus(FENCING);
                if (bonus > 0) amount *= (float) (1 + bonus);
            }
        }
        // protection
        if (entity instanceof ServerPlayer player && !player.isInvulnerableTo(source) && !player.gameMode.isCreative()) {
            SkillInfo info = SkillHelper.getInfo(player);
            if (info.isEnabled(DODGE) && gokiskills$canProtect(source)) {
                if (Math.random() < info.getBonus(DODGE)) {
                    player.connection.send(
                            new ClientboundSetActionBarTextPacket(
                                    new TranslatableComponent("skill.gokiskills.dodge.message")
                                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                            )
                    );
                    player.level.playSound(
                            null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS,
                            1.0f, 1.0f
                    );
                    ((EntityAccessor) this).setInvulnerableTime(20);
                    cir.setReturnValue(false);
                    return;
                }
            }
            if (info.isEnabled(BLAST_PROTECTION) && source.isExplosion()) {
                double bonus = info.getBonus(BLAST_PROTECTION);
                if (bonus > 0) amount = (float) (amount * (1 - bonus));
            } else if (info.isEnabled(ENDOTHERMY) && (source.isFire() || source.equals(DamageSource.FREEZE))) {
                double bonus = info.getBonus(ENDOTHERMY);
                if (bonus > 0) amount = (float) (amount * (1 - bonus));
            } else if (info.isEnabled(FEATHER_FALLING) && source.isFall()) {
                double bonus = info.getBonus(FEATHER_FALLING);
                if (bonus > 0) amount = Mth.floor(amount * (1 - bonus));
            } else if (info.isEnabled(PROTECTION) && gokiskills$canProtect(source)) {
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
            SkillInfo info = SkillHelper.getInfo(p);
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

    @Unique
    private static boolean gokiskills$canProtect(DamageSource source) {
        return !source.isBypassArmor() ||
                source.msgId.equals("sonic_boom") ||
                source.equals(DamageSource.FLY_INTO_WALL) ||
                source.equals(DamageSource.FREEZE) ||
                source.isFall() ||
                source.isMagic();
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
