package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.undefined0.duskwoodmanor.ManorUtils;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorGameModes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Mixin(GameFunctions.class)
public class GameFunctionsKillMixin {
    @WrapMethod(method = "Ldev/doctor4t/wathe/game/GameFunctions;killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V")
    private static void killPlayer(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier deathReason, Operation<Void> original) {
        var gameWorldComponent = GameWorldComponent.KEY.get(victim.getWorld());
        if (gameWorldComponent.getGameMode() != ManorGameModes.HITMAN) {
            original.call(victim, spawnBody, killer, deathReason);
            return;
        }

        // indirect = dark, hunter = red
        // direct = light , target = green

        int darkGreen = 0x0b5920;
        int darkRed = 0x590b1c;

        int lightGreen = 0x3bc22f;
        int lightRed = 0xc24a2f;

        var victimComponent = HitmanDataComponent.KEY.get(victim);

        if (killer == null) {
            victimComponent.getTarget().sendMessage(Text.translatable("tip.hitman.hunter_down_indirect").withColor(darkRed));
            victimComponent.getHunter().sendMessage(Text.translatable("tip.hitman.target_down_indirect").withColor(darkGreen));
            original.call(victim, spawnBody, killer, deathReason);
            return;
        }

        var killerComponent = HitmanDataComponent.KEY.get(killer);

        if (killerComponent.getTarget() == victim) {
            // killed the right guy, cool
            original.call(victim, spawnBody, killer, deathReason);

            killer.sendMessage(Text.translatable("tip.hitman.target_down", ManorUtils.playerName(killer)).withColor(lightGreen));
            victimComponent.getTarget().sendMessage(Text.translatable("tip.hitman.hunter_down_indirect").withColor(darkRed));
        } else if (killerComponent.getHunter() == victim) {
            // killed your hunter
            original.call(victim, spawnBody, killer, deathReason);

            killer.sendMessage(Text.translatable("tip.hitman.hunter_down", ManorUtils.playerName(killer)).withColor(lightRed));
            victimComponent.getHunter().sendMessage(Text.translatable("tip.hitman.victim_down_indirect").withColor(darkGreen));
        } else if (GameFunctions.isPlayerEliminated(killerComponent.getHunter())) {
            // killed someone with no hunder
            original.call(victim, spawnBody, killer, deathReason);

            killer.sendMessage(Text.translatable("tip.hitman.no_hunter", ManorUtils.playerName(victim)).withColor(lightGreen));
            victimComponent.getTarget().sendMessage(Text.translatable("tip.hitman.hunter_down_indirect").withColor(darkRed));
        } else {
            // you stupid
            original.call(killer, true, null, GameConstants.DeathReasons.GENERIC);

            killer.sendMessage(Text.translatable("tip.hitman.incorrect_target", ManorUtils.playerName(victim)).formatted(Formatting.RED));
            killerComponent.getHunter().sendMessage(Text.translatable("tip.hitman.target_down_indirect"));
            killerComponent.getTarget().sendMessage(Text.translatable("tip.hitman.hunter_down_indirect").withColor(darkRed));
        }
    }
}
