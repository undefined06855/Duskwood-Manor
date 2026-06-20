package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.cca.TrainWorldComponent;
import net.minecraft.server.world.ServerWorld;

@Mixin(TrainWorldComponent.class)
public class TrainWorldComponentMixin {
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    private static void setTimeOfDay(ServerWorld instance, long time, Operation<Void> original) {
        // do not call original, we hardcode override setting time of day in FinishFinalizeListener
        // FUCK doctor4t's time of day system it is NON FUNCTIONAL!!!!!!!
    }
}
