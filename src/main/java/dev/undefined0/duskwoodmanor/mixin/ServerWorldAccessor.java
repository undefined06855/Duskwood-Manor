package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;

// currently unused

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
    @Accessor("entityManager")
    ServerEntityManager<Entity> duskwoodmanor$getEntityManager();
}
