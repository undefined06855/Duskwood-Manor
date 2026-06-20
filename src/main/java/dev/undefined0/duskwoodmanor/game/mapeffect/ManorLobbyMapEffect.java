package dev.undefined0.duskwoodmanor.game.mapeffect;

import java.util.List;

import dev.doctor4t.wathe.cca.TrainWorldComponent;
import dev.doctor4t.wathe.game.mapeffect.HarpyExpressTrainMapEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ManorLobbyMapEffect extends HarpyExpressTrainMapEffect {
    public ManorLobbyMapEffect(Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initializeMapEffects(ServerWorld serverWorld, List<ServerPlayerEntity> players) {
        TrainWorldComponent trainWorldComponent = TrainWorldComponent.KEY.get(serverWorld);
        trainWorldComponent.setSnow(false);
        trainWorldComponent.setFog(false);
        trainWorldComponent.setHud(true);
        trainWorldComponent.setSpeed(0);
        trainWorldComponent.setTime(0);
    }

    @Override
    public void finalizeMapEffects(ServerWorld serverWorld, List<ServerPlayerEntity> players) {

    }
}
