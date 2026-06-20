package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;

@Mixin(ShopEntry.class)
public class ShopEntryMixin {
    @ModifyReturnValue(method = "price", at = @At("RETURN"))
    private int price(int original) {
        var self = (ShopEntry)(Object)this;

        if (self.stack().getItem() == WatheItems.NOTE) {
            return 0;
        }

        if (self.stack().getItem() == WatheItems.BLACKOUT) {
            return 150;
        }

        return original;
    }
}
