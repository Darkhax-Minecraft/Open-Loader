package net.darkhax.openloader.common.impl.packs;

import net.darkhax.openloader.common.impl.OpenLoader;
import net.darkhax.openloader.common.impl.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class OpenLoaderRepositorySource implements RepositorySource {

    private static final PackSource SOURCE = PackSource.create(packText -> packText, true);
    private static final Comparator<File> FILE_NAME_ORDER = Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(File::getName);
    private final PackType type;
    private final List<File> scanLocations = new LinkedList<>();

    public OpenLoaderRepositorySource(PackType type) {
        this.type = type;
        if (OpenLoader.CONFIG.get().canLoad(type)) {
            final File packFolder = new File(Platform.PLATFORM.getConfigDirectory(), "openloader/packs");
            if (packFolder.mkdirs()) {
                OpenLoader.LOG.info("Created packs folder a '{}'", packFolder.getAbsolutePath());
            }
            final File datapacksDir = new File(Platform.PLATFORM.getGameDirectory(), "datapacks");
            if (datapacksDir.exists() && OpenLoader.CONFIG.get().load_datapacks_dir) {
                this.scanLocations.add(datapacksDir);
            }
            this.scanLocations.add(packFolder);
            for (String location : OpenLoader.CONFIG.get().additional_locations) {
                if (isValidPath(location)) {
                    final File file = new File(location);
                    if (file.exists()) {
                        this.scanLocations.add(file);
                    }
                    else {
                        OpenLoader.LOG.warn("The configured path does not currently exist! '{}'", location);
                    }
                }
                else {
                    OpenLoader.LOG.error("Invalid path specified in config folder! '{}'", location);
                }
            }
        }
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        if (!this.scanLocations.isEmpty()) {
            OpenLoader.LOG.info("Scan started for {}.", type.name());
            final long startTime = System.nanoTime();
            int validPackCount = 0;
            for (File location : this.scanLocations) {
                validPackCount += loadFrom(location, consumer);
            }
            final long endTime = System.nanoTime();
            OpenLoader.LOG.info("Located {} packs. Took {}ms.", validPackCount, OpenLoader.DECIMAL_2.format((endTime - startTime) / 1000000d));
        }
    }

    private int loadFrom(File location, @NotNull Consumer<Pack> consumer) {
        final PackFileType type = PackFileType.from(location);
        if (!type.isLoadable()) {
            if (location.isDirectory() && location.exists()) {
                int validPackCount = 0;
                final File[] subLocations = location.listFiles();
                if (subLocations == null) {
                    OpenLoader.LOG.warn("Could not list packs in '{}'.", location.getAbsolutePath());
                    return 0;
                }
                Arrays.sort(subLocations, FILE_NAME_ORDER);
                for (File subLocation : subLocations) {
                    if (!OpenLoader.INVALID_FOLDERS.contains(subLocation.getName())) {
                        validPackCount += loadFrom(subLocation, consumer);
                    }
                }
                return validPackCount;
            }
            return 0;
        }
        else {
            final PackContentType contentType = PackContentType.from(location.toPath());
            if (contentType.isFor(this.type)) {
                final PackLocationInfo locationInfo = new PackLocationInfo("openloader/" + location.getAbsolutePath(), Component.literal(location.getName()), SOURCE, Optional.empty());
                final PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
                consumer.accept(Pack.readMetaAndCreate(locationInfo, type.createPackSupplier(location), this.type, selectionConfig));
                return 1;
            }
        }
        return 0;
    }

    private static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        }
        catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}
