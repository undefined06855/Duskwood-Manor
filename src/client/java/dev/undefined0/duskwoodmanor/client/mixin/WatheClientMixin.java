package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorGameModes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(WatheClient.class)
public class WatheClientMixin {
    // remember here false means visible and true means invisible
    @WrapOperation(method = "getInstinctHighlight", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static boolean isPlayerSpectatingOrCreative(PlayerEntity player, Operation<Boolean> original) {
        var gameComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameComponent.getGameMode() != ManorGameModes.HITMAN) return original.call(player);


        // if we are spectating or creative, they're whatever wathe says
        if (GameFunctions.isPlayerSpectatingOrCreative(MinecraftClient.getInstance().player)) {
            return original.call(player);
        }

        // if their hunter is dead, they're visible
        var playerComponent = HitmanDataComponent.KEY.get(MinecraftClient.getInstance().player);
        var targetComponent = HitmanDataComponent.KEY.get(player);
        if (GameFunctions.isPlayerEliminated(targetComponent.getHunter())) {
            return false;
        }

        // if they're our target, they're visible
        if (playerComponent.getTarget() == player) {
            return false;
        }

        return true;
    }
}
