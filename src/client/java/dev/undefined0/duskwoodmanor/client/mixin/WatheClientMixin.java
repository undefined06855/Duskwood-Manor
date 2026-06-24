package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorGameModes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(WatheClient.class)
public class WatheClientMixin {
    @WrapOperation(method = "getInstinctHighlight", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static boolean isPlayerSpectatingOrCreative(PlayerEntity player, Operation<Boolean> original) {
        var gameComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameComponent.getGameMode() != ManorGameModes.HITMAN) return original.call(player);

        // if they're not our target, they're """spectating"""
        var playerComponent = HitmanDataComponent.KEY.get(MinecraftClient.getInstance().player);
        if (playerComponent.getTarget() != player) return true;
        else return false;
    }
}
