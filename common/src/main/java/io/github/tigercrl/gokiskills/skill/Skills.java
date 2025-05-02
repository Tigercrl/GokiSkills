package io.github.tigercrl.gokiskills.skill;

import io.github.tigercrl.gokiskills.client.gui.SkillTexture;
import io.github.tigercrl.gokiskills.client.gui.SkillTextures;
import io.github.tigercrl.gokiskills.misc.GokiUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public class Skills {
    private static final ResourceLocation abilityCategory = resource("1_ability");
    private static final ResourceLocation breakingCategory = resource("3_breaking");
    private static final ResourceLocation professionCategory = resource("2_profession");
    private static final ResourceLocation protectionCategory = resource("4_protection");

    public static final ISkill CLIMBING = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.ORANGE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/ladder.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/acacia_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.climbing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.climbing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill FORTUNE = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RAINBOW))
            .setMaxLevel(3)
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/goki.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/stone.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.fortune.name"))
            .setDescription((level, bonus) -> Component.translatable("skill.gokiskills.fortune.desc"))
            .setCalcCost(level -> 25 * Math.pow(level, 2) + 25 * level + 100)
            .build();

    public static final ISkill HEALTH = new Skill.Builder()
            .setCategory(abilityCategory)
            .setMaxLevel(40)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/instant_health.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/crimson_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.health.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.health.desc",
                            GokiUtils.doubleToString(bonus, 0)
                    )
            )
            .setCalcCost(level -> Math.pow(level, 2) + 48 + level)
            .setCalcBonus(Double::valueOf)
            .build();

    public static final ISkill LEAPER = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/leaper.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/birch_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.leaper.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.leaper.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill SWIMMING = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_BLUE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/mob_effect/dolphins_grace.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/background/water.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.swimming.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.swimming.desc",
                            GokiUtils.doubleToString(bonus * 100, 2),
                            GokiUtils.doubleToString(bonus * 25, 2)
                    )
            )
            .build();

    public static final ISkill JUMP_BOOST = new Skill.Builder()
            .setCategory(abilityCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/mob_effect/jump_boost.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/birch_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.jump_boost.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.jump_boost.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill CHOPPING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.BROWN))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/diamond_axe.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/oak_log.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.chopping.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.chopping.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final ISkill DIGGING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_GRAY))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/diamond_shovel.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/gravel.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.digging.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.digging.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final ISkill HARVESTING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/diamond_hoe.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/hay_block_side.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.harvesting.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.harvesting.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final ISkill MINING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.GRAY))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/diamond_pickaxe.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/stone.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.mining.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.mining.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();
    public static final ISkill SHEARING = new Skill.Builder()
            .setCategory(breakingCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/shears.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/yellow_wool.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.shearing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.shearing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill ALCHEMY = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.YELLOW))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/raw_iron.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/gold_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.alchemy.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.alchemy.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill ARCHER = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/bow_pulling_0.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/background/archer.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.archer.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.archer.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill BOXING = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/boxing.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/bricks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.boxing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.boxing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill FENCING = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.PINK))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/iron_sword.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/cherry_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.fencing.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.fencing.desc",
                            GokiUtils.doubleToString(bonus * 100, 2)
                    )
            )
            .build();

    public static final ISkill NINJA = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.BLACK))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/ninja.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/chiseled_polished_blackstone.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.ninja.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.ninja.desc",
                            GokiUtils.doubleToString(bonus * 100, 2),
                            GokiUtils.doubleToString(bonus * 25, 2)
                    )
            )
            .build();

    public static final ISkill ONE_HIT = new Skill.Builder()
            .setCategory(professionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/sickle.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/red_terracotta.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.one_hit.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.one_hit.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2),
                            GokiUtils.doubleToString(Math.min(bonus * 40, 100), 2)
                    )
            )
            .setCalcBonus((level) -> 0.01 * level)
            .build();

    public static final ISkill BLAST_PROTECTION = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.RED))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/creeper.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/tnt_side.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.blast_protection.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.blast_protection.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final ISkill DODGE = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/icon/wind_charged.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(resource("textures/gui/background/pale_oak_planks.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.dodge.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.dodge.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.006 * level)
            .build();

    public static final ISkill ENDOTHERMY = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.LIGHT_BLUE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/mob_effect/fire_resistance.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/ice.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.endothermy.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.endothermy.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final ISkill FEATHER_FALLING = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/item/feather.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/sand.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.feather_falling.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.feather_falling.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.026 * level)
            .build();

    public static final ISkill KNOCKBACK_RESISTANCE = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.WHITE))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/mob_effect/absorption.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/iron_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.knockback_resistence.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.knockback_resistence.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.013 * level)
            .build();

    public static final ISkill PROTECTION = new Skill.Builder()
            .setCategory(protectionCategory)
            .setFrame(SkillTextures.getFrame(SkillTextures.FrameColor.GRAY))
            .setIcon(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/mob_effect/resistance.png"))
                            .setTextureSize(16)
                            .build()
            )
            .setBackground(
                    new SkillTexture.Builder()
                            .setDefaultImage(ResourceLocation.withDefaultNamespace("textures/block/netherite_block.png"))
                            .setTextureSize(24)
                            .build()
            )
            .setName(Component.translatable("skill.gokiskills.protection.name"))
            .setDescription((level, bonus) ->
                    Component.translatable(
                            "skill.gokiskills.protection.desc",
                            GokiUtils.doubleToString(Math.min(bonus * 100, 100), 2)
                    )
            )
            .setCalcBonus(level -> 0.008 * level)
            .build();

    public static ISkill bootstrap(Registry<ISkill> registry) {
        register(registry, "climbing", CLIMBING);
//        register(registry, "fortune", FORTUNE);
        register(registry, "health", HEALTH);
        register(registry, "jump_boost", JUMP_BOOST);
        register(registry, "leaper", LEAPER);
//        register(registry, "swimming", SWIMMING);

        register(registry, "chopping", CHOPPING);
        register(registry, "digging", DIGGING);
        register(registry, "harvesting", HARVESTING);
        register(registry, "mining", MINING);
        register(registry, "shearing", SHEARING);

//        register(registry, "alchemy", ALCHEMY);
        register(registry, "archer", ARCHER);
        register(registry, "boxing", BOXING);
        register(registry, "fencing", FENCING);
        register(registry, "ninja", NINJA);
        register(registry, "one_hit", ONE_HIT);

        register(registry, "blast_protection", BLAST_PROTECTION);
        register(registry, "dodge", DODGE);
        register(registry, "endothermy", ENDOTHERMY);
        register(registry, "feather_falling", FEATHER_FALLING);
        register(registry, "knockback_resistence", KNOCKBACK_RESISTANCE);
        return register(registry, "protection", PROTECTION);
    }

    private static ISkill register(Registry<ISkill> registry, String path, ISkill skill) {
        return Registry.register(registry, resource(path), skill);
    }
}
