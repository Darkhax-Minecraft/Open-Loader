package net.darkhax.openloader.common.impl.conig;

import net.darkhax.pricklemc.common.api.annotations.Value;
import net.minecraft.server.packs.PackType;

public class Config {

    @Value(comment = "Determines if OpenLoader should load resource packs.")
    public boolean load_resource_packs = true;

    @Value(comment = "Determines if OpenLoader should load data packs.")
    public boolean load_data_packs = true;

    @Value(comment = "A list of additional locations to load packs from. These can be folders or direct paths to specific files. By default all file paths will be relative to the root of your game instance folder. Other locations can be specified using absolute paths.")
    public String[] additional_locations = {};

    @Value(comment = "Some launchers like CurseForge install data packs to the datapacks folder (.minecraft/datapacks). When enabled, OpenLoader will always check that folder for pack files.")
    public boolean load_datapacks_dir = true;

    public final boolean canLoad(PackType type) {
        return (type == PackType.CLIENT_RESOURCES && load_resource_packs) || (type == PackType.SERVER_DATA && load_data_packs);
    }
}