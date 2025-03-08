package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
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

import static io.github.tigercrl.gokiskills.skill.Skills.*;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    public void destroySpeedBonus(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        Player p = (Player) (Object) this;
        SkillInfo info = SkillManager.getInfo(p);
        ItemStack item = p.getMainHandItem();
        double bonus = 1.0;
        if (info.isEnabled(CHOPPING) && item.is(ItemTags.AXES) && blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
            bonus += info.getBonus(CHOPPING);
        } else if (info.isEnabled(DIGGING) && item.is(ItemTags.SHOVELS) && blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            bonus += info.getBonus(DIGGING);
        } else if (info.isEnabled(HARVESTING) && item.is(ItemTags.HOES) && blockState.is(BlockTags.MINEABLE_WITH_HOE)) {
            bonus += info.getBonus(HARVESTING);
        } else if (info.isEnabled(MINING) && item.is(ItemTags.PICKAXES) && blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            bonus += info.getBonus(MINING);
        } else if (info.isEnabled(SHEARING) && item.is(Items.SHEARS) && item.getDestroySpeed(blockState) != 1) {
            bonus += info.getBonus(SHEARING);
        }
        cir.setReturnValue(cir.getReturnValue() * (float) bonus);
    }
}
