package net.darkhax.openloader.packs;

import java.io.File;
import java.nio.file.Path;

public enum RepoType {

    DATA("Data Pack", "data"),
    RESOURCES("Resource Pack", "resources");

    final String displayName;
    final String path;

    RepoType(String name, String path) {

        this.displayName = name;
        this.path = path;
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
