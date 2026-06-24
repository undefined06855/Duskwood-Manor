package dev.undefined0.duskwoodmanor.events;

import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.world.World;

// doctor4t this is a great mod and i love it but can the time of day system be a LITTLE better??
// edit: it has now come to my attention my issues with it were because of a typo i made. i am sincerely sorry rat and
// i hope to amend this by making the mod use less instrusive mixins just kidding im making this mod as intrusive as it
// needs to be
public class GameEventsListener implements GameEvents.OnFinishFinalize, GameEvents.OnFinishInitialize {
    @Override
    public void onFinishInitialize(World world, GameWorldComponent gameComponent) {
        if (world.isClient) return;
        var serverWorld = world.getServer().getWorld(world.getRegistryKey());
        if (gameComponent.getGameMode() == WatheGameModes.DISCOVERY) {
            serverWorld.setTimeOfDay(6000);
        } else {
            serverWorld.setTimeOfDay(18000);
        }
    }

    @Override
    public void onFinishFinalize(World world, GameWorldComponent gameComponent) {
        if (world.isClient) return;
        world.getServer().getWorld(world.getRegistryKey()).setTimeOfDay(12800);
    }
}
