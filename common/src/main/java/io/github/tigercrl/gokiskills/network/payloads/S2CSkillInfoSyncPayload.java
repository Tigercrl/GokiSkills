package io.github.tigercrl.gokiskills.network.payloads;

import io.github.tigercrl.gokiskills.skill.SkillInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record S2CSkillInfoSyncPayload(SkillInfo info) implements CustomPacketPayload {
    public static final Type<S2CSkillInfoSyncPayload> TYPE = new Type<>(resource("skill_info_sync"));
    public static final StreamCodec<ByteBuf, S2CSkillInfoSyncPayload> STREAM_CODEC = StreamCodec.composite(
            SkillInfo.STREAM_CODEC,
            S2CSkillInfoSyncPayload::info,
            S2CSkillInfoSyncPayload::new
    );

    @Override
    public @NotNull Type<S2CSkillInfoSyncPayload> type() {
        return TYPE;
    }
}
