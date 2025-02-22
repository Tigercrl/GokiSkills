package io.github.tigercrl.gokiskills.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.tigercrl.gokiskills.GokiSkills;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GokiSkills.MOD_ID)
public final class GokiSkillsForge {
    public GokiSkillsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(GokiSkills.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        GokiSkills.init();
    }
}
