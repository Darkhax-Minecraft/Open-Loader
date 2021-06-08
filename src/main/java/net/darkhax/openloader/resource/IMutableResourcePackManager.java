package net.darkhax.openloader.resource;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.resource.ResourcePackProvider;

public interface IMutableResourcePackManager {
    
    @Unique
    public void opeloader$addProvider (ResourcePackProvider provider);
    
}
