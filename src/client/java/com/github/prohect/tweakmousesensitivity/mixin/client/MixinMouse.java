package com.github.prohect.tweakmousesensitivity.mixin.client;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(at = @At("HEAD"), method = "updateMouse", cancellable = true)
    private void updateMouse(double timeDelta, CallbackInfo ci) {
        try {
            var that = (Mouse) (Object) this;
            if (that.isCursorLocked() && that.client.isWindowFocused()) {
                Double value = that.client.options.getMouseSensitivity().getValue();
                double f = value * 0.6F + 0.2F;
                double g = f * f * f * value;
                double h = g * 8.0;
                double k;
                double l;
                if (that.client.options.smoothCameraEnabled) {
                    double i = that.cursorXSmoother.smooth(that.cursorDeltaX * h, timeDelta * h);
                    double j = that.cursorYSmoother.smooth(that.cursorDeltaY * h, timeDelta * h);
                    k = i;
                    l = j;
                } else if (that.client.options.getPerspective().isFirstPerson() && that.client.player.isUsingSpyglass()) {
                    that.cursorXSmoother.clear();
                    that.cursorYSmoother.clear();
                    k = that.cursorDeltaX * g;
                    l = that.cursorDeltaY * g;
                } else {
                    that.cursorXSmoother.clear();
                    that.cursorYSmoother.clear();
                    k = that.cursorDeltaX * h;
                    l = that.cursorDeltaY * h;
                }

                that.cursorDeltaX = 0.0;
                that.cursorDeltaY = 0.0;
                int m = 1;
                if (that.client.options.getInvertYMouse().getValue()) {
                    m = -1;
                }

                that.client.getTutorialManager().onUpdateMouse(k, l);
                if (that.client.player != null) {
                    that.client.player.changeLookDirection(k, l * (double) m);
                }
            } else {
                that.cursorDeltaX = 0.0;
                that.cursorDeltaY = 0.0;
            }
        } finally {
            ci.cancel();
        }
    }
}
