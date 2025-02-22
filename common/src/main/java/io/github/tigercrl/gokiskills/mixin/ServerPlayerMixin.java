package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.ServerSkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void saveSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        SkillInfo info = SkillManager.getInfo((ServerPlayer) (Object) this);
        if (info == null) info = new SkillInfo();
        compoundTag.put("GokiSkills", info.toNbt());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readSkillsInfo(CompoundTag compoundTag, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        SkillManager.INFOS.put(player, ServerSkillInfo.fromNbt(compoundTag.getCompound("GokiSkills")).toServerSkillInfo(player));
    }
}
