package net.darkhax.openloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.dto.GuardedSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static final String MOD_ID = "openloader";
    public static final String MOD_NAME = "Open Loader";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
}