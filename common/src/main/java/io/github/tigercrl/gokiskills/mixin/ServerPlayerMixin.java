package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import io.github.tigercrl.gokiskills.network.S2CSyncSkillInfoMessage;
import io.github.tigercrl.gokiskills.skill.ISkill;
import io.github.tigercrl.gokiskills.skill.SkillHelper;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void saveSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag tag = SkillHelper.getInfo((Player) (Object) this).toNbt();
        if (tag != null) compoundTag.put("GokiSkills", tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        Player p = (Player) (Object) this;
        SkillHelper.setSkillInfo(p, SkillInfo.fromNbt(p, compoundTag.getCompound("GokiSkills")));
    }

    @Override
    @Unique
    public void updateSkill(ISkill skill, boolean upgrade, boolean fast) {
        ServerPlayer p = (ServerPlayer) (Object) this;
        SkillInfo info = SkillHelper.getInfo(p);

        int level = info.getLevel(skill);
        int[] result = SkillHelper.calcOperation(skill, level, SkillHelper.getTotalXp(p), upgrade, fast);

        info.setLevel(skill, level + result[0]);
        giveExperiencePoints(result[1]);
        connection.send(
                new ClientboundSetExperiencePacket(
                        p.experienceProgress,
                        p.totalExperience,
                        p.experienceLevel
                )
        );
    }

    @Override
    @Unique
    public void syncSkillInfo() {
        ServerPlayer p = (ServerPlayer) (Object) this;
        new S2CSyncSkillInfoMessage(SkillHelper.getInfo(p)).sendTo(p);
    }
}
