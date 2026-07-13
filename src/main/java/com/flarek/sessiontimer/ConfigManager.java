package com.flarek.sessiontimer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("sessiontimer");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("sessiontimer.json");

    private ConfigManager() {
    }

    public static TimerConfig load() {
        if (Files.isRegularFile(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                TimerConfig config = GSON.fromJson(reader, TimerConfig.class);
                if (config != null) {
                    config.sanitize();
                    return config;
                }
            } catch (IOException | RuntimeException exception) {
                LOGGER.warn("Could not read {}; defaults will be used", PATH, exception);
            }
        }
        return new TimerConfig();
    }

    public static void save(TimerConfig config) {
        config.sanitize();
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            LOGGER.error("Could not save {}", PATH, exception);
        }
    }
}
