package net.darkhax.openloader.commands;

import java.util.List;

public interface ClientCommand {

    int execute();

    String id();

    default boolean isHidden() {
        return false;
    }

    default List<ClientCommand> commands() {
        return List.of();
    }
}
