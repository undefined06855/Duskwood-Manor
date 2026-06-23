package dev.undefined0.duskwoodmanor.client.events;

import dev.undefined0.duskwoodmanor.cca.CCAModEnforcer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndWorldTick;
import net.minecraft.client.world.ClientWorld;

public class ClientTickEventsListener implements EndWorldTick {
    @Override
    public void onEndTick(ClientWorld world) {
        CCAModEnforcer.KEY.get(world);
    }
}
