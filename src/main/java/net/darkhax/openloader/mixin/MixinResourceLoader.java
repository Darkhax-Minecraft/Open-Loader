package net.darkhax.openloader.mixin;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.darkhax.openloader.OpenLoader;
import net.darkhax.openloader.OpenLoaderPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.packs.ResourcePackLoader.IPackInfoFinder;

@Mixin(ResourcePackLoader.class)
public class MixinResourceLoader {

	@Inject(method = "loadResourcePacks(Lnet/minecraft/resources/ResourcePackList;Ljava/util/function/BiFunction;)V", at = @At("HEAD"), remap = false)
    private static <T extends ResourcePackInfo> void injectPacks(ResourcePackList resourcePacks, BiFunction<Map<ModFile, ? extends ModFileResourcePack>, BiConsumer<? super ModFileResourcePack, T>, IPackInfoFinder> packFinder, CallbackInfo callback) {

		resourcePacks.addPackFinder(OpenLoaderPackFinder.DATA);
		OpenLoader.LOGGER.info("Injecting data pack finder.");
    }
}