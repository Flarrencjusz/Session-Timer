package com.flarek.sessiontimer.modern;

import com.flarek.sessiontimer.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class ModernTimerConfigScreen extends Screen {
    private final Screen parent;
    private boolean dragging;
    private double dragOffsetX;
    private double dragOffsetY;
    private boolean leftWasDown;

    public ModernTimerConfigScreen(Screen parent) {
        super(Text.translatable("screen.sessiontimer.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int panelX = Math.max(8, width - 166);
        int sliderWidth = 150;
        int y = 40;

        addDrawableChild(new ConfigSlider(panelX, y, sliderWidth, "Scale", 0.5, 3.0,
                () -> SessionTimerModernClient.config.scale,
                value -> SessionTimerModernClient.config.scale = (float) value));
        y += 24;
        addDrawableChild(new ConfigSlider(panelX, y, sliderWidth, "Background", 0, 255,
                () -> SessionTimerModernClient.config.backgroundOpacity,
                value -> SessionTimerModernClient.config.backgroundOpacity = (int) Math.round(value)));
        y += 24;
        addDrawableChild(new ConfigSlider(panelX, y, sliderWidth, "Text red", 0, 255,
                () -> SessionTimerModernClient.config.textRed,
                value -> SessionTimerModernClient.config.textRed = (int) Math.round(value)));
        y += 24;
        addDrawableChild(new ConfigSlider(panelX, y, sliderWidth, "Text green", 0, 255,
                () -> SessionTimerModernClient.config.textGreen,
                value -> SessionTimerModernClient.config.textGreen = (int) Math.round(value)));
        y += 24;
        addDrawableChild(new ConfigSlider(panelX, y, sliderWidth, "Text blue", 0, 255,
                () -> SessionTimerModernClient.config.textBlue,
                value -> SessionTimerModernClient.config.textBlue = (int) Math.round(value)));

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.sessiontimer.reset"), button -> {
            SessionTimerModernClient.config.reset();
            clearAndInit();
        }).dimensions(panelX, height - 52, 72, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.sessiontimer.done"), button -> close())
                .dimensions(panelX + 78, height - 52, 72, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateDragging(mouseX, mouseY);
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.sessiontimer.drag"),
                width / 2, 24, 0xFFB8B8B8);

        ModernTimerHud.render(context, MinecraftClient.getInstance(), SessionTimerModernClient.config,
                ModernTimerHud.PREVIEW_TEXT);
        ModernTimerHud.Bounds bounds = ModernTimerHud.bounds(MinecraftClient.getInstance(),
                SessionTimerModernClient.config, ModernTimerHud.PREVIEW_TEXT);
        int outline = dragging || bounds.contains(mouseX, mouseY) ? 0xFFFFFFFF : 0xFF808080;
        drawBorder(context, bounds.x() - 1, bounds.y() - 1, bounds.width() + 2, bounds.height() + 2, outline);

        int panelX = Math.max(4, width - 170);
        context.fill(panelX, 30, width - 4, height - 24, 0xA0000000);
        super.render(context, mouseX, mouseY, delta);
    }

    private void updateDragging(double mouseX, double mouseY) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        boolean leftDown = GLFW.glfwGetMouseButton(minecraft.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT)
                == GLFW.GLFW_PRESS;
        if (leftDown && !leftWasDown) {
            ModernTimerHud.Bounds bounds = ModernTimerHud.bounds(MinecraftClient.getInstance(),
                    SessionTimerModernClient.config, ModernTimerHud.PREVIEW_TEXT);
            if (bounds.contains(mouseX, mouseY)) {
                dragging = true;
                dragOffsetX = mouseX - bounds.x();
                dragOffsetY = mouseY - bounds.y();
            }
        }
        if (leftDown && dragging) {
            ModernTimerHud.Bounds bounds = ModernTimerHud.bounds(MinecraftClient.getInstance(),
                    SessionTimerModernClient.config, ModernTimerHud.PREVIEW_TEXT);
            double availableWidth = Math.max(1, width - bounds.width());
            double availableHeight = Math.max(1, height - bounds.height());
            SessionTimerModernClient.config.x = (float) ((mouseX - dragOffsetX) / availableWidth);
            SessionTimerModernClient.config.y = (float) ((mouseY - dragOffsetY) / availableHeight);
            SessionTimerModernClient.config.sanitize();
        }
        if (!leftDown && leftWasDown && dragging) {
            dragging = false;
            ConfigManager.save(SessionTimerModernClient.config);
        }
        leftWasDown = leftDown;
    }

    private static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    @Override
    public void close() {
        ConfigManager.save(SessionTimerModernClient.config);
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private interface DoubleGetter {
        double get();
    }

    private interface DoubleSetter {
        void set(double value);
    }

    private static final class ConfigSlider extends SliderWidget {
        private final String label;
        private final double min;
        private final double max;
        private final DoubleSetter setter;

        private ConfigSlider(int x, int y, int width, String label, double min, double max,
                             DoubleGetter getter, DoubleSetter setter) {
            super(x, y, width, 20, Text.empty(), (getter.get() - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private double actualValue() {
            return min + value * (max - min);
        }

        @Override
        protected void updateMessage() {
            double current = actualValue();
            String shown = max <= 3.0 ? String.format(java.util.Locale.ROOT, "%.2fx", current)
                    : Integer.toString((int) Math.round(current));
            setMessage(Text.literal(label + ": " + shown));
        }

        @Override
        protected void applyValue() {
            setter.set(actualValue());
            SessionTimerModernClient.config.sanitize();
            ConfigManager.save(SessionTimerModernClient.config);
        }
    }
}
