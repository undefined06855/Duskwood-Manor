package dev.undefined0.duskwoodmanor.cca;

import java.util.UUID;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import dev.undefined0.duskwoodmanor.DuskwoodManor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class HitmanDataComponent implements AutoSyncedComponent {
    public static final ComponentKey<HitmanDataComponent> KEY = ComponentRegistry.getOrCreate(DuskwoodManor.id("player_hitman_data"), HitmanDataComponent.class);
    private final PlayerEntity player;

    private UUID targetPlayer;
    private UUID hunterPlayer;

    public HitmanDataComponent(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getTarget() {
        if (targetPlayer == null || player == null || player.getWorld() == null) return null;
        return player.getWorld().getPlayerByUuid(targetPlayer);
    }

    public PlayerEntity getHunter() {
        if (hunterPlayer == null || player == null || player.getWorld() == null) return null;
        return player.getWorld().getPlayerByUuid(hunterPlayer);
    }

    public void setTarget(PlayerEntity target) {
        this.targetPlayer = target.getUuid();
        KEY.sync(player);
    }

    public void setHunter(PlayerEntity hunter) {
        this.hunterPlayer = hunter.getUuid();
        KEY.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        if (tag.contains("target")) targetPlayer = tag.getUuid("target");
        if (tag.contains("hunter")) hunterPlayer = tag.getUuid("hunter");
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        if (targetPlayer != null) tag.putUuid("target", targetPlayer);
        if (hunterPlayer != null) tag.putUuid("hunter", hunterPlayer);
    }
}
