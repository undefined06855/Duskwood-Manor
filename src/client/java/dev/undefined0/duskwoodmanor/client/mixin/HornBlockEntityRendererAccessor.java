package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.doctor4t.wathe.client.render.entity.HornBlockEntityRenderer;
import net.minecraft.client.render.block.BlockRenderManager;

@Mixin(HornBlockEntityRenderer.class)
public interface HornBlockEntityRendererAccessor {
    @Accessor("renderManager")
    BlockRenderManager duskwoodmanor$getRenderManager();
}
