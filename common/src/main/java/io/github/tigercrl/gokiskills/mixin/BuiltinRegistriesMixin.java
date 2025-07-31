package io.github.tigercrl.gokiskills.mixin;

import io.github.tigercrl.gokiskills.skill.SkillRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(BuiltinRegistries.class)
public class BuiltinRegistriesMixin {
    @Shadow
    @Final
    private static WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY;

    @Shadow
    @Final
    private static Map<ResourceLocation, Supplier<?>> LOADERS;

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/BuiltinRegistries;registerSimple(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Supplier;)Lnet/minecraft/core/Registry;", ordinal = 1))
    private static void initSkillRegistry(CallbackInfo ci) {
        SkillRegistry.init(WRITABLE_REGISTRY, LOADERS);
    }
}
