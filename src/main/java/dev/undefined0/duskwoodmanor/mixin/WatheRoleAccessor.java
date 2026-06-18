package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.doctor4t.wathe.api.Role;

@Mixin(Role.class)
public interface WatheRoleAccessor {
    @Accessor("maxSprintTime")
    void duskwoodmanor$setMaxSprintTime(int maxSprintTime);
}
