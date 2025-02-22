package io.github.tigercrl.gokiskills.misc;

import io.github.tigercrl.gokiskills.GokiSkills;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class GokiTags {
    public static final TagKey<DamageType> CAN_DODGE = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GokiSkills.MOD_ID, "can_dodge"));
    public static final TagKey<DamageType> CAN_PROTECT = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GokiSkills.MOD_ID, "can_protect"));
}
