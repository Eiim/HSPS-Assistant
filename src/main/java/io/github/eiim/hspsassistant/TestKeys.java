package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class TestKeys {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final KeyMapping categoryMapping = new KeyMapping("key.hsps.category", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.hsps");

	@SubscribeEvent
	public static void registerMappings(RegisterKeyMappingsEvent event) {
		event.register(categoryMapping);
		LOGGER.debug("Registered mapping");
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
			while (categoryMapping.consumeClick()) {
				LOGGER.debug("Category switched!");
				switch(TestHud.category) {
					case "All Checkpoints":
						TestHud.category = "Any%";
						break;
					case "Any%":
						TestHud.category = "Seasonal%";
						break;
					case "Seasonal%":
						TestHud.category = "All Checkpoints";
						break;
					default:
						TestHud.category = "Any%";
				}
		    }
		}
	}

}
