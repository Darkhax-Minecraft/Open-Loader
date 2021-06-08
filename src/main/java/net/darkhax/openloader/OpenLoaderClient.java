package net.darkhax.openloader;

import net.darkhax.openloader.resource.IMutableResourcePackManager;
import net.darkhax.openloader.resource.OpenLoaderPackProvider;
import net.darkhax.openloader.resource.OpenLoaderPackProvider.Type;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class OpenLoaderClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient () {
        
        Type.DATA.getLogger().info("Injecting resource pack provider for client.");
        final IMutableResourcePackManager manager = (IMutableResourcePackManager) MinecraftClient.getInstance().getResourcePackManager();
        manager.opeloader$addProvider(new OpenLoaderPackProvider(Type.RESOURCES));
    }
}