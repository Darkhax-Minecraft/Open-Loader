package net.darkhax.openloader.mixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import net.darkhax.openloader.OpenLoader;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mixin(ResourcePackList.class)
public class MixinResourcePackList {
    
    @Shadow
    private Map<String, ResourcePackInfo> packNameToInfo;
    
    @Shadow
    private List<ResourcePackInfo> enabled;
    
    @Inject(method = "setEnabledPacks(Ljava/util/Collection;)V", at = @At("RETURN"))
    public void setEnabledPacks (Collection<String> packs, CallbackInfo callback) {
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            
            final List<ResourcePackInfo> toEnable = this.enabled.stream().collect(Collectors.toCollection(ArrayList::new));
            
            for (final ResourcePackInfo packInfo : this.packNameToInfo.values()) {
                
                OpenLoader.attemptForceLoad(packInfo, toEnable);
            }
            
            this.enabled = ImmutableList.copyOf(toEnable);
        }
    }
}