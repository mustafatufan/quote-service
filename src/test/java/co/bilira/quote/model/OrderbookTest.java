package co.bilira.quote.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderbookTest {

	@Test
	void testReverseOrderbook() {
		String originalBase = "BTC";
		String originalQuote = "TRYB";

		Map<PriceType, List<Order>> originalPrices = new HashMap<>();
		originalPrices.put(PriceType.asks, generateOrders());
		originalPrices.put(PriceType.bids, generateOrders());

		Orderbook orderbook = new Orderbook(originalBase, originalQuote, originalPrices);
		Orderbook reversedOrderbook = Orderbook.reverseOrderbook(orderbook);

		// Just sanity check.
		assertEquals(originalQuote, reversedOrderbook.getBaseCurrency());
		assertEquals(originalBase, reversedOrderbook.getQuoteCurrency());

		// Checks if asks and bids are swapped.
		assertEquals(originalPrices.get(PriceType.asks).size(), reversedOrderbook.getPrices().get(PriceType.bids).size());
		assertEquals(originalPrices.get(PriceType.bids).size(), reversedOrderbook.getPrices().get(PriceType.asks).size());

		// Checks if first ask order becomes first bid order and it is correct.
		Order expectedAskOrder = originalPrices.get(PriceType.asks).get(0);
		Order actualBidOrder = reversedOrderbook.getPrices().get(PriceType.bids).get(0);
		assertEquals(BigDecimal.ONE.divide(expectedAskOrder.getPrice(), 18, RoundingMode.HALF_UP), actualBidOrder.getPrice());
		assertEquals(expectedAskOrder.getPrice().multiply(expectedAskOrder.getVolume()), actualBidOrder.getVolume());

		// Checks if first bid order becomes first ask order and it is correct.
		Order expectedBidOrder = originalPrices.get(PriceType.bids).get(0);
		Order actualAskOrder = reversedOrderbook.getPrices().get(PriceType.asks).get(0);
		assertEquals(BigDecimal.ONE.divide(expectedBidOrder.getPrice(), 18, RoundingMode.HALF_UP), actualAskOrder.getPrice());
		assertEquals(expectedBidOrder.getPrice().multiply(expectedBidOrder.getVolume()), actualAskOrder.getVolume());
	}

	private List<Order> generateOrders() {
		List<Order> orders = new ArrayList<>();
		for (int i = 0; i < (Math.random() * 9) + 1; i++) {
			orders.add(new Order(BigDecimal.valueOf(Math.random() * 10000.0), BigDecimal.valueOf(Math.random() * 10000.0)));
		}
		return orders;
	}
}
