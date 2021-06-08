package net.darkhax.openloader.mixin;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.ImmutableSet;

import net.darkhax.openloader.resource.IMutableResourcePackManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;

@Mixin(ResourcePackManager.class)
public class MixinResourcePackManager implements IMutableResourcePackManager {
    
    @Shadow(prefix = "openloadershadow$")
    private Set<ResourcePackProvider> providers;
    
    @Override
    public void opeloader$addProvider (ResourcePackProvider provider) {
        
        final Set<ResourcePackProvider> mutableProviders = new HashSet<>(this.providers);
        mutableProviders.add(provider);
        this.providers = ImmutableSet.copyOf(mutableProviders);
    }
}