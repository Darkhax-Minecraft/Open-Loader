package net.darkhax.openloader.packs;

import net.minecraft.server.packs.PackType;

import java.io.File;
import java.nio.file.Path;

public enum RepoType {

    DATA(PackType.SERVER_DATA, "Data Pack", "data"),
    RESOURCES(PackType.CLIENT_RESOURCES, "Resource Pack", "resources");

    final PackType type;
    final String displayName;
    final String path;

    RepoType(PackType type, String name, String path) {

        this.type = type;
        this.displayName = name;
        this.path = path;
    }

    public PackType getPackType() {

        return this.type;
    }

    public String getName() {

        return this.displayName;
    }

    public String getPath() {

        return this.path;
    }

    public void createDirectory(Path configDir) {

        final File file = configDir.resolve(this.getPath()).toFile();

        if (!file.exists()) {

            file.mkdirs();
        }
    }
}
