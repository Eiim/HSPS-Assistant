package io.github.eiim.hspsassistant;

public class RunResult {

	public String lobby;
	public String category;
	public String[] variables;
	public int time;
	public Integer[] splitTimes;
	
	public RunResult(String lobby, String category, String[] variables, int time, Integer[] splitTimes) {
		this.lobby = lobby;
		this.category = category;
		this.variables = variables;
		this.time = time;
		this.splitTimes = splitTimes;
	}

}
