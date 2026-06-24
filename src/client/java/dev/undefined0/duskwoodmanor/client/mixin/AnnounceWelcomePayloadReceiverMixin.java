package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import dev.undefined0.duskwoodmanor.DuskwoodManor;
import dev.undefined0.duskwoodmanor.game.gamemode.HitmanGameMode;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Mixin(AnnounceWelcomePayload.Receiver.class)
public class AnnounceWelcomePayloadReceiverMixin {
    @WrapMethod(method = "receive")
    private void receive(AnnounceWelcomePayload payload, ClientPlayNetworking.Context context, Operation<Void> original) {
        if (payload.role() != HitmanGameMode.FAKE_HITMAN_ID) {
            original.call(payload, context);
            return;
        }

        DuskwoodManor.LOGGER.info("announce welcome payload");

        RoundTextRendererAccessor.setWelcomeTime(340);
    }
}
