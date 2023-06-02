package io.github.eiim.hspsassistant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.eiim.hspsassistant.Categories.Category;
import io.github.eiim.hspsassistant.Categories.Lobby;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class LobbyLoader {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Categories categories;

	@SubscribeEvent
	public static void onCommonSetupEvent(FMLCommonSetupEvent event) throws IOException {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		Categories defCats;
		
		try {
			InputStream lobbiesStream = rm.open(new ResourceLocation("hspsassistant", "lobbies.json"));
			String lobbiesJSON = new String(lobbiesStream.readAllBytes(), StandardCharsets.UTF_8);
			defCats = Categories.fromJSON(lobbiesJSON);
		} catch (IOException e) {
			LOGGER.error("Can't find lobbies.json!");
			throw e;
		}
		
		Minecraft mc = Minecraft.getInstance();
		Path rootDir = mc.gameDirectory.toPath();
		File configFile = rootDir.resolve("config").resolve("hspslobbies.json").toFile();
		File defaultConfigFile = rootDir.resolve("defaultconfigs").resolve("hspslobbies.json").toFile();
		
		if(!defaultConfigFile.exists()) {
			try {
				LOGGER.debug("Attempting to create default HSPS lobbies file");
				defaultConfigFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(defaultConfigFile));
				bw.append(defCats.toJSON());
				bw.close();
			} catch (IOException e) {
				LOGGER.warn("Couldn't create default HSPS lobbies file!");
				e.printStackTrace();
			}
		}
		
		if(!configFile.exists()) {
			try {
				LOGGER.debug("Attempting to create HSPS lobbies file");
				configFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
				bw.append(defCats.toJSON());
				bw.close();
			} catch (IOException e) {
				LOGGER.warn("Couldn't create HSPS lobbies file!");
				e.printStackTrace();
			}
		}
		
		// TODO: Compare defaultConfigFile to defaultCategories
		categories = Categories.fromJSON(new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8));
		Categories oldDefCats = Categories.fromJSON(new String(Files.readAllBytes(defaultConfigFile.toPath()), StandardCharsets.UTF_8));
		
		if(defCats.equals(oldDefCats)) {
			return;
		}
		
		LOGGER.info("Update to default lobbies file detected, propagating changes");
		List<Lobby> olds = oldDefCats.lobbies;
		List<Lobby> news = defCats.lobbies;
		List<Lobby> reals = categories.lobbies;
		
		categories.lobbies = sortLobbies(olds, news, reals);
		
		LOGGER.debug("Overwriting default config");
		defaultConfigFile.delete();
		defaultConfigFile.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(defaultConfigFile));
		bw.append(defCats.toJSON());
		bw.close();
		
		LOGGER.debug("Overwriting config");
		configFile.delete();
		configFile.createNewFile();
		bw = new BufferedWriter(new FileWriter(configFile));
		bw.append(defCats.toJSON());
		bw.close();
	}
	
	private static boolean contains(List<Lobby> ll, Lobby l) {
		for(Lobby l2 : ll) {
			if(l2.name.equals(l.name)) return true;
		}
		return false;
	}
	
	private static boolean contains(List<Category> ll, Category l) {
		for(Category l2 : ll) {
			if(l2.name.equals(l.name)) return true;
		}
		return false;
	}
	
	private static List<Lobby> sortLobbies(List<Lobby> olds, List<Lobby> news, List<Lobby> reals) {
		// This algorithm isn't great - it doesn't re-order the categories file,
		// and it can do weird things when there's custom stuff. But it's probably
		// good enough for now.
		int i = 0;
		while(i < Math.max(olds.size(), news.size())) {
			if(i >= olds.size()) {
				// Insertion
				LOGGER.debug("Adding "+news.get(i));
				olds.add(i, news.get(i));
				reals.add(i, news.get(i));
				continue;
			}
			if(i >= news.size()) {
				// Deletion
				LOGGER.debug("Removing "+olds.get(i));
				olds.remove(i);
				String n = olds.get(i).name;
				for(int j = 0; j < reals.size(); j++) {
					if(reals.get(j).name.equals(n)) reals.remove(j);
				}
				continue;
			}
			if(olds.get(i).name.equals(news.get(i).name)) {
				LOGGER.debug(olds.get(i)+" match");
				reals.set(i, normalizeLobby(olds.get(i), news.get(i), reals.get(i)));
				i++;
				continue;
			}
			if(!contains(olds, news.get(i))) {
				// Insertion
				LOGGER.debug("Adding "+news.get(i));
				olds.add(i, news.get(i));
				reals.add(i, news.get(i));
				continue;
			}
			if(!contains(news, olds.get(i))) {
				// Deletion
				LOGGER.debug("Removing "+olds.get(i));
				olds.remove(i);
				String n = olds.get(i).name;
				for(int j = 0; j < reals.size(); j++) {
					if(reals.get(j).name.equals(n)) reals.remove(j);
				}
				continue;
			}
			// Swapping - TODO: swap reals
			int j = olds.indexOf(news.get(i));
			Lobby temp = olds.get(i);
			LOGGER.debug("Swapping "+temp+" and "+olds.get(j));
			olds.set(i, olds.get(j));
			olds.set(j, temp);
		}
		
		return reals;
	}
	
	private static Lobby normalizeLobby(Lobby old, Lobby n, Lobby real) {
		if(!Objects.equals(old.name, n.name) && Objects.equals(old.name, real.name))
			real.name = n.name;
		if(old.checkpoints != n.checkpoints && old.checkpoints == real.checkpoints)
			real.checkpoints = n.checkpoints;
		// "Dumb" overrides for aliases
		if(!Objects.equals(old.aliases, n.aliases) && Objects.equals(old.aliases, real.aliases))
			real.aliases = n.aliases;
		if(!Objects.equals(old.categories, n.categories))
			real.categories = sortCategories(old.categories, n.categories, real.categories);
		
		return real;
	}
	
	// TODO: condense with sortLobbies somehow
	private static List<Category> sortCategories(List<Category> olds, List<Category> news, List<Category> reals) {
		// This algorithm isn't great - it doesn't re-order the categories file,
		// and it can do weird things when there's custom stuff. But it's probably
		// good enough for now.
		int i = 0;
		while(i < Math.max(olds.size(), news.size())) {
			if(i >= olds.size()) {
				// Insertion
				LOGGER.debug("Adding "+news.get(i));
				olds.add(i, news.get(i));
				reals.add(i, news.get(i));
				continue;
			}
			if(i >= news.size()) {
				// Deletion
				LOGGER.debug("Removing "+olds.get(i));
				olds.remove(i);
				String n = olds.get(i).name;
				for(int j = 0; j < reals.size(); j++) {
					if(reals.get(j).name.equals(n)) reals.remove(j);
				}
				continue;
			}
			if(olds.get(i).name.equals(news.get(i).name)) {
				LOGGER.debug(olds.get(i)+" match");
				reals.set(i, normalizeCategory(olds.get(i), news.get(i), reals.get(i)));
				i++;
				continue;
			}
			if(!contains(olds, news.get(i))) {
				// Insertion
				LOGGER.debug("Adding "+news.get(i));
				olds.add(i, news.get(i));
				reals.add(i, news.get(i));
				continue;
			}
			if(!contains(news, olds.get(i))) {
				// Deletion
				LOGGER.debug("Removing "+olds.get(i));
				olds.remove(i);
				String n = olds.get(i).name;
				for(int j = 0; j < reals.size(); j++) {
					if(reals.get(j).name.equals(n)) reals.remove(j);
				}
				continue;
			}
			// Swapping - TODO: swap reals
			int j = olds.indexOf(news.get(i));
			Category temp = olds.get(i);
			LOGGER.debug("Swapping "+temp+" and "+olds.get(j));
			olds.set(i, olds.get(j));
			olds.set(j, temp);
		}
		
		return reals;
	}
	
	private static Category normalizeCategory(Category old, Category n, Category real) {
		if(!Objects.equals(old.name, n.name) && Objects.equals(old.name, real.name))
			real.name = n.name;
		// "Dumb" overrides for checkpoints
		if(old.checkpoints != n.checkpoints && old.checkpoints == real.checkpoints)
			real.checkpoints = n.checkpoints;
		// "Dumb" overrides for variables
		if(!Objects.equals(old.variables, n.variables))
			real.variables = n.variables;
		// "Dumb" overrides for checkpointOverrides
		if(!Objects.equals(old.checkpointOverrides, n.checkpointOverrides))
			real.checkpointOverrides = n.checkpointOverrides;
		
		return real;
	}

}
