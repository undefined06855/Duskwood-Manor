package dev.undefined0.duskwoodmanor.cca;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.undefined0.duskwoodmanor.DuskwoodManor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.World;

// easy way to force the mod on client and server

public class CCAModEnforcer implements AutoSyncedComponent {
    public static final ComponentKey<CCAModEnforcer> KEY = ComponentRegistry.getOrCreate(DuskwoodManor.id("really_important_i_promise_v2"), CCAModEnforcer.class);
    private final World world;

    public CCAModEnforcer(World world) {
        this.world = world;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    }
}
