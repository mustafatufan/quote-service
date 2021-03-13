package co.bilira.quote.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Orderbook {

	private String baseCurrency;
	private String quoteCurrency;
	private Map<PriceType, List<Order>> prices = new EnumMap<>(PriceType.class);

	public Orderbook(String baseCurrency, String quoteCurrency, Map<PriceType, List<Order>> prices) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		for (PriceType priceType : PriceType.values()) {
			if (priceType.equals(PriceType.asks)) {
				Collections.sort(prices.get(priceType));
			} else {
				Collections.sort(prices.get(priceType), Collections.reverseOrder());
			}
		}
		this.prices = prices;
	}

	public Orderbook(String baseCurrency, String quoteCurrency, JSONObject jsonObject) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		for (PriceType priceType : PriceType.values()) {
			prices.put(priceType, new ArrayList<>());
			JSONArray array = jsonObject.getJSONArray(priceType.name());
			for (int i = 0, size = array.length(); i < size; i++) {
				JSONArray rawOrder = array.getJSONArray(i);
				Order order = new Order(rawOrder.getBigDecimal(0), rawOrder.getBigDecimal(1));
				prices.get(priceType).add(order);
			}
			if (priceType.equals(PriceType.asks)) {
				Collections.sort(prices.get(priceType));
			} else {
				Collections.sort(prices.get(priceType), Collections.reverseOrder());
			}
		}
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	public Map<PriceType, List<Order>> getPrices() {
		return prices;
	}
}
