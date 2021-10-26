package net.darkhax.openloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod("openloader")
public final class OpenLoader {

    public static final Logger LOGGER = LogManager.getLogger("Open Loader");
    public static final Configuration CONFIG = new Configuration();

    private final OpenLoaderPackFinder DATA = new OpenLoaderPackFinder(PackTypes.DATA);
    private final OpenLoaderPackFinder RESOUCE = new OpenLoaderPackFinder(PackTypes.RESOURCES);

    public OpenLoader () {

        // Allow clients without the OpenLoader to connect to a server with it.
        ModLoadingContext.get().registerExtensionPoint(DisplayTest.class, () -> new DisplayTest( () -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true));

        // Register the configuration file with Forge and force load it.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG.getSpec(), "openloader/advanced-settings.toml");
        CONFIG.forceLoad(FMLPaths.CONFIGDIR.get().resolve("openloader").resolve("advanced-settings.toml"));

        // Use Forge's hook to bootstrap pack sources.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addRepositorySources);
    }

    private void addRepositorySources (AddPackFindersEvent event) {

        switch (event.getPackType()) {

            case CLIENT_RESOURCES:
                event.addRepositorySource(this.RESOUCE);
                LOGGER.warn("Successfully loaded pack repository for client resources.");
                break;

            case SERVER_DATA:
                event.addRepositorySource(this.DATA);
                LOGGER.warn("Successfully loaded pack repository for server data.");
                break;

            default:
                LOGGER.warn("Encountered unknown pack type {}. No pack finders will be loaded.", event.getPackType().name());
                break;
        }
    }
}