package main.trading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class Account {
	private int numOfLosses;
	private double currentLoss;
	private int lastTransactionId;
	
	public Account() {
		double loss = 0;
		int numOfLosses = 0;
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		Logger.logString("Last transaction id: "+lastTransactionId);
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/transactions/sinceid?id="+(lastTransactionId != 0 ? lastTransactionId-200 : 1000));
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
			JSONArray transactions = resultJson.getJSONArray("transactions");
			lastTransactionId = Integer.parseInt(resultJson.getString("lastTransactionID"));
			for (int i=transactions.length()-1; i>=0; i--) {
				JSONObject transaction = transactions.getJSONObject(i);
				//System.out.println(transaction);
				if ((transaction.has("reason")) && (transaction.getString("reason").contentEquals("MARKET_ORDER_TRADE_CLOSE"))) {
					JSONObject tradeClosed = transaction.getJSONArray("tradesClosed").getJSONObject(0);
					loss += Double.parseDouble(tradeClosed.getString("realizedPL"));
					loss += Double.parseDouble(tradeClosed.getString("financing"));
					numOfLosses++;
				}
				if ((transaction.has("reason")) && (transaction.getString("reason").contentEquals("STOP_LOSS_ORDER"))) {
					JSONObject tradeClosed = transaction.getJSONArray("tradesClosed").getJSONObject(0);
					loss += Double.parseDouble(tradeClosed.getString("realizedPL"));
					loss += Double.parseDouble(tradeClosed.getString("financing"));
					numOfLosses++;
				}
				if ((transaction.has("reason")) && (transaction.getString("reason").contentEquals("TAKE_PROFIT_ORDER"))) {
					break;
				}
			}
			setCurrentLoss(loss);
			setNumOfLosses(numOfLosses);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static double getBalance() {
		String temp;
		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpGet request = new HttpGet(RequestURI.baseURL+"/v3/accounts/"+RequestURI.accountId+"/summary");
		request.addHeader(RequestURI.headerTitle,RequestURI.accessToken);
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject accountDetails = new JSONObject(result.toString()).getJSONObject("account");
			temp = accountDetails.getString("balance");
			return Double.parseDouble(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int getNumOfLosses() {
		return numOfLosses;
	}

	public void setNumOfLosses(int numOfLosses) {
		this.numOfLosses = numOfLosses;
	}

	public double getCurrentLoss() {
		return currentLoss;
	}

	public void setCurrentLoss(double currentLoss) {
		this.currentLoss = currentLoss;
	}
	
}
