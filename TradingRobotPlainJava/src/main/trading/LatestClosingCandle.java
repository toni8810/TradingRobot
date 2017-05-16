package main.trading;

import java.util.Date;

public class LatestClosingCandle {
	private int volume;
	private double close;
	private double high;
	private double low;
	private double open;
	private Date time;
	
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "LatestClosingCandle [volume=" + volume + ", close=" + close + ", high=" + high + ", low=" + low
				+ ", open=" + open + ", time=" + time + "]";
	}
	
	
	
}
