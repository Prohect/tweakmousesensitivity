# Tweak Mouse Sensitivity

## Why?

the dpi range provided by the mouse is going higher and higher, the origin mouse sensitivity procedure is not working cool when u set dpi to value like 20000.
this mod only do one simple thing: multiply the origin mouse sensitivity by itself when processing

## branch suffix 'a'
make is works like cs series or Apex, while branch ".*_b" stays oringin idea

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
I somehow make it finnally same as cs2 which is 0.022 * cursorDeltaX|Y

The variables `i` and `j` are ultimately added to your look direction. `f` is used for non-spyglass views, while `e` is used for spyglass views.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it into your own projects.
