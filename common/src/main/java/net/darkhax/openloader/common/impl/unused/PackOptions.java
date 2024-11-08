package net.darkhax.openloader.common.impl.unused;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecHelper;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.openloader.common.impl.OpenLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public record PackOptions(boolean enabled, boolean required, Pack.Position position, Optional<Component> name, Optional<Component> description, boolean includeSourceName, boolean fixedPosition) {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final PackOptions DEFAULT_OPTIONS = new PackOptions(true, true, Pack.Position.TOP, Optional.empty(), Optional.empty(), true, false);
    public static final MapCodecHelper<Pack.Position> POSITION = new MapCodecHelper<>(MapCodecs.enumerable(Pack.Position.class));
    public static final Codec<PackOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MapCodecs.BOOLEAN.get("enabled", PackOptions::enabled, true),
            MapCodecs.BOOLEAN.get("required", PackOptions::required, true),
            POSITION.get("position", PackOptions::position, Pack.Position.TOP),
            MapCodecs.TEXT.getOptional("name", PackOptions::name),
            MapCodecs.TEXT.getOptional("description", PackOptions::description),
            MapCodecs.BOOLEAN.get("include_source_name", PackOptions::includeSourceName, true),
            MapCodecs.BOOLEAN.get("fixed_position", PackOptions::fixedPosition, false)
    ).apply(instance, PackOptions::new));

    public PackSelectionConfig selectionConfig() {
        return new PackSelectionConfig(this.required, this.position, this.fixedPosition);
    }

    public static PackOptions readOptions(File packCandidate) {
        File optionsFile = new File(packCandidate.getParent(), packCandidate.getName() + ".packmeta");
        if (!optionsFile.exists()) {
            optionsFile = new File(packCandidate.getParent(), packCandidate.getName() + ".packmeta.json");
            if (!optionsFile.exists()) {
                optionsFile = new File(packCandidate.getParent(), packCandidate.getName() + ".json");
            }
        }
        if (optionsFile.exists()) {
            if (optionsFile.isFile()) {
                try (FileReader reader = new FileReader(optionsFile)) {
                    final JsonElement json = GSON.fromJson(reader, JsonElement.class);
                    return CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
                }
                catch (IOException e) {
                    OpenLoader.LOG.error("Failed to read pack options. The file is not formatted correctly! {}", optionsFile.getAbsolutePath());
                    OpenLoader.LOG.catching(e);
                }
            }
            else {
                OpenLoader.LOG.error("Pack options must be a file! {}", optionsFile.getAbsolutePath());
            }
        }
        OpenLoader.LOG.debug("Using default pack options for {}", optionsFile.getAbsolutePath());
        return DEFAULT_OPTIONS;
    }
}
