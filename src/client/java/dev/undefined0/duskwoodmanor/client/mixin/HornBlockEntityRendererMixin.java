package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.client.render.entity.HornBlockEntityRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(HornBlockEntityRenderer.class)
public class HornBlockEntityRendererMixin {
    @WrapMethod(method = "render")
    private void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Operation<Void> original) {
        var accessor = (HornBlockEntityRendererAccessor)(Object)this;

        matrices.push();
        // float pull = Easing.CUBIC_IN.ease((entity instanceof HornBlockEntity plushie ? (float) MathHelper.lerp(tickDelta, plushie.prevPull, plushie.pull) : 0), 0, 1, 1) / 2f;
        // matrices.translate(0, -pull, 0);
        BlockState state = entity.getCachedState();
        (accessor.duskwoodmanor$getRenderManager()).getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(state, false)), state, accessor.duskwoodmanor$getRenderManager().getModel(state), 0xFF, 0xFF, 0xFF, light, overlay);

        // matrices.push();
        // BlockState chain = Blocks.CHAIN.getDefaultState();
        // matrices.translate(0, 1, 0);
        // ((BlockRenderManagerAccessor) this.renderManager).getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(chain, false)), chain, this.renderManager.getModel(chain), 0xFF, 0xFF, 0xFF, light, overlay);
        // matrices.translate(0, 1, 0);
        // ((BlockRenderManagerAccessor) this.renderManager).getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(RenderLayers.getEntityBlockLayer(chain, false)), chain, this.renderManager.getModel(chain), 0xFF, 0xFF, 0xFF, light, overlay);
        // matrices.pop();

        matrices.pop();
    }
}
