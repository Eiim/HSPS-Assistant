package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TestHud {
	
	private static Minecraft mc;
	private static FontRenderer fr;
	
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
		fr.func_238405_a_(event.getMatrixStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "Lobby Map", 10, 20, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "All Checkpoints 1.8-1.12", 10, 30, 0xFFFFFF);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		fr = mc.fontRenderer;
	}
	
	private static final Logger LOGGER = LogManager.getLogger();
}
