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
			//sensitivity is multiplied by 2 because the displayed value in game option gui is multiplied by 2
			double sensitivity = that.client.options.getMouseSensitivity().getValue() * 2;
			//divided by 0.15 because inside that.client.player.changeLookDirection(x,y), the value is multiplied by 0.15
			//0.022 is same as counterStrike2
			double f = 0.022 * sensitivity / 0.15;
			double e = f / 8;
			double deltaRaw;
			double deltaPitch;
			double cursorDeltaX = that.cursorDeltaX;
			double cursorDeltaY = that.cursorDeltaY;
			if (that.client.options.smoothCameraEnabled) {
				double g = that.cursorXSmoother.smooth(cursorDeltaX * f, timeDelta * f);
				double h = that.cursorYSmoother.smooth(cursorDeltaY * f, timeDelta * f);
				deltaRaw = g;
				deltaPitch = h;
			} else if (that.client.options.getPerspective().isFirstPerson() && that.client.player.isUsingSpyglass()) {
				that.cursorXSmoother.clear();
				that.cursorYSmoother.clear();
				deltaRaw = cursorDeltaX * e;
				deltaPitch = cursorDeltaY * e;
			} else {
				that.cursorXSmoother.clear();
				that.cursorYSmoother.clear();
				deltaRaw = cursorDeltaX * f;
				deltaPitch = cursorDeltaY * f;
			}

			int k = 1;
			if (that.client.options.getInvertYMouse().getValue()) {
				k = -1;
			}

			that.client.getTutorialManager().onUpdateMouse(deltaRaw, deltaPitch);
			if (that.client.player != null) {
				that.client.player.changeLookDirection(deltaRaw, deltaPitch * (double) k);
			}
		} finally {
			ci.cancel();
		}
	}
}
