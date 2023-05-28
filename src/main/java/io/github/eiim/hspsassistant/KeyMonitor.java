package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class KeyMonitor {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static Minecraft mc = Minecraft.getInstance();
	
	public static KeyMapping forward = mc.options.keyUp;
	public static KeyMapping left = mc.options.keyLeft;
	public static KeyMapping right = mc.options.keyRight;
	public static KeyMapping back = mc.options.keyDown;
	public static KeyMapping jump = mc.options.keyJump;
	public static KeyMapping sprint = mc.options.keySprint;
	public static KeyMapping sneak = mc.options.keyShift;
	
	public static final KeyMapping categoryMapping = new KeyMapping("key.hsps.category", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.hsps");
	public static final KeyMapping variablesMapping = new KeyMapping("key.hsps.variables", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.hsps");

	@SubscribeEvent
	public static void registerMappings(RegisterKeyMappingsEvent event) {
		event.register(categoryMapping);
		event.register(variablesMapping);
		LOGGER.debug("Registered mappings");
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
			while (categoryMapping.consumeClick()) {
				RenderUpdater.switchCategory();
				LOGGER.debug("Category switched!");
		    }
			while (variablesMapping.consumeClick()) {
				RenderUpdater.switchVariables();
				LOGGER.debug("Variables switched!");
		    }
		}
	}

}
