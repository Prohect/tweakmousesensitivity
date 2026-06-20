package com.github.prohect.mixin.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.client.Options.genericValueLabel;
import static net.minecraft.client.Options.percentValueLabel;

@Mixin(MouseHandler.class)
public class MixinMouse {
    @Unique
    private double lastRaw = Double.NaN;
    @Unique
    private final OptionInstance<Double> bufferedOption = new OptionInstance<>("options.sensitivity", OptionInstance.noTooltip(), (caption, value) -> {
        if (value == 0.0) {
            return genericValueLabel(caption, Component.translatable("options.sensitivity.min"));
        } else {
            return value == 1.0 ? genericValueLabel(caption, Component.translatable("options.sensitivity.max")) : percentValueLabel(caption, 2.0 * value);
        }
    }, new OptionInstance.ValueSet<>() {
        @Override
        public Function<OptionInstance<Double>, AbstractWidget> createButton(OptionInstance.TooltipSupplier<Double> tooltipSupplier, Options gameOptions, int x, int y, int width, OptionInstance.ValueUpdateListener<? super Double> changeCallback) {
            return null;
        }

        @Override
        public Optional<Double> validateValue(Double value) {
            return Optional.of(value);
        }

        @Override
        public Codec<Double> codec() {
            return null;
        }
    }, 0.5, OptionInstance.NO_ACTION);

    @Redirect(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Options;sensitivity()Lnet/minecraft/client/OptionInstance;"
            )
    )
    private OptionInstance<Double> redirectMouseSensitivity(Options options) {
        double raw = options.sensitivity().get();
        if (raw != lastRaw) {
            lastRaw = raw;
            bufferedOption.set(((Math.cbrt(2 * raw * 0.022d / 0.15d) / 2d) - 0.2d) / 0.6d);
        }
        return bufferedOption;
    }

/*    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    private void turnPlayer(double timeDelta, CallbackInfo ci) {
        try {
            @SuppressWarnings("DataFlowIssue") var that = (MouseHandler) (Object) this;
            //sensitivity is multiplied by 2 because the displayed value in game option gui is multiplied by 2
            double sensitivity = that.minecraft.options.sensitivity().get() * 2;
            //divided by 0.15 because inside that.client.player.changeLookDirection(x,y), the value is multiplied by 0.15
            //0.022 is same as counterStrike2
            double f = 0.022 * sensitivity / 0.15;
            double e = f / 8;
            double deltaRaw;
            double deltaPitch;
            double accumulatedDX = that.accumulatedDX;
            double accumulatedDY = that.accumulatedDY;
            if (that.minecraft.options.smoothCameraEnabled) {
                double g = that.smoothTurnX.smooth(accumulatedDX * f, timeDelta * f);
                double h = that.smoothTurnY.smooth(accumulatedDY * f, timeDelta * f);
                deltaRaw = g;
                deltaPitch = h;
            } else if (that.minecraft.options.getPerspective().isFirstPerson() && that.minecraft.player.isUsingSpyglass()) {
                that.smoothTurnX.clear();
                that.smoothTurnY.clear();
                deltaRaw = accumulatedDX * e;
                deltaPitch = accumulatedDY * e;
            } else {
                that.smoothTurnX.clear();
                that.smoothTurnY.clear();
                deltaRaw = accumulatedDX * f;
                deltaPitch = accumulatedDY * f;
            }

            that.minecraft.getTutorialManager().onUpdateMouse(deltaRaw, deltaPitch);
            if (that.minecraft.player != null) {
                that.minecraft.player.changeLookDirection(deltaRaw, deltaPitch);
            }
        } finally {
            ci.cancel();
        }
    }*/
}
