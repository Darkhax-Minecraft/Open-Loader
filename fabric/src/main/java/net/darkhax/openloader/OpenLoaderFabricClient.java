package net.darkhax.openloader;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.darkhax.openloader.commands.ClientCommand;
import net.darkhax.openloader.commands.OpenLoaderClientCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class OpenLoaderFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) ->
            dispatcher.register(createCommand(new OpenLoaderClientCommand(OpenLoaderFabric.configDir, OpenLoaderFabric.config)))
        );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> createCommand(ClientCommand command) {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = ClientCommandManager.literal(command.id());
        if (!command.isHidden()) {
            builder = builder.executes(context -> command.execute());
        }
        for (ClientCommand subCommand : command.commands()) {
            if (subCommand.isHidden()) continue;
            builder = builder.then(createCommand(subCommand));
        }
        return builder;
    }
}
