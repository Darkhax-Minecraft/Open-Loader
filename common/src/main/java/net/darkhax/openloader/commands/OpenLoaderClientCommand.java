package net.darkhax.openloader.commands;

import net.darkhax.openloader.Constants;
import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.RepoType;
import net.minecraft.Util;

import java.nio.file.Path;
import java.util.List;

public record OpenLoaderClientCommand(Path configDir, ConfigSchema config) implements ClientCommand {

    @Override
    public int execute() {
        Util.getPlatform().openFile(configDir.toFile());
        return 1;
    }

    @Override
    public String id() {
        return Constants.MOD_ID;
    }

    @Override
    public List<ClientCommand> commands() {
        return List.of(
                new RepoCommand(configDir, RepoType.DATA, config.dataPacks),
                new RepoCommand(configDir, RepoType.RESOURCES, config.resourcePacks)
        );
    }

    private record RepoCommand(Path configDir, RepoType type, ConfigSchema.PackConfig config) implements ClientCommand {

        @Override
        public int execute() {
            Util.getPlatform().openFile(configDir.resolve(type.getPath()).toFile());
            return 1;
        }

        @Override
        public String id() {
            return type.getPath();
        }

        @Override
        public boolean isHidden() {
            return !config.enabled;
        }
    }
}
