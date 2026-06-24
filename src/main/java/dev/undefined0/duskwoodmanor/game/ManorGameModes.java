package dev.undefined0.duskwoodmanor.game;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.undefined0.duskwoodmanor.DuskwoodManor;
import dev.undefined0.duskwoodmanor.game.gamemode.HitmanGameMode;
import net.minecraft.util.Identifier;

public class ManorGameModes {
    public static final Identifier HITMAN_ID = DuskwoodManor.id("hitman");

    public static final GameMode HITMAN = registerGameMode(HITMAN_ID, new HitmanGameMode(HITMAN_ID));

    public static GameMode registerGameMode(Identifier identifier, GameMode gameMode) {
        DuskwoodManor.LOGGER.info("Registering gamemode {}", identifier);
        WatheGameModes.GAME_MODES.put(identifier, gameMode);
        return gameMode;
    }

    public static void init() {
        DuskwoodManor.LOGGER.info("Added Duskwood gamemodes (hopefully)");
    }
}
