package net.darkhax.openloader;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Functions;

import net.darkhax.openloader.OpenLoaderPackFinder.PackMeta;
import net.darkhax.openloader.OpenLoaderPackFinder.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod("openloader")
public final class OpenLoader {
    
    public static final Logger LOGGER = LogManager.getLogger("Open Loader");
    public static final Configuration CONFIG = new Configuration();
    public static final DataCache CACHE = DataCache.load();
    
    public OpenLoader() {
        
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of( () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG.getSpec());
        CONFIG.forceLoad(FMLPaths.CONFIGDIR.get().resolve("openloader-common.toml"));
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            
            Minecraft.getInstance().getResourcePackList().addPackFinder(OpenLoaderPackFinder.RESOUCE);
        }
    }
    
    private void onServerStart (FMLServerAboutToStartEvent event) {
        
        event.getServer().getResourcePacks().addPackFinder(OpenLoaderPackFinder.DATA);
    }
    
    public static void injectDatapackFinder (ResourcePackList resourcePacks) {
        
        if (DistExecutor.unsafeRunForDist( () -> () -> resourcePacks != Minecraft.getInstance().getResourcePackList(), () -> () -> true)) {
            
            resourcePacks.addPackFinder(OpenLoaderPackFinder.DATA);
            OpenLoader.LOGGER.info("Injecting data pack finder.");
        }
    }
    
    public static void attemptForceLoad (ResourcePackInfo packInfo, List<ResourcePackInfo> toEnable) {
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            
            final Type packType = OpenLoaderPackFinder.Type.getPackType(packInfo);
            
            if (packType == Type.RESOURCES && !OpenLoader.CACHE.isCached(packType, packInfo.getName())) {
                
                final PackMeta meta = OpenLoaderPackFinder.getPackMeta(packInfo.getName(), packInfo::getResourcePack);
                
                if (meta != null && meta.defaultEnabled && !toEnable.contains(packInfo)) {
                    
                    packInfo.getPriority().insert(toEnable, packInfo, Functions.identity(), false);
                    OpenLoader.LOGGER.debug("Force enabling " + packInfo.getName());
                    OpenLoader.CACHE.cache(packType, packInfo.getName());
                }
            }
        }
    }
}