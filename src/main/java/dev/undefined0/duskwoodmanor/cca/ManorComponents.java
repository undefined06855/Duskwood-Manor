package dev.undefined0.duskwoodmanor.cca;

import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class ManorComponents implements WorldComponentInitializer {
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CCAModEnforcer.KEY, CCAModEnforcer::new);
    }
}
