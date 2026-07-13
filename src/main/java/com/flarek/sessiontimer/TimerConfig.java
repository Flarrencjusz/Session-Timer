package com.flarek.sessiontimer;

public final class TimerConfig {
    public float x = 0.02F;
    public float y = 0.04F;
    public float scale = 1.0F;
    public int backgroundOpacity = 128;
    public int textRed = 255;
    public int textGreen = 255;
    public int textBlue = 255;

    public void reset() {
        x = 0.02F;
        y = 0.04F;
        scale = 1.0F;
        backgroundOpacity = 128;
        textRed = 255;
        textGreen = 255;
        textBlue = 255;
    }

    public void sanitize() {
        x = clamp(x, 0.0F, 1.0F);
        y = clamp(y, 0.0F, 1.0F);
        scale = clamp(scale, 0.5F, 3.0F);
        backgroundOpacity = clamp(backgroundOpacity, 0, 255);
        textRed = clamp(textRed, 0, 255);
        textGreen = clamp(textGreen, 0, 255);
        textBlue = clamp(textBlue, 0, 255);
    }

    public int textColor() {
        return 0xFF000000 | textRed << 16 | textGreen << 8 | textBlue;
    }

    public int backgroundColor() {
        return backgroundOpacity << 24;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
