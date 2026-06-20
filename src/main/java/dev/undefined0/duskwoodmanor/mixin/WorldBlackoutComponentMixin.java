package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.cca.WorldBlackoutComponent;
import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.util.math.random.Random;

@Mixin(WorldBlackoutComponent.class)
public class WorldBlackoutComponentMixin {
    @WrapOperation(method = "triggerBlackout", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private static int blackoutDuration(Random instance, int bounds, Operation<Integer> original) {
        int newMaxDuration = GameConstants.getInTicks(0, 30);
        return original.call(instance, newMaxDuration - GameConstants.BLACKOUT_MIN_DURATION);
    }
}
