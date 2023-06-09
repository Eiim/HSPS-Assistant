package io.github.eiim.hspsassistant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

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
	
	private static final String TITLE = "Hypixel Server Parkour";
	
	private static Lobby lobby;
	private static Category category;
	private static int categoryId;
	private static String[] variables = new String[0];
	private static int variablesId;
	private static int variablesOptions;
	private static boolean inHypixel = false;
	private static boolean worldRefreshed = false;
	private static Timing timing;
	private static String time = "00:00.000";
	private static RunResult pb;
	
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
			int pbsob = 0;
			
			if(HSPSConfig.compareSOB.get()) {
				int[] cps = category.getCheckpoints(variables);
				for(int i = 0; i <= cps.length; i++) {
					int goldsplit = gdf.getGold(lobby.name, category.name, variables, i == cps.length ? lobby.checkpoints+1 : cps[i]);
					if(goldsplit == Integer.MAX_VALUE) {
						pbsob = 0;
						break;
					}
					pbsob += goldsplit;
				}
			} else {
				pbsob = pb == null ? 0 : pb.time;
			}
			
			ColorSettings tlcs = new ColorSettings(0xFF000000+HSPSConfig.uiColor.get(), HSPSConfig.bgOpacity.get() << 24 + HSPSConfig.bgColor.get(), 0xFF000000+HSPSConfig.uiColor.get());
			
			GraphicsHelper.drawRectTextBordered(screenBorder, screenBorder, width, height, TITLE, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(screenBorder, screenBorder + 6 + 2*padding, width, height, (lobby == null ? "" : lobby.name), lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(screenBorder, screenBorder + 12 + 4*padding, width, height, catString, lineWidth, tlcs);
			GraphicsHelper.drawRectTextBordered(screenBorder, screenBorder + 18 + 6*padding, width, height, time+" | "+Timing.millisToTimestring(pbsob), lineWidth, tlcs);
			
			if(timing != null) {
				for(int i = 0; i < timing.segmentTimes.length; i++) {
					String cp = i == timing.checkpoints.length ? "Finish" : "CP "+timing.checkpoints[i];
					GraphicsHelper.drawRectTextBordered(screenBorder, screenBorder + (6 + 2*padding)*(i+4), width/2, height, cp, lineWidth, tlcs);
					int time = timing.segmentTimes[i];
					if(time == 0) {
						if(HSPSConfig.compareSOB.get()) {
							int cpn = i == timing.checkpoints.length ? lobby.checkpoints+1 : timing.checkpoints[i];
							time = gdf.getGold(lobby.name, category.name, variables, cpn);
						} else {
							time = pb.splitTimes[i];
						}
					}
					GraphicsHelper.drawRectTextBordered(screenBorder + width/2, screenBorder + (6 + 2*padding)*(i+4), width/2, height, Timing.millisToTimestring(time), lineWidth, tlcs);
				}
			}
		}
		
		// Key indicators
		if(HSPSConfig.renderKeys.get()) {
			int width = mc.getWindow().getGuiScaledWidth();
			int sqSize = 20;
			int lineWidth = 1;
			int spacing = 3;
			ColorSettings pressed = new ColorSettings(0xFF000000+HSPSConfig.uiColor.get(), (HSPSConfig.keyOpacity.get() << 24) + HSPSConfig.uiColor.get(), 0xFF000000+HSPSConfig.uiColor.get());
			ColorSettings unpressed = new ColorSettings(0xFF000000+HSPSConfig.uiColor.get(), 0x00000000, 0xFF000000+HSPSConfig.uiColor.get());
			
			int x = width - sqSize - spacing;
			int y = spacing;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.sneak.isDown() ? pressed : unpressed);
			
			x = width - 2*sqSize - 2*spacing;
			y = spacing;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.forward.isDown() ? pressed : unpressed);
			
			x = width - 3*sqSize - 3*spacing;
			y = spacing;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.sprint.isDown() ? pressed : unpressed);
			// TODO: Add ctrl/"helm" icon
			
			x = width - 3*sqSize - 3*spacing;
			y = 2*spacing + sqSize;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.left.isDown() ? pressed : unpressed);
			
			x = width - 2*sqSize - 2*spacing;
			y = 2*spacing + sqSize;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.back.isDown() ? pressed : unpressed);
			
			x = width - sqSize - spacing;
			y = 2*spacing + sqSize;
			GraphicsHelper.drawRectBordered(x, y, sqSize, sqSize, lineWidth, KeyMonitor.right.isDown() ? pressed : unpressed);
			
			x = width - 3*sqSize - 3*spacing;
			y = 3*spacing + 2*sqSize;
			GraphicsHelper.drawRectBordered(x, y, width-spacing-x, sqSize/2, lineWidth, KeyMonitor.jump.isDown() ? pressed : unpressed);
		}
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
			for(Lobby l : LobbyLoader.categories.lobbies) {
				if(nameMatch(propLobby, l)) {
					lobby = l;
					foundLobby = true;
				}
			}
			
			if(!foundLobby) {
				// Failed to find lobby - default for first for now
				lobby = LobbyLoader.categories.lobbies.get(0);
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
			pb = pbf.getPB(lobby.name, category.name, variables);
		}
	}
	
	public static void switchLobby() {
		Scoreboard sb = mc.level.getScoreboard();
		Objective obj = sb.getDisplayObjective(1);
		inHypixel = obj != null;
		if(inHypixel) {
			String propLobby = obj.getDisplayName().getString();
			// Find matching lobbies
			ArrayList<Lobby> matching = new ArrayList<>();
			for(Lobby l : LobbyLoader.categories.lobbies) {
				if(nameMatch(propLobby, l)) matching.add(l);
			}
			LOGGER.debug(matching.toString());
			int currIdx = matching.indexOf(lobby);
			int newIdx = (currIdx + 1) % matching.size();
			LOGGER.debug(currIdx+" "+newIdx);
			if(newIdx != currIdx) {
				lobby = matching.get(newIdx);
				boolean matchingCat = false;
				for(int i = 0; i < lobby.categories.size(); i++) {
					Category c = lobby.categories.get(i);
					if(c.name == category.name) {
						category = c;
						categoryId = i;
						matchingCat = true;
					}
				}
				if(!matchingCat) {
					// Failed to find category - default to first
					category = lobby.categories.get(0);
					categoryId = 0;
				}
				
				timing = null;
				time = "00:00.000";
				pb = pbf.getPB(lobby.name, category.name, variables);
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
		
		timing = null;
		time = "00:00.000";
		pb = pbf.getPB(lobby.name, category.name, variables);
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
		pb = pbf.getPB(lobby.name, category.name, variables);
	}
	
	public static void startTiming() {
		int[] cps = category.getCheckpoints(variables);
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
	
	private static boolean nameMatch(String prop, Lobby lobby) {
		if(prop.toLowerCase().equals(lobby.name.toLowerCase())) return true;
		if(lobby.aliases == null) return false;
		for(String s : lobby.aliases) {
			if(s.toLowerCase().equals(prop.toLowerCase())) return true;
		}
		return false;
	}

}
