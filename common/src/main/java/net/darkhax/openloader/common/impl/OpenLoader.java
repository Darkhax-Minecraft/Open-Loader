package net.darkhax.openloader.common.impl;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.openloader.common.impl.conig.Config;
import net.darkhax.openloader.common.impl.packs.OpenLoaderRepositorySource;
import net.darkhax.pricklemc.common.api.config.ConfigManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Set;

public class OpenLoader {

    public static final String MOD_ID = "openloader";
    public static final String MOD_NAME = "Open Loader";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static final Set<String> INVALID_FOLDERS = Set.of("data", "assets");
    public static final DecimalFormat DECIMAL_2 = new DecimalFormat("#.##");
    public static final CachedSupplier<Config> CONFIG = CachedSupplier.cache(() -> ConfigManager.load(MOD_ID + "/options", new Config()));
    public static final CachedSupplier<RepositorySource> DATA_SOURCE = CachedSupplier.cache(() -> new OpenLoaderRepositorySource(PackType.SERVER_DATA));
    public static final CachedSupplier<RepositorySource> RESOURCE_SOURCE = CachedSupplier.cache(() -> new OpenLoaderRepositorySource(PackType.CLIENT_RESOURCES));
}