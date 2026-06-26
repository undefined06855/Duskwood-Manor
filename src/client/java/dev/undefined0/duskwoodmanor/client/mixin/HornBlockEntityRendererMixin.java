package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.client.render.entity.HornBlockEntityRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(HornBlockEntityRenderer.class)
public class HornBlockEntityRendererMixin {
    @WrapMethod(method = "render")
    private void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Operation<Void> original) {

    }
}
