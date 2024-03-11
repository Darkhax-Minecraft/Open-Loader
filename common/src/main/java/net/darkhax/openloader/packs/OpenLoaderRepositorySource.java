package net.darkhax.openloader.packs;

import net.darkhax.openloader.Constants;
import net.darkhax.openloader.config.ConfigSchema;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipFile;

public class OpenLoaderRepositorySource implements RepositorySource {

    private final RepoType type;
    private final List<File> directories;
    private final ConfigSchema modConfig;
    private final ConfigSchema.PackConfig config;

    public OpenLoaderRepositorySource(RepoType type, ConfigSchema.PackConfig repoConfig, Path configDir, ConfigSchema modConfig) {

        this.type = type;
        this.config = repoConfig;
        this.modConfig = modConfig;

        this.directories = new ArrayList<>();
        this.directories.add(configDir.resolve(type.getPath()).toFile());

        for (String customDir : repoConfig.additionalFolders) {

            this.directories.add(new File(customDir));
        }

        for (File directory : directories) {

            if (!directory.exists()) {

                Constants.LOG.info("Generating new {} folder at {}.", type.displayName, directory.getAbsolutePath());
                directory.mkdirs();
            }

            if (!directory.isDirectory()) {

                Constants.LOG.error("Improper {} folder specified. Must be a directory! See {}.", type.displayName, directory.getAbsolutePath());
                throw new IllegalStateException("Can not load " + type.displayName + " from non-directory. " + directory.getAbsolutePath());
            }
        }
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {

        if (this.config.enabled) {

            Constants.LOG.info("Preparing {} injection.", this.type.displayName);

            for (File parentDirectory : this.directories) {

                int newPackCount = 0;

                for (File packCandidate : Objects.requireNonNull(parentDirectory.listFiles())) {

                    if (loadPack(packCandidate, consumer)) {

                        newPackCount++;
                    }
                }

                Constants.LOG.info("Successfully injected {} packs from {}.", newPackCount, parentDirectory.getAbsolutePath());
            }
        }

        else {

            Constants.LOG.info("Skipping {}. Disabled by user config.", this.type.displayName);
        }

        if (this.type == RepoType.DATA && modConfig.loadResourcePackData) {

            final File resourcePackDir = new File("resourcepacks");

            if (resourcePackDir.exists() && resourcePackDir.isDirectory()) {

                int newPackCount = 0;

                for (File packCandidate : Objects.requireNonNull(resourcePackDir.listFiles())) {

                    if (isPotentialDatapack(packCandidate.toPath()) && loadPack(packCandidate, consumer)) {

                        newPackCount++;
                    }
                }

                Constants.LOG.info("Successfully injected {} packs from {}.", newPackCount, resourcePackDir.getAbsolutePath());
            }
        }
    }

    private boolean loadPack(File packCandidate, Consumer<Pack> consumer) {

        final PackFileType fileType = PackFileType.getType(packCandidate);

        if (fileType.isLoadable()) {

            final String packName = this.type.getPath() + "/" + packCandidate.getName();
            final PackOptions options = PackOptions.readOptions(packCandidate);

            if (options.enabled) {

                if (options.fixedPosition && !options.required) {

                    Constants.LOG.error("Pack '{}' has a fixed position but is not required. This is not allowed! The pack can not be loaded.", packName);
                    return false;
                }

                final PackSource source = PackSource.create(rawDesc -> {
                    Component description = options.getDescription(packName, rawDesc);
                    if (options.addSourceToDescription && this.modConfig.appendSourceToPacks) {
                        description = Component.translatable("pack.nameAndSource", description, Component.translatable("pack.source.openloader").withStyle(ChatFormatting.DARK_AQUA));
                    }
                    return description;
                }, true);

                final Pack pack = readMetaAndCreate(packName, options.getDisplayName(packName), options.required, fileType.createPackSupplier(packCandidate), this.type.getPackType(), options.getPosition(), source, options.fixedPosition);

                if (pack != null) {

                    consumer.accept(pack);
                    Constants.LOG.info("Loaded {} {} from {}.", fileType.typeName(), this.type.getName(), packCandidate.getAbsolutePath());
                    return true;
                }
            }

            else {
                Constants.LOG.debug("Pack '{}' has been disabled by config file.", packName);
            }
        }

        else {

            Constants.LOG.debug("Skipping invalid {} {} from {}", fileType.typeName(), this.type.getName(), packCandidate.getAbsolutePath());
        }

        return false;
    }

    private static boolean isPotentialDatapack(Path filePath) {

        if (Files.isRegularFile(filePath)) {

            try (FileSystem fs = FileSystems.newFileSystem(filePath)) {

                return Files.isDirectory(fs.getPath("data/")) && Files.isRegularFile(fs.getPath("pack.mcmeta"));
            }

            catch (IOException e) {
                // no-op
            }
        }

        else if (Files.isDirectory(filePath)) {

            return Files.isDirectory(filePath.resolve("data")) && Files.isRegularFile(filePath.resolve("pack.mcmeta"));
        }

        return false;
    }

    private static boolean endsWithIgnoreCase(String str, String suffix) {

        final int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }

    @Nullable
    public static Pack readMetaAndCreate(String id, Component title, boolean required, Pack.ResourcesSupplier resourceSupplier, PackType type, Pack.Position position, PackSource source, boolean fixedPosition) {
        final int manifestVersion = SharedConstants.getCurrentVersion().getPackVersion(type);
        final Pack.Info packInfo = Pack.readPackInfo(id, resourceSupplier, manifestVersion);
        return packInfo != null ? Pack.create(id, title, required, resourceSupplier, packInfo, position, fixedPosition, source) : null;
    }

    public static enum PackFileType {

        ARCHIVE(true, file -> new FilePackResources.FileResourcesSupplier(file, false)),
        FOLDER(true, file -> new PathPackResources.PathResourcesSupplier(file.toPath(), false)),
        INVALID(false, file -> null);

        private final boolean loadable;
        private final Function<File, Pack.ResourcesSupplier> packSupplier;

        PackFileType(boolean loadable, Function<File, Pack.ResourcesSupplier> packSupplier) {

            this.loadable = loadable;
            this.packSupplier = packSupplier;
        }

        public boolean isLoadable() {

            return this.loadable;
        }

        public String typeName() {

            return this.name().toLowerCase(Locale.ROOT);
        }

        private Pack.ResourcesSupplier createPackSupplier(File packFile) {

            return this.packSupplier.apply(packFile);
        }

        public static PackFileType getType(File candidate) {

            if (candidate.isFile() && (endsWithIgnoreCase(candidate.getName(), ".zip") || endsWithIgnoreCase(candidate.getName(), ".jar"))) {

                return ARCHIVE;
            }

            else if (candidate.isDirectory()) {

                if (new File(candidate, "pack.mcmeta").isFile()) {

                    return FOLDER;
                }

                else {

                    Constants.LOG.warn("Can not load {} as a folder pack. It is missing a pack.mcmeta file!", candidate.getAbsolutePath());
                }
            }

            return INVALID;
        }
    }
}