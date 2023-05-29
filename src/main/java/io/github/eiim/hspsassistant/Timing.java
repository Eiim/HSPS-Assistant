package io.github.eiim.hspsassistant;

import java.time.Instant;

public class Timing {
	
	public int[] checkpoints;
	private int lastCP = 0;
	private long startTime;
	public int[] segmentTimes;
	public int[] cumulativeTimes;
	public boolean active = true;

	public Timing(int[] cps) {
		startTime = Instant.now().toEpochMilli();
		checkpoints = cps;
		segmentTimes = new int[checkpoints.length+1];
		cumulativeTimes = new int[checkpoints.length+1];
	}
	
	public Timing(int[] cps, long startTime) {
		this.startTime = startTime;
		checkpoints = cps;
		segmentTimes = new int[checkpoints.length+1];
		cumulativeTimes = new int[checkpoints.length+1];
	}
	
	public long sinceStart() {
		return Instant.now().toEpochMilli() - startTime;
	}
	
	public String sinceStartString() {
		return millisToTimestring(sinceStart());
	}
	
	public void setDelta(int delta) {
		for(int i = 0; i <= checkpoints.length; i++) {
			if(i == checkpoints.length || checkpoints[i] == lastCP) {
				segmentTimes[i] = delta;
				return;
			}
		}
	}
	
	public void cpTotal(int cp, int total) {
		lastCP = cp;
		for(int i = 0; i <= checkpoints.length; i++) {
			if(i == checkpoints.length || checkpoints[i] == cp) {
				cumulativeTimes[i] = total;
				return;
			}
		}
	}
	
	public static String millisToTimestring(long millis) {
		long mil = millis % 1000;
		long sec = (millis % 60000) / 1000;
		long min = millis / 60000;
		return String.format("%02d:%02d.%03d", min, sec, mil);
	}
	
	/*
	 * String should be of the form mm:ss.xxx. This is not checked and may cause weird errors if it's not.
	 */
	public static int timestringToMillis(String ts) {
		int ms = 0;
		ms += Integer.parseInt(ts.substring(0, 2)) * 60000;
		ms += Integer.parseInt(ts.substring(3, 5)) * 1000;
		ms += Integer.parseInt(ts.substring(6));
		return ms;
	}

}
