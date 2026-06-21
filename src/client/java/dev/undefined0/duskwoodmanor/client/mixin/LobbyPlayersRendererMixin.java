package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.doctor4t.wathe.client.gui.LobbyPlayersRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Mixin(LobbyPlayersRenderer.class)
public class LobbyPlayersRendererMixin {
    @WrapOperation(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private static int drawTextWithShadow(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original) {
        if (x == 10) {
            // text in bottom left, shift it up 40px for our additions
            y -= 40;
        }

        if (y == 0) {
            // text at the top, check if resource pack is loaded!!
            if (!Text.translatable("credits.wathe.thank_you").getString().contains("- somewhat_grand")) {
                for (int i = 0; i < 6; i++) {
                    Text line = Text.translatable("lobby.resource_pack_unloaded." + i);
                    original.call(instance, textRenderer, line, -textRenderer.getWidth(line) / 2, 40 + (i * 10), 0xFFA81111);
                }
            }
        }

        return original.call(instance, textRenderer, text, x, y, color);
    }
}
