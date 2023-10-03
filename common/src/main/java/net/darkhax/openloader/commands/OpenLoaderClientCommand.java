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
        return 0;
    }

    @Override
    public String id() {
        return Constants.MOD_ID;
    }

    @Override
    public List<ClientCommand> commands() {
        return List.of(new RepoCommand(configDir, config));
    }

    private record RepoCommand(Path configDir, ConfigSchema config) implements ClientCommand {

        @Override
        public int execute() {
            Util.getPlatform().openFile(configDir.toFile());
            return 1;
        }

        @Override
        public String id() {
            return "folder";
        }

        @Override
        public List<ClientCommand> commands() {
            return List.of(
                    new SubRepoCommand(configDir, RepoType.DATA, config.dataPacks),
                    new SubRepoCommand(configDir, RepoType.RESOURCES, config.resourcePacks)
            );
        }
    }

    private record SubRepoCommand(Path configDir, RepoType type, ConfigSchema.PackConfig config) implements ClientCommand {

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
