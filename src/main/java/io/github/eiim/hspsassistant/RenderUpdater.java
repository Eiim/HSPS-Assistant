package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import io.github.eiim.hspsassistant.Categories.Category;
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
	
	private static final int WHITE = 0xFFFFFFFF;
	private static final int TRANS_WHITE = 0x88FFFFFF;
	private static final int TRANS_BLACK = 0x44000000;
	private static final int CLEAR = 0x00000000;
	private static final String TITLE = "Hypixel Server Parkour";
	
	private static Lobby lobby;
	private static Category category;
	private static int categoryId;
	private static String[] variables = new String[0];
	private static int variablesId;
	private static int variablesOptions;
	private static boolean inHypixel = true;
	private static boolean worldRefreshed = false;
	
	@SubscribeEvent
    public void onRender(RenderGuiEvent event) {
		
		if(inHypixel) {
			int screenBorder = 3;
			int padding = 3;
			String catString = (category == null ? "" : category.name)+" "+String.join(" ", variables);
			int titleWidth = GraphicsHelper.getTextWidth(TITLE);
			int lobbyWidth = GraphicsHelper.getTextWidth(lobby == null ? "" : lobby.name);
			int catWidth = GraphicsHelper.getTextWidth(catString);
			int maxWidth = Math.max(titleWidth, Math.max(catWidth, lobbyWidth));
			int lineWidth = 1;
			ColorSettings tlcs = new ColorSettings(WHITE, TRANS_BLACK, WHITE);
			
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder, maxWidth + 2*padding, 7 + 2*padding, TITLE, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 6 + 2*padding, maxWidth + 2*padding, 7 + 2*padding, (lobby == null ? "" : lobby.name), lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 12 + 4*padding, maxWidth + 2*padding, 7 + 2*padding, catString, lineWidth, tlcs);
		}
		
		// Key indicators
		
		int width = mc.getWindow().getGuiScaledWidth();
		int sqSize = 20;
		int lineWidth = 1;
		int spacing = 3;
		ColorSettings pressed = new ColorSettings(WHITE, TRANS_WHITE, WHITE);
		ColorSettings unpressed = new ColorSettings(WHITE, CLEAR, WHITE);
		
		int x = width - sqSize - spacing;
		int y = spacing;
		GraphicsHelper.drawRectBordered(event.getPoseStack(), x, y, sqSize, sqSize, lineWidth, KeyMonitor.sneak.isDown() ? pressed : unpressed);
		
		x = width - 2*sqSize - 2*spacing;
		y = spacing;
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
			worldRefreshed = false;
			updateLobbyCat();
		}
	}
	
	private void updateLobbyCat() {
		LOGGER.debug("Updating lobby");
		if(mc.level == null) return;
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = sb.getDisplayObjective(1);
		// Only draw Hypixel overlays if we're on Hypixel or at least something with a scoreboard
		inHypixel = obj != null;
		
		if(inHypixel) {
			String propLobby = obj.getDisplayName().getString();
			
			boolean foundLobby = false;
			for(Lobby l : SettingsLoader.categories.lobbies) {
				if(propLobby.toLowerCase().equals(l.name.toLowerCase())) {
					lobby = l;
					foundLobby = true;
				} else {
					if(l.aliases != null) {
						for(String s : l.aliases) {
							if(propLobby.toLowerCase().equals(s.toLowerCase())) {
								lobby = l;
								foundLobby = true;
							}
						}
					}
				}
			}
			
			if(!foundLobby) {
				// Failed to find lobby - default for first for now
				lobby = SettingsLoader.categories.lobbies.get(0);
			}
			
			boolean matchingCat = false;
			if(category != null) {
				for(int i = 0; i < lobby.categories.size(); i++) {
					Category c = lobby.categories.get(i);
					if(c.name == category.name) {
						category = c;
						categoryId = i;
						matchingCat = true;
					}
				}
			}
			if(!matchingCat) {
				// Failed to find category - default to first
				category = lobby.categories.get(0);
				categoryId = 0;
			}
			
			variablesId = 0;
			variables = new String[category.variables == null ? 0 : category.variables.size()];
			variablesOptions = 1;
			for(int i = 0; i < variables.length; i++) {
				variables[i] = category.variables.get(i).options[0];
				variablesOptions *= category.variables.get(i).options.length;
			}
		}
	}
	
	public static void switchCategory() {
		categoryId = (categoryId + 1) % lobby.categories.size();
		category = lobby.categories.get(categoryId);
		
		variablesId = 0;
		variables = new String[category.variables == null ? 0 : category.variables.size()];
		variablesOptions = 1;
		for(int i = 0; i < variables.length; i++) {
			variables[i] = category.variables.get(i).options[0];
			variablesOptions *= category.variables.get(i).options.length;
		}
	}
	
	public static void switchVariables() {
		variablesId = (variablesId + 1) % variablesOptions;
		LOGGER.debug(variablesId+"/"+variablesOptions);
		int runProd = 1;
		// Loop through variables backwards so the first one is the "top-most" variable
		for(int i = variables.length - 1; i >= 0; i--) {
			String[] opts = category.variables.get(i).options;
			runProd *= opts.length;
			variables[i] = opts[(variablesId % runProd)/(runProd / opts.length)];
		}
	}
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
		mc = Minecraft.getInstance();
		
		mcVersion = ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
		isNew = mcVersion.getMinorVersion() > 12;
	}

}
