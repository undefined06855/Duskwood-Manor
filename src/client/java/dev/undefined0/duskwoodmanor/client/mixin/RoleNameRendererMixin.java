package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorGameModes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.EntityHitResult;

@Mixin(RoleNameRenderer.class)
public class RoleNameRendererMixin {
    @WrapOperation(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private static MutableText translatable(String key, Operation<MutableText> original) {
        if (key != "game.tip.cohort" || GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld()).getGameMode() != ManorGameModes.HITMAN) {
            return original.call(key);
        }

        var player = MinecraftClient.getInstance().player;
        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8f : 2f;
        if (ProjectileUtil.getCollision(player, entity -> entity instanceof PlayerEntity, range) instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof PlayerEntity target) {
            var hitman = HitmanDataComponent.KEY.get(player);

            if (hitman.getTarget() == target) {
                return original.call("game.tip.hitman.target");
            }

            if (hitman.getHunter() == target) {
                return original.call("game.tip.hitman.hunter");
            }
        }

        return original.call(key);
    }
}
