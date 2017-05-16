package main.trading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;


public class Logger {
	static File log;
	static BufferedWriter bw;
	
	public static void init() {
		// /home/toni8810/Desktop/tradingRobotlog.out
		// /home/gigbud5/public_html/tradingRobotlog.out
		log = new File("/home/gigbud5/public_html/tradingRobotlog.out");
		try {
			log.createNewFile();
			System.out.println("Log file created at "+log.getAbsolutePath());
			bw = new BufferedWriter(new FileWriter(log,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void logLatestCandle(LatestClosingCandle lcc) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
			try {
				bw.newLine();
				bw.write(c.getTime().toString());
				bw.newLine();
				bw.write(lcc.toString());
				bw.newLine();
				bw.write("-------------------------------------------------------------");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public static void logString(String s) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
		try {
			bw.newLine();
			bw.write(c.getTime().toString());
			bw.newLine();
			bw.write(s);
			bw.newLine();
			bw.write("-------------------------------------------------------------");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void closeConnection() {
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
