package dev.undefined0.duskwoodmanor.game;

import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.api.WatheMapEffects;
import net.minecraft.util.Identifier;
import dev.undefined0.duskwoodmanor.DuskwoodManor;
import dev.undefined0.duskwoodmanor.game.mapeffect.ManorMapEffect;

public class ManorMapEffects {
    public static final Identifier MANOR_ID = DuskwoodManor.id("manor");
    public static final Identifier LOBBY_ID = DuskwoodManor.id("lobby");

    public static final MapEffect MANOR = registerMapEffect(MANOR_ID, new ManorMapEffect(MANOR_ID));
    public static final MapEffect LOBBY = registerMapEffect(LOBBY_ID, new ManorMapEffect(LOBBY_ID));

    public static MapEffect registerMapEffect(Identifier identifier, MapEffect mapEffect) {
        DuskwoodManor.LOGGER.info("Registering map effect {}", identifier);
        WatheMapEffects.MAP_EFFECTS.put(identifier, mapEffect);
        return mapEffect;
    }

    public static void init() {
        DuskwoodManor.LOGGER.info("Added Duskwood map effects (hopefully)");
    }
}
