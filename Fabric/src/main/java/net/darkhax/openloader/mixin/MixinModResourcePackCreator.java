package net.darkhax.openloader.mixin;

import net.darkhax.openloader.Constants;
import net.darkhax.openloader.OpenLoaderCommon;
import net.darkhax.openloader.OpenLoaderFabric;
import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.function.Consumer;

@Mixin(ModResourcePackCreator.class)
public class MixinModResourcePackCreator {

    @Final
    @Shadow
    private PackType type;

    @Unique
    private OpenLoaderRepositorySource newSource;

    @Inject(method = "<init>(Lnet/minecraft/server/packs/PackType;)V", at = @At("RETURN"))
    private void onConstruction(PackType type, CallbackInfo callback) {

        final Path configDir = FabricLoader.getInstance().getConfigDir().resolve("openloader");
        final ConfigSchema config = OpenLoaderCommon.loadConfig(configDir);

        if (config == null) {

            Constants.LOG.error("Failed to read OpenLoader config file. Your file is missing, invalid, or corrupt. Another mod may be loading packs incorrectly.");
            return;
        }

        if (type == PackType.SERVER_DATA) {

            this.newSource = new OpenLoaderRepositorySource(RepoType.DATA, config.dataPacks, configDir);
        }

        else if (type == PackType.CLIENT_RESOURCES) {

            this.newSource = new OpenLoaderRepositorySource(RepoType.RESOURCES, config.resourcePacks, configDir);
        }
    }

    @Inject(method = "loadPacks(Ljava/util/function/Consumer;Lnet/minecraft/server/packs/repository/Pack$PackConstructor;)V", at = @At("RETURN"))
    private void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor factory, CallbackInfo callback) {

        if (this.newSource != null) {

            this.newSource.loadPacks(consumer, factory);
        }
    }
}