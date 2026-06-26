package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.doctor4t.wathe.api.WatheGameModes;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.undefined0.duskwoodmanor.ManorUtils;
import dev.undefined0.duskwoodmanor.cca.HitmanDataComponent;
import dev.undefined0.duskwoodmanor.game.ManorGameModes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// for the record, a lot of this was just copied from wathe
// im not entirely sure how to rewrite this without using wathe code

@Mixin(RoundTextRenderer.class)
public class RoundTextRendererMixin {
    @WrapMethod(method = "renderHud")
    private static void renderHud(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, Operation<Void> original) {
        if (player == null || GameWorldComponent.KEY.get(player.getWorld()).getGameMode() != ManorGameModes.HITMAN) {
            original.call(renderer, player, context);
            return;
        }

        int welcomeTime = RoundTextRendererAccessor.getWelcomeTime();
        int endTime = RoundTextRendererAccessor.getEndTime();
        var component = HitmanDataComponent.KEY.get(player);
        var matrix = context.getMatrices();

        if (welcomeTime > 0) {
            matrix.push();
            matrix.translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 3.5, 0);
            matrix.push();
            matrix.scale(2.6f, 2.6f, 1f);
            if (welcomeTime <= 240) {
                Text welcomeText = Text.translatable("announcement.hitman.welcome");
                context.drawTextWithShadow(renderer, welcomeText, -renderer.getWidth(welcomeText) / 2, -12, 0xB3350B);
            }
            matrix.pop();
            matrix.push();
            matrix.scale(1.2f, 1.2f, 1f);
            if (welcomeTime <= 180) {
                Text premiseText = Text.translatable("announcement.hitman.premise.0", ManorUtils.playerName(component.getTarget()));
                context.drawTextWithShadow(renderer, premiseText, -renderer.getWidth(premiseText) / 2, 0, 0xB3350B);
            }
            matrix.pop();
            matrix.push();
            matrix.scale(1.2f, 1.2f, 1f);
            if (welcomeTime <= 120) {
                Text goalText = Text.translatable("announcement.hitman.premise.1", ManorUtils.playerName(component.getHunter()));
                context.drawTextWithShadow(renderer, goalText, -renderer.getWidth(goalText) / 2, 14, 0xB3350B);
            }
            matrix.pop();
            matrix.push();
            matrix.scale(1f, 1f, 1f);
            if (welcomeTime <= 60) {
                Text goalText = Text.translatable("announcement.hitman.premise.2");
                context.drawTextWithShadow(renderer, goalText, -renderer.getWidth(goalText) / 2, 32, 0xB3350B);
            }
            matrix.pop();
            matrix.pop();
        }


        GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
        if (game.getLooseEndWinner() == null) return;

        if (endTime > 0 && endTime < /* END_DURATION */ 200 - (GameConstants.FADE_TIME * 2) && !game.isRunning() && game.getGameMode() != WatheGameModes.DISCOVERY) {
            PlayerEntity winner = player.getWorld().getPlayerByUuid(game.getLooseEndWinner());
            HitmanDataComponent hitmanData = HitmanDataComponent.KEY.get(winner);

            matrix.push();
            matrix.translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f - 40, 0);

            matrix.push();
            matrix.scale(2.6f, 2.6f, 1f);
            Text endText = Text.translatable("announcement.win.hitman", ManorUtils.playerName(winner));
            context.drawTextWithShadow(renderer, endText, -renderer.getWidth(endText) / 2, -17, 0xd14213);
            matrix.pop();
            matrix.push();
            matrix.scale(1.2f, 1.2f, 1f);
            MutableText winMessage = Text.translatable("game.win.hitman_elimination", ManorUtils.playerName(hitmanData.getTarget()), ManorUtils.playerName(hitmanData.getHunter()));
            context.drawTextWithShadow(renderer, winMessage, -renderer.getWidth(winMessage) / 2, -12, 0xB3350B);
            matrix.pop();

            PlayerListEntry playerEntry = WatheClient.PLAYER_ENTRIES_CACHE.get(winner.getUuid());
            if (playerEntry != null && playerEntry.getSkinTextures().texture() != null) {
                Identifier texture = playerEntry.getSkinTextures().texture();
                RenderSystem.enableBlend();
                matrix.push();
                matrix.translate(-20, 3, 0);
                matrix.scale(5f, 5f, 1f);
                context.drawTexturedQuad(texture, 0, 8, 0, 8, 0, 8 / 64f, 16 / 64f, 8 / 64f, 16 / 64f, 1f, 1f, 1f, 1f);
                matrix.translate(-0.5, -0.5, 0);
                matrix.scale(1.125f, 1.125f, 1f);
                context.drawTexturedQuad(texture, 0, 8, 0, 8, 0, 40 / 64f, 48 / 64f, 8 / 64f, 16 / 64f, 1f, 1f, 1f, 1f);
                matrix.pop();
            }

            matrix.pop();
        }
    }

    @WrapMethod(method = "tick")
    private static void tick(Operation<Void> original) {
        var player = MinecraftClient.getInstance().player;
        if (player == null || GameWorldComponent.KEY.get(player.getWorld()).getGameMode() != ManorGameModes.HITMAN) {
            original.call();
            return;
        }

        int welcomeTime = RoundTextRendererAccessor.getWelcomeTime();
        int endTime = RoundTextRendererAccessor.getEndTime();

        if (welcomeTime > 0) {
            if (player != null) {
                switch (welcomeTime) {
                    case 260 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_RISER, SoundCategory.MASTER, 10f, 1f, player.getRandom().nextLong());
                    }
                    case 240 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.25f, player.getRandom().nextLong());
                    }
                    case 180 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.5f, player.getRandom().nextLong());
                    }
                    case 120 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 1.75f, player.getRandom().nextLong());
                    }
                    case 60 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_PIANO, SoundCategory.MASTER, 10f, 2.f, player.getRandom().nextLong());
                    }
                    case 1 -> {
                        player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), WatheSounds.UI_PIANO_STINGER, SoundCategory.MASTER, 10f, 1f, player.getRandom().nextLong());
                    }
                }
            }

            welcomeTime--;
        }

        GameWorldComponent game = GameWorldComponent.KEY.get(player.getWorld());
        PlayerEntity winner = player.getWorld().getPlayerByUuid(game.getLooseEndWinner());

        if (endTime > 0) {
            if (player != null) {
                if (endTime == /* END_DURATION */ 200 - (GameConstants.FADE_TIME * 2)) {
                    player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), player == winner ? WatheSounds.UI_PIANO_WIN : WatheSounds.UI_PIANO_LOSE, SoundCategory.MASTER, 10f, 1f, player.getRandom().nextLong());
                }
            }

            endTime--;
        }

        GameOptions options = MinecraftClient.getInstance().options;
        if (options != null && options.playerListKey.isPressed()) endTime = Math.max(2, endTime);

        RoundTextRendererAccessor.setWelcomeTime(welcomeTime);
        RoundTextRendererAccessor.setEndTime(endTime);
    }

    // set by AnnounceWelcomePayloadReceiverMixin now
    // @Inject(method = "startWelcome", at = @At("TAIL"))
    // private static void startWelcome(RoleAnnouncementTexts.RoleAnnouncementText role, int killers, int targets, CallbackInfo ci) {
    //     ((RoundTextRendererAccessor)(Object)RoundTextRenderer.class).setWelcomeTime(340);
    // }
}
