package main.trading;


public class LatestCandleSupport {
	
	private Candle candle;
	
	public LatestCandleSupport() {
		this.candle = new Candle();
	}
	
	public boolean isAboveSMA() {
		if (candle.getEurUsd100SMAValue() < candle.getLatestCandle().getClose()) {
			Logger.logLatestCandle(candle.getLatestCandle());
			return true;
		}
		else {
			Logger.logLatestCandle(candle.getLatestCandle());
			return false;
		}
	}
	public boolean isAscendingCandle() {
		if (candle.getLatestCandle().getClose() > candle.getLatestCandle().getOpen()) return true;
		else return false;
	}
	public boolean touchesSMA() {
		if ((candle.getLatestCandle().getHigh() >= candle.getEurUsd100SMAValue()) && (candle.getLatestCandle().getLow() <= candle.getEurUsd100SMAValue())) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean isVolumeHighEnough() {
		if (candle.getLatestCandle().getVolume() > candle.getSMAVolume()) {
			return true;
		}
		else {
			return false;
		}
	}
	public double getBuyStopLossPrice() {
		return Trading.round(candle.getLatestCandle().getLow() - 0.0001, 5);
	}
	public double getSellStopLossPrice() {
		return Trading.round(candle.getLatestCandle().getHigh() + 0.0001, 5);
	}
	public double getDistanceBetweenClosingPriceAndStopLossBuy() {
		return candle.getLatestCandle().getClose() - getBuyStopLossPrice(); 
	}
	public double getDistanceBetweenClosingPriceAndStopLossSell() {
		return getSellStopLossPrice() - candle.getLatestCandle().getClose(); 
	}
	public double getClosingPrice() {
		return candle.getLatestCandle().getClose();
	}
	public boolean isRaisingTrend() {
		return false;
	}
}
