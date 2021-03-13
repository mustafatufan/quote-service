package co.bilira.quote.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Orderbook {

	private final String baseCurrency;
	private final String quoteCurrency;
	private Map<PriceType, List<Order>> prices = new EnumMap<>(PriceType.class);

	public Orderbook(String baseCurrency, String quoteCurrency, Map<PriceType, List<Order>> prices) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		for (PriceType priceType : PriceType.values()) {
			if (priceType.equals(PriceType.asks)) {
				Collections.sort(prices.get(priceType));
			} else {
				prices.get(priceType).sort(Collections.reverseOrder());
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
				prices.get(priceType).sort(Collections.reverseOrder());
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

	public static Orderbook reverseOrderbook(Orderbook orderbook) {
		Map<PriceType, List<Order>> reversePrices = new EnumMap<>(PriceType.class);
		reversePrices.put(PriceType.asks, getReverseOrders(orderbook.getPrices().get(PriceType.bids)));
		reversePrices.put(PriceType.bids, getReverseOrders(orderbook.getPrices().get(PriceType.asks)));
		for (PriceType priceType : PriceType.values()) {
			if (priceType.equals(PriceType.asks)) {
				Collections.sort(reversePrices.get(priceType));
			} else {
				reversePrices.get(priceType).sort(Collections.reverseOrder());
			}
		}
		return new Orderbook(orderbook.getQuoteCurrency(), orderbook.getBaseCurrency(), reversePrices);
	}

	private static List<Order> getReverseOrders(List<Order> orders) {
		List<Order> reverseOrders = new ArrayList<>();
		for (Order order : orders) {
			BigDecimal reversePrice = BigDecimal.ONE.divide(order.getPrice(), 16, RoundingMode.HALF_UP);
			BigDecimal reverseVolume = order.getPrice().multiply(order.getVolume());
			reverseOrders.add(new Order(reversePrice, reverseVolume));
		}
		return reverseOrders;
	}
}
