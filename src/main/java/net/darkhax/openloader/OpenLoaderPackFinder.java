package net.darkhax.openloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackInfo.Priority;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;

public final class OpenLoaderPackFinder implements IPackFinder {
    
    private static final IMetadataSectionSerializer<PackMeta> META_SERIALIZER = new PackMetaSerializer();
    public static final OpenLoaderPackFinder DATA = new OpenLoaderPackFinder(Type.DATA);
    public static final OpenLoaderPackFinder RESOUCE = new OpenLoaderPackFinder(Type.RESOURCES);
    
    private final Type type;
    private final File loaderDirectory;
    
    private OpenLoaderPackFinder(Type type) {
        
        this.type = type;
        this.loaderDirectory = new File(type.path);
        
        try {
            
            Files.createDirectories(this.loaderDirectory.toPath());
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.error("Failed to initialize loader.", e);
        }
    }
    
    @Override
    public void findPacks (Consumer<ResourcePackInfo> packs, IFactory factory) {
        
        if (!this.type.enabled.getAsBoolean()) {
            
            OpenLoader.LOGGER.info("{} loading has been disabled via the config.", this.type.displayName);
            return;
        }
        
        for (final File packCandidate : getFilesFromDir(this.loaderDirectory)) {
            
            final boolean isFilePack = packCandidate.isFile() && packCandidate.getName().endsWith(".zip");
            final boolean isFolderPack = !isFilePack && packCandidate.isDirectory() && new File(packCandidate, "pack.mcmeta").isFile();
            
            if (isFilePack || isFolderPack) {
                
                final String packName = this.type.path + "/" + packCandidate.getName();
                final Supplier<IResourcePack> pack = this.getAsPack(packCandidate);
                final PackMeta meta = this.getPackMeta(packName, pack);
                
                OpenLoader.LOGGER.info("Loading {} {}.", this.type.displayName, packName);
                OpenLoader.LOGGER.debug("Loading {} with builtin={} priority={}", packName, meta.isBuiltIn, meta.priority);
                
                final ResourcePackInfo packInfo = ResourcePackInfo.createResourcePack(packName, meta.isBuiltIn, pack, factory, meta.priority, IPackNameDecorator.PLAIN);
                
                if (packInfo != null) {
                    
                    packs.accept(packInfo);
                }
            }
            
            else {
                
                OpenLoader.LOGGER.error("Failed to load {} from {}. Archive packs must be zips. Folder packs must have a valid pack.mcmeta file.", this.type.displayName, packCandidate.getAbsolutePath());
            }
        }
    }
    
    private PackMeta getPackMeta (String packName, Supplier<IResourcePack> pack) {
        
        try (IResourcePack packData = pack.get()) {
            
            final PackMeta meta = packData.getMetadata(META_SERIALIZER);
            
            if (meta != null) {
                
                return meta;
            }
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.warn("Could not load pack meta from {}!", packName);
            OpenLoader.LOGGER.catching(e);
        }
        
        return new PackMeta(true, Priority.TOP);
    }
    
    private Supplier<IResourcePack> getAsPack (File file) {
        
        return file.isDirectory() ? () -> new FolderPack(file) : () -> new FilePack(file);
    }
    
    private static File[] getFilesFromDir (File file) {
        
        File[] files = new File[0];
        
        if (file == null) {
            
            OpenLoader.LOGGER.error("Attempted to read from a null file.");
        }
        
        else if (!file.isDirectory()) {
            
            OpenLoader.LOGGER.error("Can not read from {}. It's not a directory.", file.getAbsolutePath());
        }
        
        else {
            
            try {
                
                final File[] readFiles = file.listFiles();
                
                if (readFiles == null) {
                    
                    OpenLoader.LOGGER.error("Could not read from {} due to a system error. This is likely an issue with your computer.", file.getAbsolutePath());
                }
                
                else {
                    
                    files = readFiles;
                }
            }
            
            catch (final SecurityException e) {
                
                OpenLoader.LOGGER.error("Could not read from {}. Blocked by system level security. This is likely an issue with your computer.", file.getAbsolutePath(), e);
            }
        }
        
        return files;
    }
    
    static enum Type {
        
        DATA("Data Pack", "openloader/data", OpenLoader.CONFIG::allowDataPacks),
        RESOURCES("Resource Pack", "openloader/resources", OpenLoader.CONFIG::allowResourcePacks);
        
        final String displayName;
        final String path;
        final BooleanSupplier enabled;
        
        Type(String name, String path, BooleanSupplier enabled) {
            
            this.displayName = name;
            this.path = path;
            this.enabled = enabled;
        }
    }
    
    static class PackMetaSerializer implements IMetadataSectionSerializer<PackMeta> {
        
        @Override
        public String getSectionName () {
            
            return "openloader";
        }
        
        @Override
        public PackMeta deserialize (JsonObject json) {
            
            final boolean builtin = JSONUtils.getBoolean(json, "builtin", true);
            final String priorityName = JSONUtils.getString(json, "priority", "top").toLowerCase();
            final Priority priority = "top".equalsIgnoreCase(priorityName) ? Priority.TOP : "bottom".equalsIgnoreCase(priorityName) ? Priority.BOTTOM : null;
            
            if (priority == null) {
                
                throw new JsonParseException("Expected priority to be \"top\" or \"bottom\". " + priorityName + " is not a valid value!");
            }
            
            return new PackMeta(builtin, priority);
        }
    }
    
    static class PackMeta {
        
        public final boolean isBuiltIn;
        public final Priority priority;
        
        public PackMeta(boolean isBuiltIn, Priority priority) {
            
            this.isBuiltIn = isBuiltIn;
            this.priority = priority;
        }
    }
}