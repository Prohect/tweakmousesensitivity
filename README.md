# Tweak Mouse Sensitivity

## Why?

As the DPI provided by mice continues to increase, the original mouse sensitivity settings become less effective at higher DPIs, such as 20000. This modification addresses this issue by simply multiplying the original mouse sensitivity by itself during processing.

### Original Code:
```java
Double value = that.client.options.getMouseSensitivity().getValue();
double d = value * 0.6F + 0.2F;
double e = d * d * d;
double f = e * 8.0;
i = that.cursorDeltaX * f;
j = that.cursorDeltaY * f;
```

### Modified Code:
```java
Double value = that.client.options.getMouseSensitivity().getValue();
double d = value * 0.6F + 0.2F;
double e = d * d * d * value; // Multiply e by mouseSensitivity
double f = e * 8.0;
i = that.cursorDeltaX * f;
j = that.cursorDeltaY * f;
```

The variables `i` and `j` are ultimately added to your look direction. `f` is used for non-spyglass views, while `e` is used for spyglass views.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it into your own projects.
