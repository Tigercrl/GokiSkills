package io.github.tigercrl.gokiskills.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.tigercrl.gokiskills.client.gui.screens.SkillsMenuScreen;
import io.github.tigercrl.gokiskills.config.CommonConfig;
import io.github.tigercrl.gokiskills.network.GokiNetwork;
import io.github.tigercrl.gokiskills.skill.SkillInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class GokiSkillsClient {
    public static CommonConfig serverConfig;
    public static SkillInfo playerInfo;
    public static long lastPlayerInfoUpdated = 0;
    private static long nextSendTime = 0;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // key bind
        KeyMapping openMenuKey = new KeyMapping("key.gokiskills.open", InputConstants.Type.KEYSYM, InputConstants.KEY_Y, "key.categories.gokiskills");
        KeyMappingRegistry.register(openMenuKey);
        ClientRawInputEvent.KEY_PRESSED.register((client, keyCode, scanCode, action, modifiers) -> {
            if (client.level != null && client.screen == null) { // in game
                if (openMenuKey.matches(keyCode, scanCode)) {
                    client.setScreen(new SkillsMenuScreen(null));
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.pass();
        });

        // events
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level != null) {
                long now = Util.getMillis();
                if (now > nextSendTime) {
                    if (serverConfig == null) GokiNetwork.sendConfigRequest();
                    if (playerInfo == null) GokiNetwork.sendSkillInfoRequest();
                    nextSendTime = now + 5000;
                }
            }
        });
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            serverConfig = null;
            playerInfo = null;
            lastPlayerInfoUpdated = 0;
        });
        LOGGER.info("GokiSkills initialized on client!");
    }
}
