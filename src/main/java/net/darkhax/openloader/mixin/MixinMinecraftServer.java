package net.darkhax.openloader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.darkhax.openloader.resource.IMutableResourcePackManager;
import net.darkhax.openloader.resource.OpenLoaderPackProvider;
import net.darkhax.openloader.resource.OpenLoaderPackProvider.Type;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;

@Mixin(value = MinecraftServer.class, priority = 1001)
public class MixinMinecraftServer {
    
    @Inject(method = "loadDataPacks", at = @At("HEAD"))
    private static void loadDataPacks (ResourcePackManager manager, DataPackSettings settings, boolean safe, CallbackInfoReturnable<DataPackSettings> info) {
        
        if (!safe) {
            
            Type.DATA.getLogger().info("Injecting datapack provider for server.");
            final IMutableResourcePackManager mutable = (IMutableResourcePackManager) manager;
            mutable.opeloader$addProvider(new OpenLoaderPackProvider(Type.DATA));
        }
    }
}