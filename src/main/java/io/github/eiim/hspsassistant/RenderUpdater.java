package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.codehaus.plexus.util.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@OnlyIn(Dist.CLIENT)
public class RenderUpdater {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static Minecraft mc;
	
	private static ArtifactVersion mcVersion;
	private static boolean isNew;
	
	public static String category = "All Checkpoints";
	
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
			
			GraphicsHelper.drawTextShadow(event.getPoseStack(), "Hypixel Server Parkour", 10, 10, 0xFFFFFF);
			GraphicsHelper.drawTextShadow(event.getPoseStack(), lobby, 10, 20, 0xFFFFFF);
			GraphicsHelper.drawTextShadow(event.getPoseStack(), category+" "+(isNew ? "1.13+" : "1.8-1.12"), 10, 30, 0xFFFFFF);
		}
		
		// Key indicators
		
		int width = mc.getWindow().getGuiScaledWidth();
		int sqSize = 20;
		int lineWidth = 1;
		int spacing = 3;
		
		int x = width - 2*sqSize - 2*spacing;
		int y = spacing;
		int fill = KeyMonitor.forward.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 3*sqSize - 3*spacing;
		y = spacing;
		fill = KeyMonitor.sprint.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		// TODO: Add ctrl/"helm" icon
		
		x = width - 3*sqSize - 3*spacing;
		y = 2*spacing + sqSize;
		fill = KeyMonitor.left.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 2*sqSize - 2*spacing;
		y = 2*spacing + sqSize;
		fill = KeyMonitor.back.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - sqSize - spacing;
		y = 2*spacing + sqSize;
		fill = KeyMonitor.right.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, fill, 0xFFFFFFFF);
		
		x = width - 3*sqSize - 3*spacing;
		y = 3*spacing + 2*sqSize;
		fill = KeyMonitor.jump.isDown() ? 0x88FFFFFF : 0;
		GraphicsHelper.drawRect(event.getPoseStack(), x, y, width-spacing-x, sqSize/2, lineWidth, fill, 0xFFFFFFFF);
    }
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
	}

}
