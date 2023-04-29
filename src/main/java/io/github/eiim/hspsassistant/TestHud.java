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
		fr.func_238405_a_(event.getMatrixStack(), "Test HUD running!", 100, 50, 15728880);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		fr = mc.fontRenderer;
	}
	
	private static final Logger LOGGER = LogManager.getLogger();
}
