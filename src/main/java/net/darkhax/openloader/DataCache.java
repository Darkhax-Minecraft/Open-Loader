package net.darkhax.openloader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import net.darkhax.openloader.OpenLoaderPackFinder.Type;

public class DataCache {
    
    @Expose
    public Set<String> cachedResourcePacks;
    
    public DataCache() {
        
        this.cachedResourcePacks = new HashSet<>();
    }
    
    public void save () {
        
        init();
        
        try (FileWriter writer = new FileWriter(path)) {
            
            GSON.toJson(this, writer);
            OpenLoader.LOGGER.debug("Saved cache.");
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.error("Could not write cache file!", e);
        }
    }
    
    public void cache (Type type, String name) {
        
        if (type == Type.RESOURCES) {
            
            this.cachedResourcePacks.add(name);
        }
    }
    
    public boolean isCached (Type type, String name) {
        
        if (type == Type.RESOURCES) {
            
            return this.cachedResourcePacks.contains(name);
        }
        
        return false;
    }
    
    private static final Gson GSON = new GsonBuilder().create();
    private static final File path = new File("openloader", ".cache");
    
    public static void init () {
        
        try {
            
            if (!path.exists()) {
                
                path.getParentFile().mkdirs();
                path.createNewFile();
            }
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.error("Failed to initialize the data cache!", e);
        }
    }
    
    @Nullable
    public static DataCache load () {
        
        init();
        
        DataCache cache = null;
        
        try (FileReader reader = new FileReader(path)) {
            
            cache = GSON.fromJson(reader, DataCache.class);
            OpenLoader.LOGGER.debug("Loaded cache.");
        }
        
        catch (final IOException e) {
            
            OpenLoader.LOGGER.error("Could not read cache file!", e);
        }
        
        return cache != null ? cache : new DataCache();
    }
}