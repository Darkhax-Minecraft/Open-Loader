package net.darkhax.festiveskulls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod("openloader")
public final class OpenLoader {

	public static final Logger LOGGER = LogManager.getLogger("Open Loader");
	
	public OpenLoader() {

		MinecraftForge.EVENT_BUS.addListener(this::onServerStart);		
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().getResourcePackList().addPackFinder(OpenLoaderPackFinder.RESOUCE));
	}
	
	private void onServerStart(FMLServerAboutToStartEvent event) {
		
		event.getServer().getResourcePacks().addPackFinder(OpenLoaderPackFinder.DATA);
	}
}