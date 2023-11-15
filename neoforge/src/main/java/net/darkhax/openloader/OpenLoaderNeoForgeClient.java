package net.darkhax.openloader;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.darkhax.openloader.commands.ClientCommand;
import net.darkhax.openloader.commands.OpenLoaderClientCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OpenLoaderNeoForgeClient {

    @SubscribeEvent
    public static void onClientCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(createCommand(new OpenLoaderClientCommand(OpenLoaderNeoForge.configDir, OpenLoaderNeoForge.config)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createCommand(ClientCommand command) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command.id());
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
