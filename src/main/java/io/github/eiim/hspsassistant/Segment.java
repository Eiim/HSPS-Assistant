package io.github.eiim.hspsassistant;

public class Segment {

	public String lobby;
	public String category;
	public String[] variables;
	public int checkpoint;
	public int time;
	
	public Segment(String lobby, String category, String[] variables, int checkpoint, int time) {
		this.lobby = lobby;
		this.category = category;
		this.variables = variables;
		this.checkpoint = checkpoint;
		this.time = time;
	}
}
