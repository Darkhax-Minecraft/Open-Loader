package net.darkhax.openloader.fabric.mixin;

import net.darkhax.openloader.common.impl.OpenLoader;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ModResourcePackCreator.class)
public class MixinModResourcePackCreator {

    @Shadow
    @Final
    private PackType type;

    @Inject(method = "loadPacks(Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void loadPacks(Consumer<Pack> consumer, CallbackInfo cbi) {
        if (type == PackType.SERVER_DATA) {
            OpenLoader.DATA_SOURCE.get().loadPacks(consumer);
        }
    }
}