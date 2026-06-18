package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;

@Mixin(ShopEntry.class)
public class ShopEntryMixin {
    @Inject(method = "price", at = @At("HEAD"))
    private int price(CallbackInfoReturnable<Integer> ci) {
        var self = (ShopEntry)(Object)this;

        if (self.stack().getItem() == WatheItems.NOTE) {
            ci.setReturnValue(0);
        }

        if (self.stack().getItem() == WatheItems.BLACKOUT) {
            ci.setReturnValue(150);
        }

        return ci.getReturnValue();
    }
}
