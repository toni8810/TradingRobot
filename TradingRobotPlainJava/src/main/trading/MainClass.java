package main.trading;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainClass {
	public static void main(String[] args) {
		
		try {
			mainWithThrows();
		} catch (Exception e) {
			Logger.logString(e.getMessage());
			Logger.logString(e.getLocalizedMessage());
			Logger.logString(e.getStackTrace().toString());
		}
		
	}
	private static boolean isGoodTime() {
		return fiveMinute();
		
	}
	private static boolean oneMinute() {
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.SECOND) >= 55) {
			return true;
		}
		else {
			return false;
		}
			
	}
	private static boolean twoMinute() {
		Calendar c = Calendar.getInstance();
		List<String> nums = new ArrayList<String>();
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		nums.add("9");
		nums.add("1");
		nums.add("3");
		nums.add("5");
		nums.add("7");
		if (nums.contains(minute.substring(minute.length()-2))) {
			
			if (c.get(Calendar.SECOND) >= 55) {
				return true;
			}
			else {
				return false;
			}
			
		}
		else {
			return false;
		} 
	}
	private static boolean threeMinute() {
		Calendar c = Calendar.getInstance();
		List<String> nums = new ArrayList<String>();
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		for (int i=1; i<=60; i++) {
			if (i%3 == 0) {
				nums.add(String.valueOf(i));
			}
		}
		if (nums.contains(minute)) {
			
			if (c.get(Calendar.SECOND) >= 55) {
				return true;
			}
			else {
				return false;
			}
			
		}
		else {
			return false;
		}
	}
	private static boolean fourMinute() {
		Calendar c = Calendar.getInstance();
		List<String> nums = new ArrayList<String>();
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		for (int i=1; i<=60; i++) {
			if (i%4 == 0) {
				nums.add(String.valueOf(i-1));
			}
		}
		if (nums.contains(minute)) {
			
			if (c.get(Calendar.SECOND) >= 55) {
				return true;
			}
			else {
				return false;
			}
			
		}
		else {
			return false;
		}
	}
	private static boolean fiveMinute() {
		Calendar c = Calendar.getInstance();
		if ((String.valueOf(c.get(Calendar.MINUTE)).endsWith("4")) || (String.valueOf(c.get(Calendar.MINUTE)).endsWith("9"))) {
			
			if (c.get(Calendar.SECOND) >= 55) {
				return true;
			}
			else {
				return false;
			}
			
		}
		else {
			return false;
		} 
	}
	private static void mainWithThrows() throws Exception {
		Account a;
		LatestCandleSupport lcs = new LatestCandleSupport();
		Trading t = new Trading();
		for (; ;) {
			if (isGoodTime()) {
				Logger.init();
				if (lcs.isAboveSMA()) {
					Logger.logString("Above SMA");
					System.out.println("Above SMA");
					if (lcs.isAscendingCandle()) {
						System.out.println("Ascending Candle");
						if (lcs.touchesSMA()) {
							Logger.logString("Touches SMA");
							System.out.println("Touches SMA");
							//if (lcs.isVolumeHighEnough()) {
								//Logger.logString("Volume High Enough");
								//System.out.println("Volume High Enough");
								if (lcs.isRaisingTrend()) {
									Logger.logString("Raising Trend");
									System.out.println("Raising Trend");
									a = new Account();
									double distanceBetweenOpeningAndClosingPrice = lcs.getDistanceBetweenClosingPriceAndStopLossBuy();
									int numOfUnits = t.calculateNumberOfUnits(distanceBetweenOpeningAndClosingPrice, a.getNumOfLosses());
									Logger.logString("Number of units: "+numOfUnits);
									double stopLossPrice = lcs.getBuyStopLossPrice();
									int tradeStatus = t.buyOrSellAtMarketRate(numOfUnits, stopLossPrice, "buy");
									if (tradeStatus == 1) {
										a = new Account();
										numOfUnits = t.calculateNumberOfUnits(distanceBetweenOpeningAndClosingPrice, a.getNumOfLosses());
										tradeStatus = t.buyOrSellAtMarketRate(numOfUnits, stopLossPrice, "buy");
									}
									Logger.logString("Trade Status Code: "+tradeStatus);
									System.out.println("Trade Status Code: "+tradeStatus);
									Logger.logString("Number of losses: "+a.getNumOfLosses());
									System.out.println("Number of losses: "+a.getNumOfLosses());
									Logger.logString("Current loss: "+a.getCurrentLoss());
									System.out.println("Current loss: "+a.getCurrentLoss());
									if (tradeStatus == 201) t.setTakeProfitPrice(a.getCurrentLoss(), numOfUnits,true);
								}
							//}
						}
					}
				}
				else {
					Logger.logString("Under SMA");
					System.out.println("Under SMA");
					if (!lcs.isAscendingCandle()) {
						System.out.println("Descending Candle");
						if (lcs.touchesSMA()) {
							Logger.logString("Touches SMA");
							System.out.println("Touches SMA");
							//if (lcs.isVolumeHighEnough()) {
								//Logger.logString("Volume High Enough");
								//System.out.println("Volume High Enough");
								if (!lcs.isRaisingTrend()) {
									Logger.logString("Falling Trend");
									System.out.println("Falling Trend");
									a = new Account();
									double distanceBetweenOpeningAndClosingPrice = lcs.getDistanceBetweenClosingPriceAndStopLossSell();
									int numOfUnits = t.calculateNumberOfUnits(distanceBetweenOpeningAndClosingPrice, a.getNumOfLosses());
									double stopLossPrice = lcs.getSellStopLossPrice();
									int tradeStatus = t.buyOrSellAtMarketRate(numOfUnits, stopLossPrice, "sell");
									if (tradeStatus == 1) {
										a = new Account();
										numOfUnits = t.calculateNumberOfUnits(distanceBetweenOpeningAndClosingPrice, a.getNumOfLosses());
										tradeStatus = t.buyOrSellAtMarketRate(numOfUnits, stopLossPrice, "sell");
									}
									Logger.logString("Trade Status: "+tradeStatus);
									System.out.println("Trade Status: "+tradeStatus);
									Logger.logString("Number of losses: "+a.getNumOfLosses());
									System.out.println("Number of losses: "+a.getNumOfLosses());
									Logger.logString("Current loss: "+a.getCurrentLoss());
									System.out.println("Current loss: "+a.getCurrentLoss());
									if (tradeStatus == 201) t.setTakeProfitPrice(a.getCurrentLoss(), numOfUnits,false);
								}
							//}
						}
					}
				}
				Logger.closeConnection();
				Thread.sleep(3*60*1000);
			}
			Thread.sleep(1000);
		}
	}

}
