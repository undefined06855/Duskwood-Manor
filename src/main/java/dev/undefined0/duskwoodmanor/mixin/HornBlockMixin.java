package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.api.GameMode;
import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.block.HornBlock;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.undefined0.duskwoodmanor.game.ManorMapEffects;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Mixin(HornBlock.class)
public class HornBlockMixin {
    @ModifyReturnValue(method = "getOutlineShape", at = @At("RETURN"))
    public VoxelShape getOutlineShape(VoxelShape original) {
        return Block.createCuboidShape(
            4.0, 4.0, 14.0,
            12.0, 12.0, 16.0
        );
    }

    @WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/game/GameFunctions;startGame(Lnet/minecraft/server/world/ServerWorld;Ldev/doctor4t/wathe/api/GameMode;Ldev/doctor4t/wathe/api/MapEffect;I)V"))
    private static void startGame(ServerWorld world, GameMode gameMode, MapEffect mapEffect, int time, Operation<Void> original) {
        original.call(world, WatheGameModes.MURDER, ManorMapEffects.MANOR, time);
    }

    @WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void playSound(World instance, PlayerEntity source, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        if (sound != WatheSounds.AMBIENT_TRAIN_HORN) return;
        original.call(instance, source, x, y, z, sound, category, volume, pitch);
    }
}
