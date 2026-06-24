package dev.undefined0.duskwoodmanor.cca;

import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

import net.minecraft.entity.player.PlayerEntity;

public class ManorComponents implements WorldComponentInitializer, EntityComponentInitializer {
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CCAModEnforcer.KEY, CCAModEnforcer::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, HitmanDataComponent.KEY, HitmanDataComponent::new);
    }
}
