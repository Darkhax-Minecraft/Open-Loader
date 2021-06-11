package net.darkhax.openloader.resource;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ZipResourcePack;

public class OpenLoaderPackProvider implements ResourcePackProvider {
    
    private final Type type;
    
    public OpenLoaderPackProvider(Type type) {
        
        this.type = type;
    }
    
    @Override
    public void register (Consumer<ResourcePackProfile> consumer, Factory factory) {
        
        for (final File candidate : this.type.getDirectory().listFiles()) {
            
            final boolean isFilePack = candidate.isFile() && candidate.getName().endsWith(".zip");
            final boolean isFolderPack = !isFilePack && candidate.isDirectory() && new File(candidate, "pack.mcmeta").isFile();
            
            if (isFilePack || isFolderPack) {
                
                final String packName = this.type.path + "/" + candidate.getName();
                this.type.getLogger().info("Loading pack {} from {}.", packName, candidate.getAbsolutePath());
                
                final Supplier<ResourcePack> packSupplier = candidate.isDirectory() ? () -> new DirectoryResourcePack(candidate) : () -> new ZipResourcePack(candidate);
                final ResourcePackProfile profile = ResourcePackProfile.of(packName, true, packSupplier, factory, InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_NONE);
                
                if (profile != null) {
                    
                    consumer.accept(profile);
                    this.type.getLogger().info("Loaded pack {}.", packName);
                }
                
                else {
                    
                    this.type.getLogger().error("Failed to build pack profile {} from {}.", packName, candidate.getAbsolutePath());
                }
            }
            
            else {
                
                this.type.getLogger().error("Skipping over {}. It is not a valid folder or archive/file pack.", candidate.getAbsolutePath());
            }
        }
    }
    
    public static enum Type {
        
        DATA("Data Pack", "openloader/data"),
        RESOURCES("Resource Pack", "openloader/resources");
        
        final String displayName;
        final String path;
        final Logger logger;
        
        Type(String name, String path) {
            
            this.displayName = name;
            this.path = path;
            this.logger = LogManager.getLogger("Open Loader");
        }
        
        public Logger getLogger () {
            
            return this.logger;
        }
        
        public File getDirectory () {
            
            final File directory = FabricLoader.getInstance().getConfigDir().resolve(this.path).toFile();
            
            if (!directory.exists()) {
                
                directory.mkdirs();
            }
            
            return directory;
        }
    }
}