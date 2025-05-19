package net.darkhax.openloader.fabric.mixin;

import net.darkhax.openloader.common.impl.OpenLoader;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
public class MixinBuiltInPackSourceClient {

    @Shadow
    @Final
    private PackType packType;

    @Inject(method = "loadPacks", at = @At("RETURN"))
    private void loadPacks(Consumer<Pack> consumer, CallbackInfo cbi) {
        final BuiltInPackSource self = (BuiltInPackSource) (Object) this;
        if (packType == PackType.CLIENT_RESOURCES && self instanceof ClientPackSource) {
            OpenLoader.RESOURCE_SOURCE.get().loadPacks(consumer);
        }
    }
}