package net.darkhax.openloader.common.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ServiceLoader;

public interface Platform {

    Platform PLATFORM = load(Platform.class);

    Path getGamePath();

    default File getGameDirectory() {
        return this.getGamePath().toFile();
    }

    Path getConfigPath();

    default File getConfigDirectory() {
        return this.getConfigPath().toFile();
    }

    static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        OpenLoader.LOG.debug("Loaded {} for service {}.", loadedService, clazz);
        return loadedService;
    }
}
