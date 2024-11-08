package net.darkhax.openloader.common.impl.packs;

import net.minecraft.server.packs.PackType;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Determines the type of contents held by a given pack. A pack may contain data, resources, both, or neither.
 *
 * @param data      If the pack contains data.
 * @param resources If the pack contains data.
 */
public record PackContentType(boolean data, boolean resources) {

    /**
     * Represents a pack that does not contain any valid pack contents.
     */
    private static final PackContentType INVALID = new PackContentType(false, false);

    /**
     * Checks if the pack contains content for a given pack type. For example if the pack contains resource pack files,
     * or data pack files.
     *
     * @param type The pack type to check for.
     * @return If the pack contains contents for the given pack type.
     */
    public boolean isFor(PackType type) {
        return (type == PackType.SERVER_DATA && this.data) || (type == PackType.CLIENT_RESOURCES && this.resources);
    }

    /**
     * Determines the content types of a given file.
     *
     * @param filePath The path to the pack file.
     * @return The content types of the pack.
     */
    public static PackContentType from(Path filePath) {
        // Archive
        if (Files.isRegularFile(filePath)) {
            try (FileSystem fs = FileSystems.newFileSystem(filePath)) {
                if (Files.isRegularFile(fs.getPath("pack.mcmeta"))) {
                    return new PackContentType(Files.isDirectory(fs.getPath("data/")), Files.isDirectory(fs.getPath("assets/")));
                }
            }
            catch (IOException e) {
                // no-op
            }
        }
        // Folder
        else if (Files.isDirectory(filePath)) {
            if (Files.isRegularFile(filePath.resolve("pack.mcmeta"))) {
                return new PackContentType(Files.isDirectory(filePath.resolve("data")), Files.isDirectory(filePath.resolve("assets")));
            }
        }
        return INVALID;
    }
}