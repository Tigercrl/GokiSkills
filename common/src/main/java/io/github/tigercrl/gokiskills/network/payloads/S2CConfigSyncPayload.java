package io.github.tigercrl.gokiskills.network.payloads;

import io.github.tigercrl.gokiskills.config.CommonConfig;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public record S2CConfigSyncPayload(CommonConfig config) implements CustomPacketPayload {
    public static final Type<S2CConfigSyncPayload> TYPE = new Type<>(resource("config_sync"));
    public static final StreamCodec<ByteBuf, S2CConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ConfigUtils.streamCodecOf(CommonConfig.class),
            S2CConfigSyncPayload::config,
            S2CConfigSyncPayload::new
    );

    @Override
    public @NotNull Type<S2CConfigSyncPayload> type() {
        return TYPE;
    }
}
