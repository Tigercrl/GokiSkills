package io.github.tigercrl.gokiskills;

import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.config.CommonConfig;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.skill.ServerSkillInfo;
import io.github.tigercrl.gokiskills.skill.SkillEvents;
import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public final class GokiSkills {
    public static final String MOD_ID = "gokiskills";
    public static CommonConfig config;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // client
        if (Platform.getEnv() == EnvType.CLIENT)
            GokiSkillsClient.init();

        // events
        LifecycleEvent.SETUP.register(() -> {
            // config
            config = ConfigUtils.readConfig(MOD_ID + "-common", CommonConfig.class);
            // log skills
            StringBuilder sb = new StringBuilder();
            sb.append("Loaded skills: ");
            SkillManager.SKILL.keySet().forEach(key -> sb.append(key.toString()).append(", "));
            LOGGER.info(sb.delete(sb.length() - 2, sb.length()).toString());
        });
        PlayerEvent.PLAYER_JOIN.register(player -> {
            SkillManager.INFOS.putIfAbsent(player, new ServerSkillInfo(player));
            GokiNetwork.sendConfigSync(player);
            GokiNetwork.sendSkillInfoSync(player);
        });
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(level -> {
            SkillManager.INFOS.clear();
        });
        EntityEvent.LIVING_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer player)
                SkillManager.getInfo(player).onDeath(player);
            return EventResult.pass();
        });
        SkillEvents.register();

        LOGGER.info("GokiSkills initialized!");
    }

    public static CommonConfig getConfig() {
        if (Platform.getEnv() == EnvType.CLIENT &&
                Minecraft.getInstance().level != null &&
                !Minecraft.getInstance().level.isClientSide
        )
            return GokiSkillsClient.serverConfig;
        return config;
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
