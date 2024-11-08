package net.darkhax.openloader.forge.impl;

import net.darkhax.openloader.common.impl.OpenLoader;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OpenLoader.MOD_ID)
public class ForgeMod {

    public ForgeMod(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::addPackRepoSource);
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