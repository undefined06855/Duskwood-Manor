package dev.undefined0.duskwoodmanor.client.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.doctor4t.wathe.WatheConfig;
import net.minecraft.client.MinecraftClient;

@Mixin(WatheConfig.class)
public class WatheConfigMixin {
    // WatheConfig#writeChanges runs during client init (via MidnightConfig#init),
    // where it applies the locked render distance straight onto
    // MinecraftClient.getInstance().options. On some setups options isn't populated
    // that early, so the direct apply throws a NullPointerException and crashes
    // startup. The render distance is already handled by the OptionLocker#overrideOption
    // call just above, so skip the redundant direct apply while options is still null.
    // See https://github.com/undefined06855/Duskwood-Manor/issues/1
    @Inject(method = "writeChanges", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;options:Lnet/minecraft/client/option/GameOptions;", opcode = Opcodes.GETFIELD), cancellable = true)
    private void duskwoodmanor$skipRenderDistanceApplyWhenOptionsUnavailable(String modid, CallbackInfo ci) {
        if (MinecraftClient.getInstance().options == null) ci.cancel();
    }
}
