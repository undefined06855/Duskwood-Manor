package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public class ArmorStandMixin {
    @WrapMethod(method = "interactAt")
    private ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand, Operation<ActionResult> original) {
        if (player.isCreative() || player.isSpectator()) {
            return original.call(player, hitPos, hand);
        }

        // don;t allow interacting with armour stands in survival or adventure
        return ActionResult.PASS;
    }
}
