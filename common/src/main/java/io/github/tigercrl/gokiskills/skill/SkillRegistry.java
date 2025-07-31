package io.github.tigercrl.gokiskills.skill;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.config.ConfigUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkillRegistry {
    private static final ResourceKey<Registry<ISkill>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(GokiSkills.MOD_ID, "skills"));
    private static Registry<ISkill> SKILL;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY, Map<ResourceLocation, Supplier<?>> LOADERS) {
        Bootstrap.checkBootstrapCalled(() -> "registry " + REGISTRY);
        ResourceLocation resourceLocation = REGISTRY.location();
        WritableRegistry<ISkill> writableRegistry = new MappedRegistry<>(REGISTRY, Lifecycle.stable(), false);
        LOADERS.put(resourceLocation, () -> Skills.bootstrap(writableRegistry));
        SKILL = writableRegistry;
        WRITABLE_REGISTRY.register((ResourceKey<WritableRegistry<?>>) (Object) REGISTRY, writableRegistry, RegistrationInfo.BUILT_IN);
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

    public static ISkill getSkill(ResourceLocation location) {
        return SKILL.get(location);
    }

    public static ResourceLocation getLocation(ISkill skill) {
        return SKILL.getKey(skill);
    }

    public static Set<ISkill> getSkills() {
        return SKILL.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toSet());
    }

    @Environment(EnvType.CLIENT)
    public static List<List<ISkill>> getSortedSkills() {
        Set<ISkill> skills = getSkills();
        Map<ResourceLocation, Map<ResourceLocation, ISkill>> skillCategories = new HashMap<>();
        skills.forEach(skill -> {
            if (skillCategories.containsKey(skill.getCategory())) {
                skillCategories.get(skill.getCategory()).put(skill.getLocation(), skill);
            } else {
                Map<ResourceLocation, ISkill> category = new HashMap<>();
                category.put(skill.getLocation(), skill);
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
}
