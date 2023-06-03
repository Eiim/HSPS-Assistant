package io.github.eiim.hspsassistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class GoldsFile {
	
	private File file;
	private Logger LOGGER = LogManager.getLogger();
	private List<Segment> golds;
	
	// If we have an exception here, just crash, because it's not recoverable
	public GoldsFile(File file) throws IOException {
		this.file = file;
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8, true);
			fw.write(CSVParser.stringsToLine("lobby","category","variables","checkpoint","time"));
			fw.close();
		}
		
		String csv = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		List<List<String>> data = CSVParser.parseFile(csv);
		Gson gson = new Gson();
		golds = new ArrayList<>();
		for(List<String> run : data) {
			if(run.size() < 5 || "lobby".equals(run.get(0))) {
				continue;
			}
			
			Segment rr = new Segment(run.get(0), run.get(1), gson.fromJson(run.get(2), String[].class),
					Integer.parseInt(run.get(3)), Integer.parseInt(run.get(4)));
			golds.add(rr);
		}
	}
	
	public boolean registerRun(Segment run) {
		for(Segment rr : golds) {
			if(run.lobby.equals(rr.lobby) && run.category.equals(rr.category) && Arrays.equals(run.variables, rr.variables) && run.checkpoint == rr.checkpoint) {
				if(run.time < rr.time) {
					setSaveGold(run);
					return true;
				} else {
					return false;
				}
			}
		}
		setSaveGold(run);
		return true;
	}
	
	private void setSaveGold(Segment run) {
		LOGGER.debug("Writing gold");
		int i = 0;
		boolean written = false;
		for(Segment rr : golds) {
			if(run.lobby.equals(rr.lobby) && run.category.equals(rr.category) && Arrays.equals(run.variables, rr.variables) && run.checkpoint == rr.checkpoint) {
				golds.set(i, run);
				written = true;
			}
			i++;
		}
		if(!written) {
			golds.add(run);
		}
		
		FileWriter fw;
		String csv = CSVParser.stringsToLine("lobby","category","variables","time","splits");
		Gson gson = new Gson();
		for(Segment rr : golds) {
			csv += CSVParser.stringsToLine(rr.lobby, rr.category, gson.toJson(rr.variables), ""+rr.checkpoint, ""+rr.time);
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
	
	public int getGold(String lobby, String category, String[] variables, int checkpoint) {
		for(Segment rr : golds) {
			if(lobby.equals(rr.lobby) && category.equals(rr.category) && Arrays.equals(variables, rr.variables) && checkpoint == rr.checkpoint) {
				return rr.time;
			}
		}
		return Integer.MAX_VALUE;
	}

}
