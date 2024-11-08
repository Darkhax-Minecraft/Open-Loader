package net.darkhax.openloader.neoforge.impl;

import net.darkhax.openloader.common.impl.OpenLoader;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(OpenLoader.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod(IEventBus modBus) {
        modBus.addListener(this::addPackRepoSource);
    }

    private void addPackRepoSource(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(OpenLoader.RESOURCE_SOURCE.get());
        }
        else if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(OpenLoader.DATA_SOURCE.get());
        }
    }
}