package com.flarek.sessiontimer.modern;

import com.flarek.sessiontimer.ConfigManager;
import com.flarek.sessiontimer.SessionClock;
import com.flarek.sessiontimer.TimerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class SessionTimerModernClient implements ClientModInitializer {
    public static final SessionClock CLOCK = new SessionClock();
    public static TimerConfig config;

    @Override
    public void onInitializeClient() {
        config = ConfigManager.load();

        KeyBinding openEditor = KeyBindingHelper.registerKeyBinding(createOpenEditorKey());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CLOCK.restart());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> CLOCK.stop());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditor.wasPressed()) {
                client.setScreen(new ModernTimerConfigScreen(client.currentScreen));
            }
        });

        HudElementRegistry.addLast(Identifier.of("sessiontimer", "timer"), (context, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (CLOCK.isRunning() && !(client.currentScreen instanceof ModernTimerConfigScreen)) {
                ModernTimerHud.render(context, client, config, SessionClock.format(CLOCK.elapsedMillis()));
            }
        });
    }

    private static KeyBinding createOpenEditorKey() {
        try {
            Class<?> categoryType;
            try {
                categoryType = Class.forName("net.minecraft.client.option.KeyBinding$Category");
            } catch (ClassNotFoundException namedEnvironment) {
                categoryType = Class.forName("net.minecraft.class_304$class_11900");
            }
            Object category = categoryType.getConstructor(Identifier.class)
                    .newInstance(Identifier.of("sessiontimer", "controls"));
            return (KeyBinding) KeyBinding.class
                    .getConstructor(String.class, InputUtil.Type.class, int.class, categoryType)
                    .newInstance("key.sessiontimer.open_editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, category);
        } catch (ClassNotFoundException noModernCategory) {
            try {
                return KeyBinding.class
                        .getConstructor(String.class, InputUtil.Type.class, int.class, String.class)
                        .newInstance("key.sessiontimer.open_editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O,
                                "key.category.sessiontimer");
            } catch (ReflectiveOperationException legacyFailure) {
                throw new IllegalStateException("Could not create the Session Timer key binding", legacyFailure);
            }
        } catch (ReflectiveOperationException modernFailure) {
            throw new IllegalStateException("Could not create the Session Timer key binding", modernFailure);
        }
    }
}
