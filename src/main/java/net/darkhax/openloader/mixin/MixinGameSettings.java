package net.darkhax.openloader.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.darkhax.openloader.OpenLoader;
import net.minecraft.client.GameSettings;
import net.minecraftforge.fml.client.ClientModLoader;

@Mixin(GameSettings.class)
public class MixinGameSettings {
    
    @Shadow
    public List<String> resourcePacks;
    
    @Inject(method = "saveOptions()V", at = @At("RETURN"))
    public void saveOptions (CallbackInfo callback) {
        
        if (!ClientModLoader.isLoading() && !this.resourcePacks.isEmpty()) {
            
            OpenLoader.CACHE.save();
        }
    }
}