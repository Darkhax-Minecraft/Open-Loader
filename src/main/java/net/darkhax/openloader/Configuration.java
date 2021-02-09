package net.darkhax.openloader;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class Configuration {
    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue loadDataPacks;
    private final BooleanValue loadResourcePacks;
    
    public Configuration() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Advanced Settings: Only edit these if you know what you're doing!");
        builder.comment("This file is NOT used to add new data/resource packs. Just put them in the folder!");
        builder.push("advanced-settings");
        
        builder.comment("Should OpenLoader try to load resource packs?");
        this.loadResourcePacks = builder.define("load-resource-packs", true);
        
        builder.comment("Should OpenLoader try to load data packs?");
        this.loadDataPacks = builder.define("load-data-packs", true);
        builder.pop();
        
        this.spec = builder.build();
    }
    
    public ForgeConfigSpec getSpec () {
        
        return this.spec;
    }
    
    public boolean allowDataPacks () {
        
        return this.loadDataPacks.get();
    }
    
    public boolean allowResourcePacks () {
        
        return this.loadResourcePacks.get();
    }
    
    public void forceLoad (Path path) {
        
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        this.spec.setConfig(configData);
    }
}