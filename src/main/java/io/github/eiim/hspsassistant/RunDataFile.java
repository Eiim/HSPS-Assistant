package io.github.eiim.hspsassistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class RunDataFile {

	private FileWriter fw;
	private Logger LOGGER = LogManager.getLogger();
	
	// If we have an exception here, just crash, because it's not recoverable
	public RunDataFile(File file) throws IOException {
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			fw = new FileWriter(file, StandardCharsets.UTF_8, true);
			fw.write(CSVParser.stringsToLine("lobby","category","variables","time","splits"));
			fw.flush();
		} else {
			fw = new FileWriter(file, StandardCharsets.UTF_8, true);
		}
	}
	
	public void appendRun(RunResult run) {
		if(HSPSConfig.saveAll.get()) {
			LOGGER.debug("Writing run");
			Gson gson = new Gson();
			String line = CSVParser.stringsToLine(run.lobby, run.category, gson.toJson(run.variables), ""+run.time, gson.toJson(run.splitTimes));
			try {
				fw.write(line);
				fw.flush();
			} catch (IOException e) {
				LOGGER.error("Can't write run data!");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void finalize() {
		try {
			fw.close();
		} catch (IOException e) {
			LOGGER.error("Can't close run file!");
			e.printStackTrace();
		}
	}

}
