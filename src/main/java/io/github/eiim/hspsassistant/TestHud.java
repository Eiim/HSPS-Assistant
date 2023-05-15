package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@OnlyIn(Dist.CLIENT)
public class TestHud {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static Minecraft mc;
	private static Font ft;
	
	private static KeyMapping forward;
	private static KeyMapping left;
	private static KeyMapping right;
	private static KeyMapping back;
	private static KeyMapping jump;
	private static KeyMapping sprint;
	
	private static ArtifactVersion mcVersion;
	private static boolean isNew;
	
	public static String category;
	
	@SubscribeEvent
    public void onRender(RenderGuiEvent event) {
		
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = sb.getDisplayObjective(1);
		String lobby = "";
		if(obj != null) { // Only draw Hypixel overlays if we're on Hypixel or at least something with a scoreboard
			lobby = obj.getDisplayName().getString();
			
			if("HYPIXEL".equals(lobby)) {
				lobby = "Main Lobby";
			} else {
				lobby = StringUtils.capitaliseAllWords(lobby.toLowerCase());
			}
			
			ft.drawShadow(event.getPoseStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
			ft.drawShadow(event.getPoseStack(), lobby, 10, 20, 0xFFFFFF);
			ft.drawShadow(event.getPoseStack(), category+" "+(isNew ? "1.13+" : "1.8-1.12"), 10, 30, 0xFFFFFF);
		}
		
		// Key indicators
		
		int width = mc.getWindow().getGuiScaledWidth();
		int sqSize = 20;
		int lineWidth = 1;
		int spacing = 3;
		
		int x = width - 2*sqSize - 2*spacing;
		int y = spacing;
		int fill = forward.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 3*sqSize - 3*spacing;
		y = spacing;
		fill = sprint.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		// TODO: Add ctrl/"helm" icon
		
		x = width - 3*sqSize - 3*spacing;
		y = 2*spacing + sqSize;
		fill = left.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 2*sqSize - 2*spacing;
		y = 2*spacing + sqSize;
		fill = back.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - sqSize - spacing;
		y = 2*spacing + sqSize;
		fill = right.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 3*sqSize - 3*spacing;
		y = 3*spacing + 2*sqSize;
		fill = jump.isDown() ? 0x88FFFFFF : 0;
		drawRect(event.getPoseStack(), x, y, width-spacing-x, sqSize/2, lineWidth, fill, 0xFFFFFFFF);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		ft = mc.font;
		
		forward = mc.options.keyUp;
		left = mc.options.keyLeft;
		right = mc.options.keyRight;
		back = mc.options.keyDown;
		jump = mc.options.keyJump;
		sprint = mc.options.keySprint;
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
		
		category = "All Checkpoints";
	}
	
	private static void drawRect(PoseStack ps, int x, int y, int width, int height, int lineWidth, int fill, int border) {
		// Fill
		GuiComponent.fill(ps, x, y, x+width, y+height, fill);
		
		// Border
		int offsetIn = (int)Math.ceil(lineWidth/2.0);
		int offsetOut = (int)Math.floor(lineWidth/2.0);
		GuiComponent.fill(ps, x-offsetOut, y-offsetOut, x+offsetIn, y+height+offsetOut, border); // Left
		GuiComponent.fill(ps, x+width-offsetIn, y-offsetOut, x+width+offsetOut, y+height+offsetOut, border); // Right
		GuiComponent.fill(ps, x+offsetIn, y-offsetOut, x+width-offsetIn, y+offsetIn, border); // Top
		GuiComponent.fill(ps, x+offsetIn, y+height-offsetIn, x+width-offsetIn, y+height+offsetOut, border); // Bottom
	}
}
