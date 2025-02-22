package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import io.github.tigercrl.gokiskills.skill.Skills;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract boolean onClimbable();

    @Inject(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"), cancellable = true)
    public void climbBonus(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            SkillInfo info = SkillManager.getInfo(player);
            if (Skills.CLIMBING.isEnabled() &&
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
            SkillInfo info = SkillManager.getInfo(player);
            if (player.isSprinting()) {
                double jumpBoostBonus = Skills.JUMP_BOOST.isEnabled() ? info.getBonus(Skills.JUMP_BOOST) : 0;
                double leaperBonus = Skills.LEAPER.isEnabled() ? info.getBonus(Skills.LEAPER) : 0;
                player.setDeltaMovement(
                        player.getDeltaMovement()
                                .multiply(leaperBonus + 1, jumpBoostBonus + 1, leaperBonus + 1)
                );
            }
        }
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
