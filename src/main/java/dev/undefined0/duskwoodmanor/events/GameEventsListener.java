package dev.undefined0.duskwoodmanor.events;

import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.api.event.GameEvents;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.MapVariablesWorldComponent;
import dev.doctor4t.wathe.cca.WorldBlackoutComponent.BlackoutDetails;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheProperties;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

// doctor4t this is a great mod and i love it but can the time of day system be a LITTLE better??
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

        // reset all the shit
        MapVariablesWorldComponent areas = MapVariablesWorldComponent.KEY.get(world);

        Box area = areas.playArea;
        for (int x = (int) area.minX; x <= (int) area.maxX; x++) {
            for (int y = (int) area.minY; y <= (int) area.maxY; y++) {
                for (int z = (int) area.minZ; z <= (int) area.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    
                }
            }
        }
    }
}
