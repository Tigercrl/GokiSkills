package io.github.tigercrl.gokiskills.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record C2SSkillInfoRequestPayload() implements CustomPacketPayload {
    public static final Type<C2SSkillInfoRequestPayload> TYPE = new Type<>(resource("skill_info_request"));
    public static final StreamCodec<ByteBuf, C2SSkillInfoRequestPayload> STREAM_CODEC = StreamCodec.unit(new C2SSkillInfoRequestPayload());

    @Override
    public @NotNull Type<C2SSkillInfoRequestPayload> type() {
        return TYPE;
    }
}
