# Tweak Mouse Sensitivity

## Why?

the dpi provided by the mouse is going higher and higher, the origin mouse sensitivity procedure is not working cool when u set dpi like 20000.
this mod only do one simple thing: multiply the origin mouse sensitivity by itself when processing
like this:
origin:     Double value = that.client.options.getMouseSensitivity().getValue();
            double d = value * 0.6F + 0.2F;
            double e = d * d * d;
            double f = e * 8.0;
            i = that.cursorDeltaX * f;
            j = that.cursorDeltaY * f;
            ......

now:        Double value = that.client.options.getMouseSensitivity().getValue();
            double d = value * 0.6F + 0.2F;
            double e = d * d * d * value;//times e by mouseSensitivity
            double f = e * 8.0;
            i = that.cursorDeltaX * f;
            j = that.cursorDeltaY * f;
            ......
            
i,j would be finnaly add to your look direction.
f is there for non spyglass view to use, e for spyglass
            

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
