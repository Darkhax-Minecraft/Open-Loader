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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class OpenLoaderRepositorySource implements RepositorySource {

    private final RepoType type;
    private final List<File> directories;
    private final ConfigSchema.PackConfig config;
    private final boolean appendSourceToPacks;

    public OpenLoaderRepositorySource(RepoType type, ConfigSchema.PackConfig config, Path configDir, boolean appendSources) {

        this.type = type;
        this.config = config;
        this.appendSourceToPacks = appendSources;

        this.directories = new ArrayList<>();
        this.directories.add(configDir.resolve(type.getPath()).toFile());

        for (String customDir : config.additionalFolders) {

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

                    final boolean isArchivePack = isArchivePack(packCandidate);
                    final boolean isFolderPack = !isArchivePack && isFolderPack(packCandidate);
                    final String typeName = isArchivePack ? "archive" : isFolderPack ? "folder" : "invalid";

                    if (isArchivePack || isFolderPack) {

                        final String packName = this.type.getPath() + "/" + packCandidate.getName();
                        final PackOptions options = PackOptions.readOptions(packCandidate);

                        if (options.enabled) {

                            if (options.fixedPosition && !options.required) {

                                Constants.LOG.error("Pack '{}' has a fixed position but is not required. This is not allowed! The pack can not be loaded.", packName);
                                continue;
                            }

                            final PackSource source = PackSource.create(rawDesc -> {
                                Component description = options.getDescription(packName, rawDesc);
                                if (options.addSourceToDescription && appendSourceToPacks) {
                                    description = Component.translatable("pack.nameAndSource", description, Component.translatable("pack.source.openloader").withStyle(ChatFormatting.DARK_AQUA));
                                }
                                return description;
                            }, true);

                            final Pack pack = readMetaAndCreate(packName, options.getDisplayName(packName), options.required, createPackSupplier(packCandidate), this.type.getPackType(), options.getPosition(), source, options.fixedPosition);

                            if (pack != null) {

                                consumer.accept(pack);
                                newPackCount++;
                                Constants.LOG.info("Loaded {} {} from {}.", typeName, this.type.getName(), packCandidate.getAbsolutePath());
                            }
                        }

                        else {
                            Constants.LOG.debug("Pack '{}' has been disabled by config file.", packName);
                        }
                    }

                    else {

                        Constants.LOG.debug("Skipping invalid {} {} from {}", typeName, this.type.getName(), packCandidate.getAbsolutePath());
                    }
                }

                Constants.LOG.info("Successfully injected {} packs from {}.", newPackCount, parentDirectory.getAbsolutePath());
            }
        }

        else {

            Constants.LOG.info("Skipping {}. Disabled by user config.", this.type.displayName);
        }
    }

    private Pack.ResourcesSupplier createPackSupplier(File packFile) {

        return packFile.isDirectory() ? new PathPackResources.PathResourcesSupplier(packFile.toPath(), false) : new FilePackResources.FileResourcesSupplier(packFile, false);
    }

    private boolean isArchivePack(File candidate) {

        return candidate.isFile() && (endsWithIgnoreCase(candidate.getName(), ".zip") || endsWithIgnoreCase(candidate.getName(), ".jar"));
    }

    private static boolean isFolderPack(File candidate) {

        if (candidate.isDirectory()) {

            if (new File(candidate, "pack.mcmeta").isFile()) {

                return true;
            }

            else {

                Constants.LOG.warn("Can not load {} as a folder pack. It is missing a pack.mcmeta file!", candidate.getAbsolutePath());
            }
        }

        return false;
    }

    private static boolean endsWithIgnoreCase(String str, String suffix) {

        final int suffixLength = suffix.length();
        return str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
    }

    @Nullable
    public static Pack readMetaAndCreate(String id, Component title, boolean required, Pack.ResourcesSupplier resourceSupplier, PackType type, Pack.Position position, PackSource source, boolean fixedPosition) {
        int $$7 = SharedConstants.getCurrentVersion().getPackVersion(type);
        Pack.Info $$8 = Pack.readPackInfo(id, resourceSupplier, $$7);
        return $$8 != null ? Pack.create(id, title, required, resourceSupplier, $$8, position, fixedPosition, source) : null;
    }
}