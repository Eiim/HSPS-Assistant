package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TestHud {
	
	private static Minecraft mc;
	private static FontRenderer fr;
	private static long mchandle;
	
	private static int forward;
	private static int left;
	private static int right;
	private static int back;
	private static int jump;
	private static int sprint;
	
	//private static String mcVersion;
	private static ArtifactVersion mcVersion;
	private static boolean isNew;
	
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
		fr.func_238405_a_(event.getMatrixStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "Lobby Map", 10, 20, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "All Checkpoints "+(isNew ? "1.13+" : "1.8-1.12"), 10, 30, 0xFFFFFF);
		
		boolean forwardDown = InputMappings.isKeyDown(mchandle, forward);
		boolean leftDown = InputMappings.isKeyDown(mchandle, left);
		boolean rightDown = InputMappings.isKeyDown(mchandle, right);
		boolean backDown = InputMappings.isKeyDown(mchandle, back);
		
		String inputStr = "";
		inputStr += forwardDown ? "W" : " ";
		inputStr += leftDown ? "A" : " ";
		inputStr += backDown ? "S" : " ";
		inputStr += rightDown ? "D" : " ";
		
		fr.func_238405_a_(event.getMatrixStack(), inputStr, 10, 40, 0xFFFFFF);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		fr = mc.fontRenderer;
		mchandle = mc.getMainWindow().getHandle();
		
		forward = mc.gameSettings.keyBindForward.getKey().getKeyCode();
		left = mc.gameSettings.keyBindLeft.getKey().getKeyCode();
		right = mc.gameSettings.keyBindRight.getKey().getKeyCode();
		back = mc.gameSettings.keyBindBack.getKey().getKeyCode();
		jump = mc.gameSettings.keyBindJump.getKey().getKeyCode();
		sprint = mc.gameSettings.keyBindSprint.getKey().getKeyCode();
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
	}
	
	private static final Logger LOGGER = LogManager.getLogger();
}
