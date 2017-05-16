package main.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;


public class Candle {
	private double SMAVolume;
	private LatestClosingCandle latestCandle;
	
	//This method also sets 100 SMA for volume and latest candle info
	public double getEurUsd100SMAValue() {
		double aggregate = 0;
		double aggregateVolume = 0;
		latestCandle = new LatestClosingCandle();
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v1/candles?instrument=EUR_USD&count=100&granularity=M5&candleFormat=midpoint");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject resultJson = new JSONObject(result.toString());
			JSONArray candles = resultJson.getJSONArray("candles");
			for (int i=0; i<candles.length(); i++) {
				JSONObject candle = candles.getJSONObject(i);
				int sec = Calendar.getInstance().get(Calendar.SECOND);
				if (sec == 0) {
					Thread.sleep(1500);
					sec = Calendar.getInstance().get(Calendar.SECOND);
				}
				if ((sec > 0) && (sec < 5)) {
					if (i == candles.length()-2) {
						latestCandle.setClose(Double.parseDouble(candle.get("closeMid").toString()));
						latestCandle.setHigh(Double.parseDouble(candle.get("highMid").toString()));
						latestCandle.setLow(Double.parseDouble(candle.get("lowMid").toString()));
						latestCandle.setVolume(Integer.parseInt(candle.get("volume").toString()));
						latestCandle.setOpen(candle.getDouble("openMid"));
						latestCandle.setTime(stringToDate(candle.getString("time")));
					}
				}
				else {
					if (!candle.getBoolean("complete")) {
						latestCandle.setClose(Double.parseDouble(candle.get("closeMid").toString()));
						latestCandle.setHigh(Double.parseDouble(candle.get("highMid").toString()));
						latestCandle.setLow(Double.parseDouble(candle.get("lowMid").toString()));
						latestCandle.setVolume(Integer.parseInt(candle.get("volume").toString()));
						latestCandle.setOpen(candle.getDouble("openMid"));
						latestCandle.setTime(stringToDate(candle.getString("time")));
					}
				}
				
				double closePrice = Double.parseDouble(candle.get("closeMid").toString());
				double closeVolume = Double.parseDouble(candle.get("volume").toString());
				aggregateVolume += closeVolume;
				aggregate += closePrice;
			}
			setLatestCandle(latestCandle);
			setSMAVolume(aggregateVolume/100);
			return aggregate/100;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public boolean isRaisingTrend() {
		double aggregate = 0;
		double SMA100;
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v1/candles?instrument=EUR_USD&count=100&granularity=M30&candleFormat=midpoint");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject resultJson = new JSONObject(result.toString());
			JSONArray candles = resultJson.getJSONArray("candles");
			for (int i=0; i<candles.length(); i++) {
				double closePrice = Double.parseDouble(candles.getJSONObject(i).get("closeMid").toString());
				aggregate += closePrice;
			}
			SMA100 = Trading.round(aggregate/100, 5);
			if (getLatestCandle().getClose() > SMA100) return true;
			else return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public double getSMAVolume() {
		return SMAVolume;
	}

	public void setSMAVolume(double sMAVolume) {
		SMAVolume = sMAVolume;
	}

	public LatestClosingCandle getLatestCandle() {
		return latestCandle;
	}

	public void setLatestCandle(LatestClosingCandle latestCandle) {
		this.latestCandle = latestCandle;
	}
	private Date stringToDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Replacing T with ' '
		date = date.replace("T", " ");
		//Removing 0s after .
		date = date.substring(0, date.indexOf('.'));
		try {
			Date returnDate = sdf.parse(date);
			return returnDate;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
}
