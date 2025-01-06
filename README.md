Tweak Mouse Sensitivity
Why?
As the DPI of mice continues to increase, the original mouse sensitivity settings do not perform well at high DPIs, such as 20000. This modification does one simple thing: it multiplies the original mouse sensitivity by itself during processing.

Code
Original code:

java
Double value = that.client.options.getMouseSensitivity().getValue();
double d = value * 0.6F + 0.2F;
double e = d * d * d;
double f = e * 8.0;
i = that.cursorDeltaX * f;
j = that.cursorDeltaY * f;
Modified code:

java
Double value = that.client.options.getMouseSensitivity().getValue();
double d = value * 0.6F + 0.2F;
double e = d * d * d * value; // Multiply e by mouse sensitivity
double f = e * 8.0;
i = that.cursorDeltaX * f;
j = that.cursorDeltaY * f;
The variables i and j will be added to your look direction. Variable f is used for non-spyglass view, and variable e is used for spyglass view.

License
This template is available under the CC0 license. Feel free to learn from it and incorporate it into your own projects.
