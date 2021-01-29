package net.darkhax.openloader;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod("openloader")
public final class OpenLoader {
    
    public static final Logger LOGGER = LogManager.getLogger("Open Loader");
    
    public OpenLoader() {
        
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            
            Minecraft.getInstance().getResourcePackList().addPackFinder(OpenLoaderPackFinder.RESOUCE);
        }
    }
    
    private void onServerStart (FMLServerAboutToStartEvent event) {
        
        event.getServer().getResourcePacks().addPackFinder(OpenLoaderPackFinder.DATA);
    }
}