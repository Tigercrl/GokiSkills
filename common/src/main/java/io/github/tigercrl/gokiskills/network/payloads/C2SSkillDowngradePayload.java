package io.github.tigercrl.gokiskills.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record C2SSkillDowngradePayload(ResourceLocation location) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SSkillDowngradePayload> TYPE = new CustomPacketPayload.Type<>(resource("skill_downgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillDowngradePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            C2SSkillDowngradePayload::location,
            C2SSkillDowngradePayload::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<C2SSkillDowngradePayload> type() {
        return TYPE;
    }
}
