package dev.undefined0.duskwoodmanor.client;

import dev.undefined0.duskwoodmanor.client.events.ClientTickEventsListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class DuskwoodManorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(new ClientTickEventsListener());
    }
}
