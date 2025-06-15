package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.misc.GokiPlayer;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.ServerSkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements GokiServerPlayer {
    @Shadow
    public abstract void giveExperiencePoints(int i);

    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Unique
    @NotNull
    private SkillInfo gokiskills$info = new SkillInfo();

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void saveSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.put("GokiSkills", gokiskills$info.toNbt());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("GokiSkills"))
            gokiskills$info = ServerSkillInfo.fromNbt(compoundTag.getCompound("GokiSkills"), (ServerPlayer) (Object) this);
    }

    @Override
    @NotNull
    @Unique
    public SkillInfo getSkillInfo() {
        return gokiskills$info;
    }

    @Override
    @Unique
    public void updateSkill(ISkill skill, boolean upgrade, boolean fast) {
        ServerPlayer p = (ServerPlayer) (Object) this;

        int level = gokiskills$info.getLevel(skill);
        int[] result = SkillInfo.calcOperation(skill, level, ((GokiPlayer) p).getPlayerTotalXp(), upgrade, fast);

        gokiskills$info.setLevel(skill, level + result[0]);
        giveExperiencePoints(result[1]);
        connection.send(
                new ClientboundSetExperiencePacket(
                        p.experienceProgress,
                        p.totalExperience,
                        p.experienceLevel
                )
        );
    }
}
