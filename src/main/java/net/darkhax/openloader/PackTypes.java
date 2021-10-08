package net.darkhax.openloader;

import java.io.File;
import java.util.function.BooleanSupplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackSource;

public enum PackTypes implements PackSource {

    DATA("Data Pack", "config/openloader/data", OpenLoader.CONFIG::allowDataPacks),
    RESOURCES("Resource Pack", "config/openloader/resources", OpenLoader.CONFIG::allowResourcePacks);

    final String displayName;
    final String path;
    final File directory;
    final BooleanSupplier enabled;
    final Component sourceName;

    PackTypes (String name, String path, BooleanSupplier enabled) {

        this.displayName = name;
        this.path = path;
        this.directory = new File(path);
        this.enabled = enabled;
        this.sourceName = new TranslatableComponent("openloader.source." + this.name().toLowerCase());
    }

    public File getDirectory () {

        if (!this.directory.exists()) {

            this.directory.mkdirs();
        }

        return this.directory;
    }

    @Override
    public Component decorate (Component packName) {

        return new TranslatableComponent("pack.nameAndSource", packName, this.sourceName).withStyle(ChatFormatting.GRAY);
    }
}