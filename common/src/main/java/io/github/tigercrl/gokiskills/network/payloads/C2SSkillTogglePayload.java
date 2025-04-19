package io.github.tigercrl.gokiskills.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record C2SSkillTogglePayload(ResourceLocation location) implements CustomPacketPayload {
    public static final Type<C2SSkillTogglePayload> TYPE = new Type<>(resource("skill_toggle"));
    public static final StreamCodec<ByteBuf, C2SSkillTogglePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            C2SSkillTogglePayload::location,
            C2SSkillTogglePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillTogglePayload> type() {
        return TYPE;
    }
}
