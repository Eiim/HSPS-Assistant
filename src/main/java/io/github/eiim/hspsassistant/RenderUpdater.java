package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import io.github.eiim.hspsassistant.Categories.Lobby;
import io.github.eiim.hspsassistant.GraphicsHelper.ColorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.level.LevelEvent;
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
	
	private static final int WHITE = 0xFFFFFFFF;
	private static final int TRANS_WHITE = 0x88FFFFFF;
	private static final int TRANS_BLACK = 0x44000000;
	private static final int CLEAR = 0x00000000;
	private static final String title = "Hypixel Server Parkour";
	
	private String lobby;
	private boolean inHypixel = true;
	private boolean worldRefreshed = false;
	
	@SubscribeEvent
    public void onRender(RenderGuiEvent event) {
		
		if(inHypixel) {
			int screenBorder = 3;
			int padding = 3;
			String catString = category+" "+(isNew ? "1.13+" : "1.8-1.12");
			int titleWidth = GraphicsHelper.getTextWidth(title);
			int lobbyWidth = GraphicsHelper.getTextWidth(lobby);
			int catWidth = GraphicsHelper.getTextWidth(catString);
			int maxWidth = Math.max(titleWidth, Math.max(catWidth, lobbyWidth));
			int lineWidth = 1;
			ColorSettings tlcs = new ColorSettings(WHITE, TRANS_BLACK, WHITE);
			
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder, maxWidth + 2*padding, 7 + 2*padding, title, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 6 + 2*padding, maxWidth + 2*padding, 7 + 2*padding, lobby, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 12 + 4*padding, maxWidth + 2*padding, 7 + 2*padding, catString, lineWidth, tlcs);
		}
		
		// Key indicators
		
		int width = mc.getWindow().getGuiScaledWidth();
		int sqSize = 20;
		int lineWidth = 1;
		int spacing = 3;
		ColorSettings pressed = new ColorSettings(WHITE, TRANS_WHITE, WHITE);
		ColorSettings unpressed = new ColorSettings(WHITE, CLEAR, WHITE);
		
		int x = width - 2*sqSize - 2*spacing;
		int y = spacing;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.forward.isDown() ? pressed : unpressed);
		
		x = width - 3*sqSize - 3*spacing;
		y = spacing;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.sprint.isDown() ? pressed : unpressed);
		// TODO: Add ctrl/"helm" icon
		
		x = width - 3*sqSize - 3*spacing;
		y = 2*spacing + sqSize;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.left.isDown() ? pressed : unpressed);
		
		x = width - 2*sqSize - 2*spacing;
		y = 2*spacing + sqSize;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.back.isDown() ? pressed : unpressed);
		
		x = width - sqSize - spacing;
		y = 2*spacing + sqSize;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.right.isDown() ? pressed : unpressed);
		
		x = width - 3*sqSize - 3*spacing;
		y = 3*spacing + 2*sqSize;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, width-spacing-x, sqSize/2, lineWidth, KeyMonitor.jump.isDown() ? pressed : unpressed);
    }
	
	@SubscribeEvent
	public void onLogin(LevelEvent.Load event) {
		worldRefreshed = true;
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(worldRefreshed) {
			worldRefreshed = !updateLobbyCat();
		}
	}
	
	private boolean updateLobbyCat() {
		LOGGER.debug("Updating lobby");
		if(mc.level == null) return false;
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = sb.getDisplayObjective(1);
		// Only draw Hypixel overlays if we're on Hypixel or at least something with a scoreboard
		inHypixel = obj != null;
		
		if(inHypixel) {
			lobby = obj.getDisplayName().getString();
			
			for(Lobby l : SettingsLoader.categories.lobbies) {
				if(lobby.toLowerCase().equals(l.name.toLowerCase())) {
					lobby = l.name;
					return true;
				} else {
					if(l.aliases != null) {
						for(String s : l.aliases) {
							if(lobby.toLowerCase().equals(s.toLowerCase())) {
								lobby = l.name;
								return true;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
	}

}
