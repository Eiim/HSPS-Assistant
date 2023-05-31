package io.github.eiim.hspsassistant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@OnlyIn(Dist.CLIENT)
public class RenderUpdater {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static Minecraft mc;
	
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
	private static Timing timing;
	private static String time = "00:00.000";
	private static String pbTime = "00:00.000";
	
	private static RunDataFile rdf;
	private static PBFile pbf;
	private static GoldsFile gdf;
	
	@SubscribeEvent
    public void onRender(RenderGuiEvent event) {
		
		if(inHypixel) {
			int screenBorder = 3;
			int padding = 3;
			String catString = (category == null ? "" : category.name)+" "+String.join(" ", variables);
			if(timing != null && timing.active)
				time = timing.sinceStartString();
			int titleWidth = GraphicsHelper.getTextWidth(TITLE);
			int lobbyWidth = GraphicsHelper.getTextWidth(lobby == null ? "" : lobby.name);
			int catWidth = GraphicsHelper.getTextWidth(catString);
			int timeWidth = GraphicsHelper.getTextWidth("00:00.000 | 00:00.000");
			int maxWidth = Math.max(timeWidth, Math.max(titleWidth, Math.max(catWidth, lobbyWidth)));
			int lineWidth = 1;
			int width = maxWidth + 2*padding;
			int height = 7 + 2*padding;
			ColorSettings tlcs = new ColorSettings(WHITE, TRANS_BLACK, WHITE);
			
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder, width, height, TITLE, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 6 + 2*padding, width, height, (lobby == null ? "" : lobby.name), lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 12 + 4*padding, width, height, catString, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + 18 + 6*padding, width, height, time+" | "+pbTime, lineWidth, tlcs);
			
			if(timing != null) {
				for(int i = 0; i < timing.segmentTimes.length; i++) {
					String cp = i == timing.checkpoints.length ? "Finish" : "CP "+timing.checkpoints[i];
					GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder, screenBorder + (6 + 2*padding)*(i+4), width/2, height, cp, lineWidth, tlcs);
					GraphicsHelper.drawRectTextBordered(event.getPoseStack(), screenBorder + width/2, screenBorder + (6 + 2*padding)*(i+4), width/2, height, Timing.millisToTimestring(timing.segmentTimes[i]), lineWidth, tlcs);
				}
			}
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
			
			timing = null;
			time = "00:00.000";
			pbTime = Timing.millisToTimestring(pbf.getPB(lobby.name, category.name, variables));
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
		
		timing = null;
		time = "00:00.000";
		pbTime = Timing.millisToTimestring(pbf.getPB(lobby.name, category.name, variables));
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
		
		timing = null;
		time = "00:00.000";
	}
	
	public static void startTiming() {
		int[] cps = category.checkpoints;
		if(category.checkpointOverrides != null) {
			for(Categories.Override o : category.checkpointOverrides) {
				if(o.matches(variables)) {
					cps = o.checkpoints;
				}
			}
		}
		timing = new Timing(cps);
	}
	
	public static void splitDelta(int delta) {
		timing.setDelta(delta);
		if(timing.active == false) {
			int total = timing.cumulativeTimes[timing.cumulativeTimes.length-1];
			time = Timing.millisToTimestring(total);
			// Manually box int[] to Integer[]
			Integer[] splits = new Integer[timing.segmentTimes.length];
			for(int i = 0; i < splits.length; i++) {
				splits[i] = timing.segmentTimes[i];
			}
			RunResult rr = new RunResult(lobby.name, category.name, variables, total, splits);
			rdf.appendRun(rr);
			pbf.registerRun(rr);
			// If we got a PB, display it
			pbTime = Timing.millisToTimestring(pbf.getPB(lobby.name, category.name, variables));
		}
		gdf.registerRun(new Segment(lobby.name, category.name, variables, timing.lastCP, delta));
	}
	
	public static void splitTotal(int cp, int total) {
		timing.cpTotal(cp, total);
	}
	
	public static void finalTime(int total) {
		timing.cpTotal(lobby.checkpoints+1, total);
		timing.active = false;
	}
	
	public static void stopTiming() {
		timing.active = false;
	}
	
	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) throws IOException {
		mc = Minecraft.getInstance();
		
		Path rootDir = mc.gameDirectory.toPath();
		File configFile = rootDir.resolve("data").resolve("hspsruns.csv").toFile();
		rdf = new RunDataFile(configFile);
		File pbFile = rootDir.resolve("data").resolve("hspspbs.csv").toFile();
		pbf = new PBFile(pbFile);
		File gdFile = rootDir.resolve("data").resolve("hspsgolds.csv").toFile();
		gdf = new GoldsFile(gdFile);
		
		// ModList.get().getModFileById("minecraft").getMods().get(0).getVersion();
	}

}
