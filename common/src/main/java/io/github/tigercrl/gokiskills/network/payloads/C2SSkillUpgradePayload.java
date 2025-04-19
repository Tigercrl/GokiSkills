package io.github.tigercrl.gokiskills.network.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record C2SSkillUpgradePayload(ResourceLocation location) implements CustomPacketPayload {
    public static final Type<C2SSkillUpgradePayload> TYPE = new Type<>(resource("skill_upgrade"));
    public static final StreamCodec<ByteBuf, C2SSkillUpgradePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            C2SSkillUpgradePayload::location,
            C2SSkillUpgradePayload::new
    );

    @Override
    public @NotNull Type<C2SSkillUpgradePayload> type() {
        return TYPE;
    }
}
