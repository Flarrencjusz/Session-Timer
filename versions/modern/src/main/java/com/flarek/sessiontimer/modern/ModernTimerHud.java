package com.flarek.sessiontimer.modern;

import com.flarek.sessiontimer.TimerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class ModernTimerHud {
    public static final int PADDING_X = 5;
    public static final int PADDING_Y = 3;
    public static final String PREVIEW_TEXT = "00:12:34.567";

    private ModernTimerHud() {
    }

    public static Bounds bounds(MinecraftClient client, TimerConfig config, String text) {
        TextRenderer font = client.textRenderer;
        int rawWidth = font.getWidth(text) + PADDING_X * 2;
        int rawHeight = font.fontHeight + PADDING_Y * 2;
        int width = Math.round(rawWidth * config.scale);
        int height = Math.round(rawHeight * config.scale);
        int x = Math.round(config.x * Math.max(0, client.getWindow().getScaledWidth() - width));
        int y = Math.round(config.y * Math.max(0, client.getWindow().getScaledHeight() - height));
        return new Bounds(x, y, width, height, rawWidth, rawHeight);
    }

    public static void render(DrawContext context, MinecraftClient client, TimerConfig config, String text) {
        Bounds bounds = bounds(client, config, text);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(bounds.x(), bounds.y());
        context.getMatrices().scale(config.scale, config.scale);
        context.fill(0, 0, bounds.rawWidth(), bounds.rawHeight(), config.backgroundColor());
        context.drawTextWithShadow(client.textRenderer, text, PADDING_X, PADDING_Y, config.textColor());
        context.getMatrices().popMatrix();
    }

    public record Bounds(int x, int y, int width, int height, int rawWidth, int rawHeight) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }
}
