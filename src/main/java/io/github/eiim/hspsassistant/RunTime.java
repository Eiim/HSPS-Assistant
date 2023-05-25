package io.github.eiim.hspsassistant;

public final class RunTime {
	
	private int ms;
	
	public RunTime(int ms) {
		this.ms = ms;
	}
	
	public RunTime(String time) {
		if(time.length() == 9) {
			// xx:yy.zzz
			ms = Integer.parseInt(time.substring(0, 2))*60*1000;
			ms += Integer.parseInt(time.substring(3, 5))*1000;
			ms += Integer.parseInt(time.substring(6, 9));
		} else {
			throw new NumberFormatException("Expected time in form of xx:yy.zzz, got "+time);
		}
	}
	
	public int getms() {
		return ms;
	}
	
	public String getTimeString() {
		return (ms/(1000*60))+":"+((ms/1000)%60)+"."+(ms%1000);
	}

	@Override
	public int hashCode() {
		return ms;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RunTime))
			return false;
		RunTime other = (RunTime) obj;
		return ms == other.ms;
	}

	@Override
	public String toString() {
		return getTimeString();
	}

}
