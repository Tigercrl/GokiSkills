package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.misc.GokiPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static io.github.tigercrl.gokiskills.misc.GokiUtils.getTotalXpNeededForLevel;
import static io.github.tigercrl.gokiskills.misc.GokiUtils.getXpNeededForNextLevel;

@Mixin(Player.class)
public abstract class PlayerMixin implements GokiPlayer {
    @Shadow
    public int experienceLevel;

    @Shadow
    public float experienceProgress;

    @Override
    @Unique
    public int getPlayerTotalXp() {
        return getTotalXpNeededForLevel(experienceLevel) +
                Mth.floor(experienceProgress * getXpNeededForNextLevel(experienceLevel));
    }
}
