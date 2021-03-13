package co.bilira.quote.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Order implements Comparable<Order> {
	BigDecimal price;
	BigDecimal volume;

	public Order(BigDecimal price, BigDecimal volume) {
		this.price = price;
		this.volume = volume;
	}

	@Override
	public int compareTo(Order other) {
		return this.price.compareTo((other).price);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (this.getClass() != other.getClass()) {
			return false;
		}

		Order otherOrder = (Order) other;
		return price.compareTo(otherOrder.price) == 0 &&
				volume.compareTo(otherOrder.volume) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(price, volume);
	}
}
