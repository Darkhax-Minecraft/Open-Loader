package net.darkhax.openloader;

import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(Constants.MOD_ID)
public class OpenLoaderForge {

    public static ConfigSchema config;
    public static Path configDir;

    public OpenLoaderForge() {

        configDir = FMLPaths.CONFIGDIR.get().resolve("openloader");
        config = ConfigSchema.load(configDir);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::injectPackRepositories);
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