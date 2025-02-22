package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import io.github.tigercrl.gokiskills.skill.Skills;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    public void destroySpeedBonus(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        Player p = (Player) (Object) this;
        SkillInfo info = SkillManager.getInfo(p);
        ItemStack item = p.getMainHandItem();
        double bonus = 1.0;
        if (Skills.CHOPPING.isEnabled() && item.is(ItemTags.AXES) && blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
            bonus += info.getBonus(Skills.CHOPPING);
        } else if (Skills.DIGGING.isEnabled() && item.is(ItemTags.SHOVELS) && blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            bonus += info.getBonus(Skills.DIGGING);
        } else if (Skills.HARVESTING.isEnabled() && item.is(ItemTags.HOES) && blockState.is(BlockTags.MINEABLE_WITH_HOE)) {
            bonus += info.getBonus(Skills.HARVESTING);
        } else if (Skills.MINING.isEnabled() && item.is(ItemTags.PICKAXES) && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            bonus += info.getBonus(Skills.MINING);
        } else if (Skills.SHEARING.isEnabled() && item.is(Items.SHEARS) && item.getDestroySpeed(blockState) != 1) {
            bonus += info.getBonus(Skills.SHEARING);
        }
        cir.setReturnValue(cir.getReturnValue() * (float) bonus);
    }
}
