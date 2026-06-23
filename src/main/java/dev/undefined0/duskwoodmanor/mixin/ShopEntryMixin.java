package dev.undefined0.duskwoodmanor.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

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

    @WrapMethod(method = "insertStackInFreeSlot")
    private static boolean insertStackInFreeSlot(PlayerEntity player, ItemStack stackToInsert, Operation<Boolean> original) {
        if (!stackToInsert.isOf(WatheItems.NOTE)) {
            return original.call(player, stackToInsert);
        }

        // prevent you having more than two stacks of notes
        int noteStackCount = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(WatheItems.NOTE)) {
                noteStackCount++;
                if (noteStackCount == 2) return false;
            }
        }

        return original.call(player, stackToInsert);
    }
}
