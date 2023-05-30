package io.github.eiim.hspsassistant;

import java.util.ArrayList;
import java.util.List;

/*
 * RFC-4180 compliant* CSV handling
 * *does not enforce consistent number of fields on each line
 */
public class CSVParser {
	
	public static String stringsToLine(String... strings) {
		String line = "";
		for(int i = 0; i < strings.length; i++) {
			line += escapeString(strings[i]);
			if(i == strings.length-1) {
				line += "\r\n";
			} else {
				line += ',';
			}
		}
		return line;
	}
	
	public static String escapeString(String s) {
		if(s.indexOf(',') > -1 || s.indexOf('"') > -1 || s.contains("\r\n")) {
			s = s.replace("\"", "\"\"");
			s = '"' + s + '"';
			return s;
		} else {
			return s;
		}
	}
	
	public static List<List<String>> parseFile(String csv) {
		List<List<String>> lines = new ArrayList<>();
		List<String> currentLine = new ArrayList<>();
		String currentField = "";
		boolean inEscapedField = false;
		for(int i = 0; i < csv.length(); i++) {
			char c = csv.charAt(i);
			if(c == ',' && !inEscapedField) {
				currentLine.add(currentField);
				currentField = "";
			} else if(c == '\r' && !inEscapedField && i+1 < csv.length() && csv.charAt(i+1) == '\n') {
				currentLine.add(currentField);
				lines.add(currentLine);
				currentField = "";
				currentLine = new ArrayList<>();
				i++; // skip over whole newline
			} else if(c == '"') {
				if(!inEscapedField) {
					inEscapedField = true;
				} else {
					if(i+1 < csv.length() && csv.charAt(i+1) == '"') {
						currentField += '"';
						i++;
					} else {
						inEscapedField = false;
					}
				}
			} else {
				currentField += c;
			}
		}
		currentLine.add(currentField);
		lines.add(currentLine);
		return lines;
	}

}
