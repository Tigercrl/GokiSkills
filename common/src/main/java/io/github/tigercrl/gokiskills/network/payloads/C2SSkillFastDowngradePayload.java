package io.github.tigercrl.gokiskills.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record C2SSkillFastDowngradePayload(ResourceLocation location) implements CustomPacketPayload {
    public static final Type<C2SSkillFastDowngradePayload> TYPE = new Type<>(resource("skill_fast_downgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillFastDowngradePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            C2SSkillFastDowngradePayload::location,
            C2SSkillFastDowngradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillFastDowngradePayload> type() {
        return TYPE;
    }
}
