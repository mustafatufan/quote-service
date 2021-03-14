package co.bilira.quote.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FilledQuote {
	private List<Order> orders = new ArrayList<>();

	public void addOrder(Order order) {
		orders.add(order);
	}

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (Order order : orders) {
			total = total.add(order.getPrice().multiply(order.getVolume()));
		}
		return total.stripTrailingZeros();
	}

	public BigDecimal getPrice() {
		return getTotal().divide(getVolume(), 18, RoundingMode.HALF_UP).stripTrailingZeros();
	}

	public BigDecimal getVolume() {
		BigDecimal volume = BigDecimal.ZERO;
		for (Order order : orders) {
			volume = volume.add(order.getVolume());
		}
		return volume.stripTrailingZeros();
	}
}
