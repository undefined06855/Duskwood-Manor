package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.game.GameConstants;

@Mixin(WatheRoles.class)
public class WatheRolesMixin {
    @Inject(method = "registerRole", at = @At(value = "HEAD"))
    private static Role registerRole(Role role, CallbackInfoReturnable<Role> ci) {
        var accessor = ((WatheRoleAccessor) (Object) role);
        if (role == WatheRoles.CIVILIAN || role == WatheRoles.VIGILANTE) {
            accessor.duskwoodmanor$setMaxSprintTime(GameConstants.getInTicks(0, 20));
        }

        return ci.getReturnValue();
    }
}
