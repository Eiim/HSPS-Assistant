package io.github.eiim.hspsassistant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

// Singleton class containing categories.json config data
public class Categories {
	
	private static Categories CATEGORIES = null;

	public Collection<Lobby> lobbies;
	
	private Categories() {
		lobbies = new ArrayList<Lobby>();
	}
	
	public static synchronized Categories getCategories() {
		if(CATEGORIES == null) {
			CATEGORIES = new Categories();
		}
		return CATEGORIES;
	}
	
	public static void fromJSON(String jsonText) {
		Gson gson = new Gson();
		CATEGORIES = gson.fromJson(jsonText, Categories.class);
	}
	
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static class Lobby {
		public String name;
		public Collection<String> aliases;
		public int checkpoints;
		public Collection<Category> categories;
		Lobby(){}
	}
	
	public static class Category {
		public String name;
		public int[] checkpoints;
		public List<Variable> variables; // Needs to be ordered for checkpointOverrides
		public List<Override> checkpointOverrides; // Needs to be ordered for determinism
		Category(){}
	}
	
	public static class Variable {
		public VariableType type;
		public String[] options;
		Variable(){}
		
		public static enum VariableType {
			@SerializedName("speed") SPEED,
			@SerializedName("version") VERSION,
			@SerializedName("route") ROUTE,
			@SerializedName("custom") CUSTOM
		}
	}
	
	public static class Override {
		public String[] options;
		public int[] checkpoints;
		Override(){}
		
		public boolean matches(String[] options) {
			if(this.options.length != options.length) {
				return false;
			}
			for(int i = 0; i < options.length; i++) {
				if(this.options[i] != null && !(this.options[i].equals(options[i]))) {
					return false;
				}
			}
			return true;
		}
	}
	
}
