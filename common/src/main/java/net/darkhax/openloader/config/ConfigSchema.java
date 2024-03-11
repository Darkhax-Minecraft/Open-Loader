package net.darkhax.openloader.config;

import com.google.gson.annotations.Expose;
import net.darkhax.openloader.Constants;
import net.darkhax.openloader.packs.RepoType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigSchema {

    @Expose
    public PackConfig resourcePacks = new PackConfig();

    @Expose
    public PackConfig dataPacks = new PackConfig();

    @Expose
    public boolean appendSourceToPacks = true;

    @Expose
    public boolean loadResourcePackData = true;

    public static class PackConfig {

        @Expose
        public boolean enabled = true;

        @Expose
        public String[] additionalFolders = new String[0];
    }

    public static ConfigSchema load(Path configDir) {

        RepoType.DATA.createDirectory(configDir);
        RepoType.RESOURCES.createDirectory(configDir);

        final File configFile = configDir.resolve("advanced_options.json").toFile();
        final ConfigSchema defaultConfig = new ConfigSchema();

        // Attempt to load existing config file
        if (configFile.exists()) {

            try (FileReader reader = new FileReader(configFile)) {

                return Constants.GSON.fromJson(reader, ConfigSchema.class);
            }

            catch (IOException e) {

                Constants.LOG.error("Could not read config file {}. Defaults will be used.", configFile.getAbsolutePath());
                Constants.LOG.catching(e);
            }
        }

        else {

            Constants.LOG.info("Creating a new config file at {}.", configFile.getAbsolutePath());
            configFile.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(configFile)) {

            Constants.GSON.toJson(defaultConfig, writer);
            Constants.LOG.info("Default configuration file generated at {}.", configFile.getAbsolutePath());
        }

        catch (IOException e) {

            Constants.LOG.error("Could not write config file '{}'!", configFile.getAbsolutePath());
            Constants.LOG.catching(e);
        }


        return defaultConfig;
    }
}