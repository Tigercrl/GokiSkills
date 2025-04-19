package io.github.tigercrl.gokiskills.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static io.github.tigercrl.gokiskills.GokiSkills.resource;

public class GokiTags {
    public static final TagKey<DamageType> CAN_DODGE = TagKey.create(Registries.DAMAGE_TYPE, resource("can_dodge"));
    public static final TagKey<DamageType> CAN_PROTECT = TagKey.create(Registries.DAMAGE_TYPE, resource("can_protect"));
}
