package com.github.prohect.mixin.client;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouse {

    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    private void turnPlayer(double timeDelta, CallbackInfo ci) {
        try {
            var that = (MouseHandler) (Object) this;
            if (that.isMouseGrabbed() && that.minecraft.isWindowActive()) {
                double value = that.minecraft.options.sensitivity().get();
                double f = value * 0.6F + 0.2F;
                double g = f * f * f * value;
                double h = g * 8.0;
                double k;
                double l;
                if (that.minecraft.options.smoothCamera) {
                    double i = that.smoothTurnX.getNewDeltaValue(that.accumulatedDX * h, timeDelta * h);
                    double j = that.smoothTurnY.getNewDeltaValue(that.accumulatedDY * h, timeDelta * h);
                    k = i;
                    l = j;
                } else if (that.minecraft.options.getCameraType().isFirstPerson() && that.minecraft.player.isScoping()) {
                    that.smoothTurnX.reset();
                    that.smoothTurnY.reset();
                    k = that.accumulatedDX * g;
                    l = that.accumulatedDY * g;
                } else {
                    that.smoothTurnX.reset();
                    that.smoothTurnY.reset();
                    k = that.accumulatedDX * h;
                    l = that.accumulatedDY * h;
                }

                that.accumulatedDX = 0.0;
                that.accumulatedDY = 0.0;
                int m = 1;
                if (that.minecraft.options.invertMouseY().get()) {
                    m = -1;
                }

                that.minecraft.getTutorial().onMouse(k, l);
                if (that.minecraft.player != null) {
                    that.minecraft.player.turn(k, l * (double) m);
                }
            } else {
                that.accumulatedDX = 0.0;
                that.accumulatedDY = 0.0;
            }
        } finally {
            ci.cancel();
        }
    }
}
