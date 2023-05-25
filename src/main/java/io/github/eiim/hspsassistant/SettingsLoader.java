package io.github.eiim.hspsassistant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class SettingsLoader {
	
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		try {
			InputStream lobbiesStream = rm.open(new ResourceLocation("hspsassistant", "lobbies.json"));
			String lobbiesJSON = new String(lobbiesStream.readAllBytes(), StandardCharsets.UTF_8);
			//Categories.fromJSON(lobbiesJSON);
		} catch (IOException e) {
			LOGGER.error("Can't find lobbies.json!");
			e.printStackTrace();
		}
	}

}
