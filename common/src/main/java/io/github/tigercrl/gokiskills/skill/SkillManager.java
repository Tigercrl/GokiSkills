package io.github.tigercrl.gokiskills.skill;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import io.github.tigercrl.gokiskills.GokiSkills;
import io.github.tigercrl.gokiskills.client.GokiSkillsClient;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public class SkillManager {
    public static final ResourceKey<Registry<ISkill>> REGISTRY = ResourceKey.createRegistryKey(resource("skills"));
    public static Registry<ISkill> SKILL;
    public static final Map<ServerPlayer, ServerSkillInfo> INFOS = new HashMap<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY, Map<ResourceLocation, Supplier<?>> LOADERS) {
        Bootstrap.checkBootstrapCalled(() -> "registry " + REGISTRY);
        ResourceLocation resourceLocation = REGISTRY.location();
        WritableRegistry<ISkill> writableRegistry = new MappedRegistry<>(REGISTRY, Lifecycle.stable(), false);
        LOADERS.put(resourceLocation, () -> Skills.bootstrap(writableRegistry));
        SKILL = writableRegistry;
        WRITABLE_REGISTRY.register((ResourceKey<WritableRegistry<?>>) (Object) REGISTRY, writableRegistry, RegistrationInfo.BUILT_IN);
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

    /**
     * Calculate the cost of upgrading / downgrading skill
     * @param skill skill
     * @param level current skill level
     * @param xp current experience points
     * @param upgrade is upgrade / downgrade
     * @param fast is fast upgrade / downgrade
     * @return [addLevel, addXp]
     */
    public static int[] calcOperation(ISkill skill, int level, int xp, boolean upgrade, boolean fast) {
        int addXp = 0;
        int addLevel = 0;
        if (upgrade) {
            if (fast) {
                while (level + addLevel < skill.getMaxLevel()) {
                    int thisCost = skill.calcCost(level + addLevel);
                    if (-addXp + thisCost > xp) break;
                    addLevel++;
                    addXp -= thisCost;
                }
                return new int[]{addLevel, addXp};
            } else {
                addXp = skill.calcCost(level);
                if (addXp > xp || level + 1 > skill.getMaxLevel()) return new int[]{0, 0};
                else return new int[]{1, -addXp};
            }
        } else {
            if (fast) {
                while (level + addLevel > skill.getMinLevel()) {
                    addXp += skill.calcReturn(level + addLevel);
                    addLevel--;
                }
                return new int[]{addLevel, addXp};
            } else {
                if (level - 1 < skill.getMinLevel()) {
                    return new int[]{0, 0};
                } else {
                    addXp = skill.calcReturn(level);
                    return new int[]{-1, addXp};
                }
            }
        }
    }

    public static SkillInfo getInfo(Player player) {
        SkillInfo info = new SkillInfo();
        if (player instanceof ServerPlayer p)
            info = INFOS.containsKey(p) ? INFOS.get(p) : new ServerSkillInfo(p);
        if (player.level().isClientSide())
            info = GokiSkillsClient.playerInfo == null ? new SkillInfo() : GokiSkillsClient.playerInfo;
        return info;
    }
}
