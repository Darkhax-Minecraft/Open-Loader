package net.darkhax.openloader;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

public class OpenLoaderPackFinder implements RepositorySource {

    private final PackTypes type;
    private final File folder;

    public OpenLoaderPackFinder (PackTypes type) {

        this.type = type;
        this.folder = type.getDirectory();
    }

    @Override
    public void loadPacks (Consumer<Pack> packLoader, Pack.PackConstructor factory) {

        if (!this.type.enabled.getAsBoolean()) {

            OpenLoader.LOGGER.info("{} loading has been disabled via the config.", this.type.displayName);
            return;
        }

        if (!this.folder.isDirectory()) {

            this.folder.mkdirs();
        }

        final File[] packCandidates = this.folder.listFiles(this::filterFiles);

        if (packCandidates != null) {

            for (final File packFile : packCandidates) {

                final String packName = "oepnloader/" + packFile.getName();
                final Pack pack = Pack.create(packName, true, this.createPackSupplier(packFile), factory, Pack.Position.TOP, this.type);

                if (pack != null) {

                    packLoader.accept(pack);
                }
            }
        }
    }

    private Supplier<PackResources> createPackSupplier (File packFile) {

        return () -> packFile.isDirectory() ? new FolderPackResources(packFile) : new FilePackResources(packFile);
    }

    private boolean filterFiles (File candidate) {

        final boolean isZip = candidate.isFile() && candidate.getName().endsWith(".zip");
        final boolean isFolder = candidate.isDirectory() && new File(candidate, "pack.mcmeta").isFile();
        return isZip || isFolder;
    }
}