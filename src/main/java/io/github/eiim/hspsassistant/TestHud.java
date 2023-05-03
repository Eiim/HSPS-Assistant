package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TestHud {
	
	private static Minecraft mc;
	private static FontRenderer fr;
	
	private static KeyBinding forward;
	private static KeyBinding left;
	private static KeyBinding right;
	private static KeyBinding back;
	private static KeyBinding jump;
	private static KeyBinding sprint;
	
	//private static String mcVersion;
	private static ArtifactVersion mcVersion;
	private static boolean isNew;
	
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
		fr.func_238405_a_(event.getMatrixStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "Lobby Map", 10, 20, 0xFFFFFF);
		fr.func_238405_a_(event.getMatrixStack(), "All Checkpoints "+(isNew ? "1.13+" : "1.8-1.12"), 10, 30, 0xFFFFFF);
		
		String inputStr = "";
		inputStr += forward.isKeyDown() ? "W" : " ";
		inputStr += left.isKeyDown() ? "A" : " ";
		inputStr += back.isKeyDown() ? "S" : " ";
		inputStr += right.isKeyDown() ? "D" : " ";
		
		fr.func_238405_a_(event.getMatrixStack(), inputStr, 10, 40, 0xFFFFFF);
		
		Scoreboard sb = mc.world.getScoreboard();
		ScoreObjective obj = sb.getObjectiveInDisplaySlot(1);
		String lobby = obj.getDisplayName().getString();
		fr.func_238405_a_(event.getMatrixStack(), lobby, 10, 50, 0xFFFFFF);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		fr = mc.fontRenderer;
		
		forward = mc.gameSettings.keyBindForward;
		left = mc.gameSettings.keyBindLeft;
		right = mc.gameSettings.keyBindRight;
		back = mc.gameSettings.keyBindBack;
		jump = mc.gameSettings.keyBindJump;
		sprint = mc.gameSettings.keyBindSprint;
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
	}
	
	private static final Logger LOGGER = LogManager.getLogger();
}
