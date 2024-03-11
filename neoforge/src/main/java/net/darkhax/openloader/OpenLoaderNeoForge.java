package net.darkhax.openloader;

import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;

@Mod(Constants.MOD_ID)
public class OpenLoaderNeoForge {

    public static ConfigSchema config;
    public static Path configDir;

    public OpenLoaderNeoForge(IEventBus modBus) {

        configDir = FMLPaths.CONFIGDIR.get().resolve("openloader");
        config = ConfigSchema.load(configDir);
        modBus.addListener(this::injectPackRepositories);
    }

    private void injectPackRepositories(AddPackFindersEvent event) {

        switch (event.getPackType()) {

            case CLIENT_RESOURCES -> {

                event.addRepositorySource(new OpenLoaderRepositorySource(RepoType.RESOURCES, config.resourcePacks, configDir, config));
            }

            case SERVER_DATA -> {

                event.addRepositorySource(new OpenLoaderRepositorySource(RepoType.DATA, config.dataPacks,configDir, config));
            }

            default -> Constants.LOG.warn("Encountered unknown pack type {}. Nothing will be loaded for this type.", event.getPackType().name());
        }
    }
}