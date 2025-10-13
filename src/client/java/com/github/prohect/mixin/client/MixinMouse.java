package com.github.prohect.mixin.client;

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
            @SuppressWarnings("DataFlowIssue") var that = (Mouse) (Object) this;
            Double sensitivity = that.client.options.getMouseSensitivity().getValue();
            double d = sensitivity * 0.6D + 0.2D;
            double e = d * d * d;
            double f = e * 8.0D * sensitivity;
            double i;
            double j;
            if (that.client.options.smoothCameraEnabled) {
                double g = that.cursorXSmoother.smooth(that.cursorDeltaX * f, timeDelta * f);
                double h = that.cursorYSmoother.smooth(that.cursorDeltaY * f, timeDelta * f);
                i = g;
                j = h;
            } else if (that.client.options.getPerspective().isFirstPerson() && that.client.player.isUsingSpyglass()) {
                that.cursorXSmoother.clear();
                that.cursorYSmoother.clear();
                i = that.cursorDeltaX * e;
                j = that.cursorDeltaY * e;
            } else {
                that.cursorXSmoother.clear();
                that.cursorYSmoother.clear();
                i = that.cursorDeltaX * f;
                j = that.cursorDeltaY * f;
            }

            that.client.getTutorialManager().onUpdateMouse(i, j);
            if (that.client.player != null) {
                that.client.player.changeLookDirection(i, j);
            }

        } finally {
            ci.cancel();
        }
    }
}
