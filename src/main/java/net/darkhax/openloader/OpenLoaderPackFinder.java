package net.darkhax.openloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import com.google.common.base.Supplier;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;

public final class OpenLoaderPackFinder implements IPackFinder {
    
    public static final OpenLoaderPackFinder DATA = new OpenLoaderPackFinder(new File("openloader/data"));
    public static final OpenLoaderPackFinder RESOUCE = new OpenLoaderPackFinder(new File("openloader/resources"));
    
    private final File loaderDirectory;
    
    private OpenLoaderPackFinder(File loaderDirectory) {
        
        this.loaderDirectory = loaderDirectory;
        
        try {
            
            Files.createDirectories(loaderDirectory.toPath());
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.error("Failed to initialize loader.", e);
        }
    }
    
    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap (Map<String, T> packs, IFactory<T> factory) {
        
        for (final File packCandidate : this.loaderDirectory.listFiles()) {
            
            final boolean isFilePack = packCandidate.isFile() && packCandidate.getName().endsWith(".zip");
            final boolean isFolderPack = !isFilePack && packCandidate.isDirectory() && new File(packCandidate, "pack.mcmeta").isFile();
            
            if (isFilePack || isFolderPack) {
                
                final String packName = "openloader/" + packCandidate.getName();
                
                if (packs.containsKey(packName)) {
                    
                    OpenLoader.LOGGER.error("Failed to load pack {}. It's already been loaded?", packName);
                    continue;
                }
                
                final T packInfo = ResourcePackInfo.createResourcePack(packName, true, this.getAsPack(packCandidate), factory, ResourcePackInfo.Priority.TOP);
                
                if (packInfo != null) {
                    
                    packs.put(packName, packInfo);
                }
            }
            
            else {
                
                OpenLoader.LOGGER.error("Failed to load pack from {}. Archive packs must be zips. Folder packs must have a valid pack.mcmeta file.", packCandidate.getAbsolutePath());
            }
        }
    }
    
    private Supplier<IResourcePack> getAsPack (File file) {
        
        return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
    }
}