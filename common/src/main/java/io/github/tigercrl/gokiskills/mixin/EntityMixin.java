package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillHooks;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.Skills;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setShiftKeyDown", at = @At("HEAD"))
    public void ninjaSpeedBonus(boolean bl, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ServerPlayer player) {
            SkillInfo info = SkillHelper.getInfo(player);
            SkillHooks.updateAttribute(
                    player, info,
                    Skills.NINJA,
                    Attributes.MOVEMENT_SPEED,
                    SkillHooks.NINJA_SPEED_MODIFIER_UUID,
                    "GokiSkills ninja speed",
                    AttributeModifier.Operation.MULTIPLY_TOTAL,
                    bl
            );
        }
    }

//    @Inject(method = "getMaxAirSupply", at = @At("RETURN"), cancellable = true)
//    public void oxygenConsumptionBonus(CallbackInfoReturnable<Integer> cir) {
//        Entity entity = (Entity) (Object) this;
//        if (entity instanceof Player player && Skills.SWIMMING.isEnabled()) {
//            SkillInfo info = SkillManager.getInfo(player);
//            double bonus = info.getBonus(Skills.SWIMMING);
//            if (bonus > 0)
//                cir.setReturnValue(Math.toIntExact(Math.round(cir.getReturnValue() * (1 + bonus * 0.25))));
//        }
//    }
}
