package main.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Trading {
	
	private Trade getTrade() {
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/openTrades");
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
			System.out.println(resultJson);
			try {
				 JSONObject trade = resultJson.getJSONArray("trades").getJSONObject(0);
				 //System.out.println(trade);
				 Trade t = new Trade();
				 t.setId(trade.getLong("id"));
				 t.setOpeningPrice(trade.getDouble("price"));
				 t.setBuyOrSell(trade.getString("currentUnits").contains("-") ? "sell" : "buy");
				 return t;
			}
			catch (JSONException je) {
				return null;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	private int closeExistingTrade(String buyOrSell) {
		Trade t = getTrade();
		long existingTradeId;
		if (t != null) {
			if (t.getBuyOrSell().contentEquals(buyOrSell)) return 1;
			existingTradeId = t.getId();
		}
		else {
			//No trade is open
			return 0;
		}
		
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpPut request = new HttpPut(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/trades/"+existingTradeId+"/close");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		System.out.println(request.getURI());
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject resultJson = new JSONObject(result.toString());
			System.out.println(resultJson);
			return response.getStatusLine().getStatusCode();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		
	}
	//0: there is an open trade already
	//1: An existing trade was closed
	public int buyOrSellAtMarketRate(int unit, double stopLossPrice, String buyOrSell) {
		//statuses: 0: No trade is open 1:There is a trade open already 200: A trade was closed
		int closeTradeStatus = closeExistingTrade(buyOrSell);
		if (closeTradeStatus == 1) return 0;
		if (closeTradeStatus == 200) {
			//Adjust take profit to reflect the loss/profit from the closed trade
			//1 means resend the order
			return 1;
		}
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpPost request = new HttpPost(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/orders");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		request.addHeader("Content-Type", "application/json");
		JSONObject requestOrderObject = new JSONObject();
		JSONObject requestStopLossObject = new JSONObject();
		JSONObject requestObject = new JSONObject();
		requestStopLossObject.put("price", String.valueOf(stopLossPrice));
		requestStopLossObject.put("timeInForce", "GTC");
		requestOrderObject.put("instrument", "EUR_USD");
		requestOrderObject.put("units", (buyOrSell.equals("buy") ? "" : "-")+String.valueOf(unit));
		requestOrderObject.put("timeInForce", "FOK");
		requestOrderObject.put("type", "MARKET");
		requestOrderObject.put("positionFill", "DEFAULT");
		requestOrderObject.put("stopLossOnFill", requestStopLossObject);
		requestObject.put("order", requestOrderObject);
		System.out.println(requestObject.toString());
		try {
			HttpEntity entity = new StringEntity(requestObject.toString());
			request.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject resultJson = new JSONObject(result.toString());
			System.out.println(resultJson.toString());
			Logger.logString(resultJson.toString());
			return response.getStatusLine().getStatusCode();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return 0;
	}
	public double getUsdGbp() {
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v1/prices?instruments=GBP_USD");
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
			JSONArray priceDetails = resultJson.getJSONArray("prices");
			resultJson = priceDetails.getJSONObject(0);
			double midPrice = (resultJson.getDouble("ask") + resultJson.getDouble("bid"))/2;
			
			return 1/midPrice;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public int setTakeProfitPrice(double lossAmmount, int numOfUnits, boolean buy) {
		Trade t = getTrade();
		double takeProfitPrice = t.getOpeningPrice();
		double usdGbp = getUsdGbp();
		double profitNeeded;
		double distanceBetweenOpenPriceAndTakeProfit = 0;
		if (lossAmmount < 0) {
			//get the absolute value of the loss amount
			profitNeeded = Math.abs(lossAmmount);
			//add 5%
			profitNeeded += profitNeeded * 0.05;
			//add �1
			profitNeeded += 1.0;
		}
		else {
			profitNeeded = 1-lossAmmount;
		}
		
		for (double i=0.00001; i<Double.MAX_VALUE; i += 0.00001) {
			if (i*usdGbp*numOfUnits >= profitNeeded) {
				distanceBetweenOpenPriceAndTakeProfit = i;
				break;
			}
		}
		if (buy) takeProfitPrice += distanceBetweenOpenPriceAndTakeProfit;
		else takeProfitPrice -= distanceBetweenOpenPriceAndTakeProfit;
		takeProfitPrice = round(takeProfitPrice, 5);
		
		//Modifying Trade
		
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpPut request = new HttpPut(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/trades/"+t.getId()+"/orders");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		request.addHeader("Content-Type", "application/json");
		JSONObject takeProfitObject = new JSONObject();
		JSONObject takeProfitDetailsObject = new JSONObject();
		takeProfitDetailsObject.put("price", String.valueOf(takeProfitPrice));
		takeProfitDetailsObject.put("timeInForce", "GTC");
		takeProfitObject.put("takeProfit", takeProfitDetailsObject);
	    System.out.println("Take Profit Price: "+takeProfitPrice);
		try {
			HttpEntity entity = new StringEntity(takeProfitObject.toString());
			request.setEntity(entity);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject resultJson = new JSONObject(result.toString());
			System.out.println(resultJson);
			return response.getStatusLine().getStatusCode();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	public int calculateNumberOfUnits(double distanceBetweenOpenAndClose, int riskAmmount) {
		double usdGbp = getUsdGbp();
		int maxUnits = new Double(Account.getBalance() *100).intValue();
		riskAmmount++;
		for (int i=1; i<maxUnits; i++) {
			if (distanceBetweenOpenAndClose*usdGbp*i >= riskAmmount - riskAmmount * 0.15) {
				return i;
			}
		}
		//if we need more unit than we have return the maximum units we can get
		return maxUnits;
	}
	
}
