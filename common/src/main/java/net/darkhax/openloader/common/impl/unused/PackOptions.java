package net.darkhax.openloader.common.impl.unused;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.openloader.common.impl.OpenLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public record PackOptions(boolean enabled, boolean required, Pack.Position position, Optional<Component> name, Optional<Component> description, boolean includeSourceName, boolean fixedPosition) {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final PackOptions DEFAULT_OPTIONS = new PackOptions(true, true, Pack.Position.TOP, Optional.empty(), Optional.empty(), true, false);
    public static final Codec<Pack.Position> POSITION = Codec.STRING.xmap(PackOptions::positionFromName, Enum::name);
    public static final Codec<PackOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(PackOptions::enabled),
            Codec.BOOL.optionalFieldOf("required", true).forGetter(PackOptions::required),
            POSITION.optionalFieldOf("position", Pack.Position.TOP).forGetter(PackOptions::position),
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(PackOptions::name),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(PackOptions::description),
            Codec.BOOL.optionalFieldOf("include_source_name", true).forGetter(PackOptions::includeSourceName),
            Codec.BOOL.optionalFieldOf("fixed_position", false).forGetter(PackOptions::fixedPosition)
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

    private static Pack.Position positionFromName(String name) {
        if (name.equalsIgnoreCase("top")) {
            return Pack.Position.TOP;
        }
        else if (name.equalsIgnoreCase("bottom")) {
            return Pack.Position.BOTTOM;
        }
        throw new IllegalStateException("Position must be top or bottom. Got " + name);
    }
}
