package dev.undefined0.duskwoodmanor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.doctor4t.wathe.api.event.GameEvents;
import dev.undefined0.duskwoodmanor.events.GameEventsListener;
import dev.undefined0.duskwoodmanor.events.UseBlockListener;
import dev.undefined0.duskwoodmanor.game.ManorMapEffects;

public class DuskwoodManor implements ModInitializer {
    public static final String MOD_ID = "duskwoodmanor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Duskwood Manor by somewhat_grand, mod by undefined0! Have fun!");

        var modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get();
        var ret = ResourceManagerHelper.registerBuiltinResourcePack(DuskwoodManor.id("duskwood_resources"), modContainer, Text.translatable(MOD_ID, "pack.duskwood_manor"), ResourcePackActivationType.ALWAYS_ENABLED);
        if (!ret) {
            LOGGER.error("Failed to register builtin resource pack! Why? No idea, Fabric won't tell me lmao");
        }

        ManorMapEffects.init();

        // im not actually sure this is how you're meant to use events like this? a lambda couldve definitely been used
        // but it's nice to have them in their own files and stuff like this
        GameEvents.ON_FINISH_FINALIZE.register(new GameEventsListener());
        GameEvents.ON_FINISH_INITIALIZE.register(new GameEventsListener());
        UseBlockCallback.EVENT.register(new UseBlockListener());
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
