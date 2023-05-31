package io.github.eiim.hspsassistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class PBFile {
	
	private File file;
	private Logger LOGGER = LogManager.getLogger();
	private List<RunResult> pbs;
	
	// If we have an exception here, just crash, because it's not recoverable
	public PBFile(File file) throws IOException {
		this.file = file;
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8, true);
			fw.write(CSVParser.stringsToLine("lobby","category","variables","time","splits"));
			fw.close();
		}
		
		String csv = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		List<List<String>> data = CSVParser.parseFile(csv);
		Gson gson = new Gson();
		pbs = new ArrayList<>();
		for(List<String> run : data) {
			if(run.size() < 5 || "lobby".equals(run.get(0))) {
				continue;
			}
			
			RunResult rr = new RunResult(run.get(0), run.get(1), gson.fromJson(run.get(2), String[].class),
					Integer.parseInt(run.get(3)), gson.fromJson(run.get(4), Integer[].class));
			pbs.add(rr);
		}
	}
	
	public boolean registerRun(RunResult run) {
		for(RunResult rr : pbs) {
			if(run.lobby.equals(rr.lobby) && run.category.equals(rr.category) && run.variables.equals(rr.variables)) {
				if(run.time < rr.time) {
					setSavePB(run);
					return true;
				} else {
					return false;
				}
			}
		}
		setSavePB(run);
		return true;
	}
	
	private void setSavePB(RunResult run) {
		LOGGER.debug("Writing PBs");
		int i = 0;
		boolean written = false;
		for(RunResult rr : pbs) {
			if(run.lobby.equals(rr.lobby) && run.category.equals(rr.category) && run.variables.equals(rr.variables)) {
				pbs.set(i, run);
				written = true;
			}
			i++;
		}
		if(!written) {
			pbs.add(run);
		}
		
		FileWriter fw;
		String csv = CSVParser.stringsToLine("lobby","category","variables","time","splits");
		Gson gson = new Gson();
		for(RunResult rr : pbs) {
			csv += CSVParser.stringsToLine(rr.lobby, rr.category, gson.toJson(rr.variables), ""+rr.time, gson.toJson(rr.splitTimes));
		}
		try {
			fw = new FileWriter(file, StandardCharsets.UTF_8, false);
			fw.write(csv);
			fw.close();
		} catch (IOException e) {
			LOGGER.error("Error writing PBs data file");
			LOGGER.error("Dumping current PBs:");
			LOGGER.error(csv);
			e.printStackTrace();
		}
		
	}
	
	public int getPB(String lobby, String category, String[] variables) {
		for(RunResult rr : pbs) {
			if(lobby.equals(rr.lobby) && category.equals(rr.category) && variables.equals(rr.variables)) {
				return rr.time;
			}
		}
		return 0;
	}

}
