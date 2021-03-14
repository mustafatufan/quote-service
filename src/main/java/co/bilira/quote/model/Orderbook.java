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
		this.prices = prices;
		this.sortOrderbook();
	}

	public Orderbook(String baseCurrency, String quoteCurrency, JSONObject jsonObject) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		this.prices = getPricesFromJsonObject(jsonObject);
		this.sortOrderbook();
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

	private Map<PriceType, List<Order>> getPricesFromJsonObject(JSONObject jsonObject) {
		Map<PriceType, List<Order>> priceList = new EnumMap<>(PriceType.class);
		for (PriceType priceType : PriceType.values()) {
			priceList.put(priceType, new ArrayList<>());
			JSONArray array = jsonObject.getJSONArray(priceType.name());
			for (int i = 0; i < array.length(); i++) {
				JSONArray rawOrder = array.getJSONArray(i);
				Order order = new Order(rawOrder.getBigDecimal(0), rawOrder.getBigDecimal(1));
				priceList.get(priceType).add(order);
			}
		}
		return priceList;
	}

	private void sortOrderbook() {
		for (PriceType priceType : PriceType.values()) {
			if (priceType.equals(PriceType.asks)) {
				Collections.sort(prices.get(priceType));
			} else {
				prices.get(priceType).sort(Collections.reverseOrder());
			}
		}
	}

	public static Orderbook reverseOrderbook(Orderbook orderbook) {
		Map<PriceType, List<Order>> reversePrices = new EnumMap<>(PriceType.class);
		reversePrices.put(PriceType.asks, getReverseOrders(orderbook.getPrices().get(PriceType.bids)));
		reversePrices.put(PriceType.bids, getReverseOrders(orderbook.getPrices().get(PriceType.asks)));
		return new Orderbook(orderbook.getQuoteCurrency(), orderbook.getBaseCurrency(), reversePrices);
	}

	private static List<Order> getReverseOrders(List<Order> orders) {
		List<Order> reverseOrders = new ArrayList<>();
		for (Order order : orders) {
			reverseOrders.add(reverseOrder(order));
		}
		return reverseOrders;
	}

	private static Order reverseOrder(Order order) {
		BigDecimal reversePrice = BigDecimal.ONE.divide(order.getPrice(), 18, RoundingMode.HALF_UP);
		BigDecimal reverseVolume = order.getPrice().multiply(order.getVolume());
		return new Order(reversePrice, reverseVolume);
	}
}
