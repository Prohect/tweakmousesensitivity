package com.github.prohect.mixin.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.client.option.GameOptions.getGenericValueText;
import static net.minecraft.client.option.GameOptions.getPercentValueText;

@Mixin(Mouse.class)
public class MixinMouse {
    @Unique
    private double lastRaw = Double.NaN;
    @Unique
    private final SimpleOption<Double> bufferedOption = new SimpleOption<>("options.sensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0.0) {
            return getGenericValueText(optionText, Text.translatable("options.sensitivity.min"));
        } else {
            return value == 1.0 ? getGenericValueText(optionText, Text.translatable("options.sensitivity.max")) : getPercentValueText(optionText, 2.0 * value);
        }
    }, new SimpleOption.Callbacks<>() {
        @Override
        public Function<SimpleOption<Double>, ClickableWidget> getWidgetCreator(SimpleOption.TooltipFactory<Double> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<Double> changeCallback) {
            return null;
        }

        @Override
        public Optional<Double> validate(Double value) {
            return Optional.of(value);
        }

        @Override
        public Codec<Double> codec() {
            return null;
        }
    }, 0.5, value -> {
    });

    @Redirect(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getMouseSensitivity()Lnet/minecraft/client/option/SimpleOption;"
            )
    )
    private SimpleOption<Double> redirectMouseSensitivity(GameOptions options) {
        double raw = options.getMouseSensitivity().getValue();
        if (raw != lastRaw) {
            lastRaw = raw;
            bufferedOption.setValue(((Math.cbrt(2 * raw * 0.022d / 0.15d) / 2d) - 0.2d) / 0.6d);
        }
        return bufferedOption;
    }

/*    @Inject(at = @At("HEAD"), method = "updateMouse", cancellable = true)
    private void updateMouse(double timeDelta, CallbackInfo ci) {
        try {
            @SuppressWarnings("DataFlowIssue") var that = (Mouse) (Object) this;
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

            that.client.getTutorialManager().onUpdateMouse(deltaRaw, deltaPitch);
            if (that.client.player != null) {
                that.client.player.changeLookDirection(deltaRaw, deltaPitch);
            }
        } finally {
            ci.cancel();
        }
    }*/
}
