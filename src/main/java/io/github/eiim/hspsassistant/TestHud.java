package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
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
	
	private static ArtifactVersion mcVersion;
	private static boolean isNew;
	
	@SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
		
		Scoreboard sb = mc.world.getScoreboard();
		ScoreObjective obj = sb.getObjectiveInDisplaySlot(1);
		String lobby = "";
		if(obj != null) {
			lobby = obj.getDisplayName().getString();
		}
		if("HYPIXEL".equals(lobby)) {
			lobby = "Main Lobby";
		} else {
			lobby = StringUtils.capitaliseAllWords(lobby.toLowerCase());
		}		
		if(obj != null) {
			fr.func_238405_a_(event.getMatrixStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
			fr.func_238405_a_(event.getMatrixStack(), lobby, 10, 20, 0xFFFFFF);
			fr.func_238405_a_(event.getMatrixStack(), "All Checkpoints "+(isNew ? "1.13+" : "1.8-1.12"), 10, 30, 0xFFFFFF);
		}
		
		// Key indicators
		
		int width = mc.getMainWindow().getScaledWidth();
		int sqSize = 20;
		int lineWidth = 2;
		int spacing = 4;
		
		// Draw backgrounds
		if(forward.isKeyDown()) {
			int x = width - 2*sqSize - 2*spacing;
			int y = spacing;
			AbstractGui.func_238467_a_(event.getMatrixStack(), x, y, x+sqSize, y+sqSize, 0x88FFFFFF);
		}
		if(left.isKeyDown()) {
			int x = width - 3*sqSize - 3*spacing;
			int y = 2*spacing + sqSize;
			AbstractGui.func_238467_a_(event.getMatrixStack(), x, y, x+sqSize, y+sqSize, 0x88FFFFFF);
		}
		if(back.isKeyDown()) {
			int x = width - 2*sqSize - 2*spacing;
			int y = 2*spacing + sqSize;
			AbstractGui.func_238467_a_(event.getMatrixStack(), x, y, x+sqSize, y+sqSize, 0x88FFFFFF);
		}
		if(right.isKeyDown()) {
			int x = width - sqSize - spacing;
			int y = 2*spacing + sqSize;
			AbstractGui.func_238467_a_(event.getMatrixStack(), x, y, x+sqSize, y+sqSize, 0x88FFFFFF);
		}
		
		if(jump.isKeyDown()) {
			int x = width - 3*sqSize - 3*spacing;
			int y = 3*spacing + 2*sqSize;
			int height = sqSize/2;
			AbstractGui.func_238467_a_(event.getMatrixStack(), x, y, width-spacing, y+height, 0x88FFFFFF);
		}
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
