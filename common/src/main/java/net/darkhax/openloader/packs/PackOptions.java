package net.darkhax.openloader.packs;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.JsonOps;
import net.darkhax.openloader.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.repository.Pack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PackOptions {

    @Expose
    @SerializedName("enabled")
    public boolean enabled = true;

    @Expose
    @SerializedName("required")
    public boolean required = true;

    @Expose
    @SerializedName("position")
    public String position = "top";

    @Expose
    @SerializedName("pack_name")
    public JsonElement packName = null;

    @Expose
    @SerializedName("description")
    public JsonElement description = null;

    @Expose
    @SerializedName("description_includes_source")
    public boolean addSourceToDescription = true;

    @Expose
    @SerializedName("fixed_position")
    public boolean fixedPosition = false;

    @Override
    public String toString() {

        return "PackOptions[enabled=" + enabled + ",required=" + required + ",position=" + position + "]";
    }

    public Pack.Position getPosition() {

        if (position != null) {

            if (position.equalsIgnoreCase("top")) {
                return Pack.Position.TOP;
            }

            else if (position.equalsIgnoreCase("bottom")) {
                return Pack.Position.BOTTOM;
            }

            else {
                Constants.LOG.error("Position type '{}' is not valid. You must use 'top' or 'bottom'.", this.position);
            }
        }

        return Pack.Position.TOP;
    }

    public Component getDisplayName(String defaultPackName) {

        if (packName != null) {

            if (packName.isJsonPrimitive() && packName.getAsJsonPrimitive().isString()) {

                return Component.literal(packName.getAsString());
            }

            else if (packName.isJsonObject() || packName.isJsonArray()) {

                try {

                    return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, this.packName).getOrThrow(false, error -> Constants.LOG.error("Invalid pack name for pack '{}': {}", defaultPackName, error)).getFirst();
                }

                catch (RuntimeException e) {

                    Constants.LOG.error(e);
                }
            }
        }

        return Component.literal(defaultPackName);
    }

    public Component getDescription(String pack, Component defaultDescription) {

        if (description != null) {

            if (description.isJsonPrimitive() && description.getAsJsonPrimitive().isString()) {

                return Component.literal(description.getAsString());
            }

            else if (description.isJsonObject() || description.isJsonArray()) {

                try {

                    return ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, this.description).getOrThrow(false, error -> Constants.LOG.error("Invalid pack description for pack '{}': {}", pack, error)).getFirst();
                }

                catch (RuntimeException e) {

                    Constants.LOG.error(e);
                }
            }
        }

        return defaultDescription;
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

                    return Constants.GSON.fromJson(reader, PackOptions.class);
                }

                catch (IOException e) {

                    Constants.LOG.error("Failed to read pack options. The file is not formatted correctly! {}", optionsFile.getAbsolutePath());
                    Constants.LOG.catching(e);
                }
            }

            else {

                Constants.LOG.error("Pack options must be a file! {}", optionsFile.getAbsolutePath());
            }
        }

        // defaults
        Constants.LOG.debug("Using default pack options for {}", optionsFile.getAbsolutePath());
        return new PackOptions();
    }
}
