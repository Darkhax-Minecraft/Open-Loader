package net.darkhax.openloader;

import net.darkhax.openloader.config.ConfigSchema;

import java.nio.file.Path;

public class OpenLoaderCommon {

    private static ConfigSchema config;
    private static Path configDir;

    public static ConfigSchema loadConfig(Path configDir) {

        if (config == null) {

            config = ConfigSchema.load(configDir);
        }

        return config;
    }
}