package main.trading;

public class Trade {
	private long id;
	private String buyOrSell;
	private double openingPrice;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBuyOrSell() {
		return buyOrSell;
	}
	public void setBuyOrSell(String buyOrSell) {
		this.buyOrSell = buyOrSell;
	}
	public double getOpeningPrice() {
		return openingPrice;
	}
	public void setOpeningPrice(double openingPrice) {
		this.openingPrice = openingPrice;
	}
	@Override
	public String toString() {
		return "Trade [id=" + id + ", buyOrSell=" + buyOrSell + ", openingPrice=" + openingPrice + "]";
	}
	
	
	
}
