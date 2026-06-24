package dev.undefined0.duskwoodmanor.game;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.api.Role.MoodType;
import dev.undefined0.duskwoodmanor.DuskwoodManor;

public class ManorRoles {
    public static final Role HITMAN = registerRole(new Role(DuskwoodManor.id("hitman"), 0xB3350B, false, true, MoodType.NONE, -1, true));

    public static Role registerRole(Role role) {
        DuskwoodManor.LOGGER.info("Registering role {}", role.identifier());
        WatheRoles.ROLES.add(role);
        return role;
    }

    public static void init() {
        DuskwoodManor.LOGGER.info("Added Duskwood roles (hopefully)");
    }
}
