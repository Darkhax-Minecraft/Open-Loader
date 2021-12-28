package net.darkhax.openloader;

import net.darkhax.openloader.config.ConfigSchema;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class OpenLoaderFabric implements ModInitializer {

    public static ConfigSchema config;
    public static Path configDir;

    @Override
    public void onInitialize() {

        configDir = FabricLoader.getInstance().getConfigDir().resolve("openloader");
        this.config = ConfigSchema.load(configDir);
    }
}