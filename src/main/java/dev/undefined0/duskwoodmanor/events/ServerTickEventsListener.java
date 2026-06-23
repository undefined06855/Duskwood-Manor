package dev.undefined0.duskwoodmanor.events;

import dev.undefined0.duskwoodmanor.cca.CCAModEnforcer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndWorldTick;
import net.minecraft.server.world.ServerWorld;

public class ServerTickEventsListener implements EndWorldTick {
    @Override
    public void onEndTick(ServerWorld world) {
        CCAModEnforcer.KEY.get(world);
    }
}
