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
import net.minecraft.util.Identifier;

public final class SessionTimerModernClient implements ClientModInitializer {
    public static final SessionClock CLOCK = new SessionClock();
    public static TimerConfig config;

    @Override
    public void onInitializeClient() {
        config = ConfigManager.load();

        KeyBinding openEditor = KeyBindingHelper.registerKeyBinding(KeyBindingCompat.createOpenEditorKey());

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

}
