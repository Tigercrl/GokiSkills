package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillManager;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Shadow @Final private static WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY;

    @Shadow @Final private static Map<ResourceLocation, Supplier<?>> LOADERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void initSkillRegistry(CallbackInfo ci) {
        SkillManager.init(WRITABLE_REGISTRY, LOADERS);
    }
}
