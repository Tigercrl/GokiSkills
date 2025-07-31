package io.github.tigercrl.gokiskills.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tigercrl.gokiskills.skill.SkillRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(RegistryAccess.class)
public interface RegistryAccessMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;make(Ljava/util/function/Supplier;)Ljava/lang/Object;"))
    private static <T> T initSkillRegistry(Supplier<T> supplier, Operation<T> original) {
        Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> map = new HashMap<>(
                (Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>>) original.call(supplier)
        );
        map.put(SkillRegistry.REGISTRY, new RegistryAccess.RegistryData<>(SkillRegistry.REGISTRY, SkillRegistry.CODEC, SkillRegistry.CODEC));
        return (T) map;
    }
}
