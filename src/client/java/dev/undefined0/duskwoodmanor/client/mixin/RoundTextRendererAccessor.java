package dev.undefined0.duskwoodmanor.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.doctor4t.wathe.client.gui.RoundTextRenderer;

@Mixin(RoundTextRenderer.class)
public interface RoundTextRendererAccessor {
    @Accessor("welcomeTime")
    static int getWelcomeTime() {
        throw new AssertionError();
    }

    @Accessor("welcomeTime")
    static void setWelcomeTime(int welcomeTime) {
        throw new AssertionError();
    }

    @Accessor("endTime")
    static int getEndTime() {
        throw new AssertionError();
    }

    @Accessor("endTime")
    static void setEndTime(int endTime) {
        throw new AssertionError();
    }
}
