package io.github.eiim.hspsassistant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

// Singleton class containing categories.json config data
public class Categories {
	
	private static Categories CATEGORIES = null;

	public List<Lobby> lobbies;
	
	private Categories() {
		lobbies = new ArrayList<Lobby>();
	}
	
	public static synchronized Categories getCategories() {
		if(CATEGORIES == null) {
			CATEGORIES = new Categories();
		}
		return CATEGORIES;
	}
	
	public static Categories fromJSON(String jsonText) {
		Gson gson = new Gson();
		CATEGORIES = gson.fromJson(jsonText, Categories.class);
		return CATEGORIES;
	}
	
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static class Lobby {
		public String name;
		public List<String> aliases;
		public int checkpoints;
		public List<Category> categories;
		Lobby(){}
		
		@java.lang.Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Lobby))
				return false;
			Lobby other = (Lobby) obj;
			return Objects.equals(aliases, other.aliases) && Objects.equals(categories, other.categories)
					&& checkpoints == other.checkpoints && Objects.equals(name, other.name);
		}
	}
	
	public static class Category {
		public String name;
		public int[] checkpoints;
		public List<Variable> variables; // Needs to be ordered for checkpointOverrides
		public List<Override> checkpointOverrides; // Needs to be ordered for determinism
		Category(){}
		
		@java.lang.Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Category))
				return false;
			Category other = (Category) obj;
			return Objects.equals(checkpointOverrides, other.checkpointOverrides)
					&& Arrays.equals(checkpoints, other.checkpoints) && Objects.equals(name, other.name)
					&& Objects.equals(variables, other.variables);
		}
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
		
		@java.lang.Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Variable))
				return false;
			Variable other = (Variable) obj;
			return Arrays.equals(options, other.options) && type == other.type;
		}
	}
	
	public static class Override {
		public String[] options;
		public int[] checkpoints;
		Override(){}
		
		// Check if options match
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
		
		@java.lang.Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Override))
				return false;
			Override other = (Override) obj;
			return Arrays.equals(checkpoints, other.checkpoints) && Arrays.equals(options, other.options);
		}
	}
	
	@java.lang.Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Categories))
			return false;
		Categories other = (Categories) obj;
		return Objects.equals(lobbies, other.lobbies);
	}
	
}
