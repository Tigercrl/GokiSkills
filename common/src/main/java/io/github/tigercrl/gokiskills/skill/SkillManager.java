package io.github.tigercrl.gokiskills.skill;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import io.github.tigercrl.gokiskills.misc.GokiServerPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SkillManager {
    public static final ResourceKey<Registry<ISkill>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(GokiSkills.MOD_ID, "skills"));
    public static Registry<ISkill> SKILL;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(WritableRegistry<WritableRegistry<?>> writableRegistry, Map<ResourceLocation, Supplier<?>> LOADERS) {
        Lifecycle lifecycle = Lifecycle.stable();
        SKILL = new MappedRegistry<>(SkillManager.REGISTRY, lifecycle, false);
        LOADERS.put(REGISTRY.location(), () -> Skills.bootstrap(SKILL));
        writableRegistry.register((ResourceKey<WritableRegistry<?>>) (Object) SkillManager.REGISTRY, (WritableRegistry<?>) SKILL, lifecycle);
    }

    @Environment(EnvType.CLIENT)
    public static List<List<ISkill>> getSortedSkills() {
        Set<Map.Entry<ResourceKey<ISkill>, ISkill>> skills = SKILL.entrySet();
        Map<ResourceLocation, Map<ResourceLocation, ISkill>> skillCategories = new HashMap<>();
        skills.forEach(entry -> {
            ISkill skill = entry.getValue();
            if (skillCategories.containsKey(skill.getCategory())) {
                skillCategories.get(skill.getCategory()).put(entry.getKey().location(), skill);
            } else {
                Map<ResourceLocation, ISkill> category = new HashMap<>();
                category.put(entry.getKey().location(), skill);
                skillCategories.put(skill.getCategory(), category);
            }
        });
        return skillCategories.entrySet().stream()
                .sorted((e1, e2) ->
                        compareResourceLocation(e1.getKey(), e2.getKey()))
                .map(Map.Entry::getValue)
                .map(map -> map.entrySet().stream()
                        .sorted((e1, e2) ->
                                compareResourceLocation(e1.getKey(), e2.getKey()))
                        .map(Map.Entry::getValue)
                        .filter(ISkill::isEnabled)
                        .toList()
                )
                .filter(list -> !list.isEmpty()).toList();
    }

    @Environment(EnvType.CLIENT)
    public static int compareResourceLocation(ResourceLocation location1, ResourceLocation location2) {
        boolean isGoki1 = location1.getNamespace().equals(GokiSkills.MOD_ID);
        boolean isGoki2 = location2.getNamespace().equals(GokiSkills.MOD_ID);
        // if category namespace is gokiskills, put it first
        if (isGoki1 && !isGoki2) {
            return -1;
        }
        if (!isGoki1 && isGoki2) {
            return 1;
        }
        // compare namespace
        int compare = location1.compareTo(location2);
        if (compare != 0) {
            return compare;
        }
        // compare path
        return location1.getPath().compareTo(location2.getPath());
    }

    public static Map<String, JsonObject> getDefaultConfigs() {
        Map<String, JsonObject> configs = new HashMap<>();
        SKILL.entrySet().forEach(entry -> {
            try {
                configs.put(entry.getKey().location().toString(), ConfigUtils.toJsonObject(entry.getValue().getDefaultConfig()));
            } catch (Exception e) {
                LOGGER.warn("Error creating config for skill {}", entry.getKey().location(), e);
            }
        });
        return Map.copyOf(configs);
    }

    public static SkillInfo getInfo(Player player) {
        if (player.level().isClientSide())
            return GokiSkillsClient.playerInfo == null ? new SkillInfo() : GokiSkillsClient.playerInfo;
        return ((GokiServerPlayer) player).getSkillInfo();
    }
}
