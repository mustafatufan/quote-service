package co.bilira.quote.service;

import co.bilira.quote.model.FilledQuote;
import co.bilira.quote.model.Order;
import co.bilira.quote.model.Orderbook;
import co.bilira.quote.model.PriceType;
import co.bilira.quote.model.QuoteAction;
import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import co.bilira.quote.util.HttpUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class QuoteServiceImpl implements QuoteService {

	@Value("${url.orderbook.ftx}")
	private String orderbookUrl;

	@Override
	public QuoteResponseDto quote(QuoteRequestDto requestDto) throws IOException {
		Orderbook orderbook = getOrderbook(requestDto.getBaseCurrency(), requestDto.getQuoteCurrency());
		return getQuoteResponse(requestDto, orderbook);
	}

	private Orderbook getOrderbook(String baseCurrency, String quoteCurrency) throws IOException {
		String actualBaseCurrency = baseCurrency;
		String actualQuoteCurrency = quoteCurrency;
		String url = getUrl(baseCurrency, quoteCurrency);
		JSONObject result = HttpUtil.readJsonFromUrl(url);
		if (!result.getBoolean("success")) {
			String reverseUrl = getUrl(quoteCurrency, baseCurrency);
			result = HttpUtil.readJsonFromUrl(reverseUrl);
			actualBaseCurrency = quoteCurrency;
			actualQuoteCurrency = baseCurrency;
			if (!result.getBoolean("success")) {
				// TODO: Throw no market exception
			}
		}
		JSONObject jsonObject = result.getJSONObject("result");
		return new Orderbook(actualBaseCurrency, actualQuoteCurrency, jsonObject);
	}

	private String getUrl(String first, String second) {
		String market = first.concat("/").concat(second);
		return String.format(orderbookUrl, market);
	}

	private QuoteResponseDto getQuoteResponse(QuoteRequestDto requestDto, Orderbook orderbook) {
		if (requestDto.getBaseCurrency().equals(orderbook.getBaseCurrency()) &&
				requestDto.getQuoteCurrency().equals(orderbook.getQuoteCurrency())) {
			return calculateQuote(requestDto, orderbook);
		} else if (requestDto.getBaseCurrency().equals(orderbook.getQuoteCurrency()) &&
				requestDto.getQuoteCurrency().equals(orderbook.getBaseCurrency())) {
			return calculateReverseQuote(requestDto, orderbook);
		} else {
			return null;
		}
	}

	private QuoteResponseDto calculateQuote(QuoteRequestDto requestDto, Orderbook orderbook) {
		List<Order> orders;
		if (requestDto.getAction().equals(QuoteAction.buy)) {
			orders = orderbook.getPrices().get(PriceType.asks);
		} else {
			orders = orderbook.getPrices().get(PriceType.bids);
		}
		FilledQuote filledQuote = new FilledQuote();
		for (Order order : orders) {
			BigDecimal projectedFilling = filledQuote.getVolume().add(order.getVolume());
			if (projectedFilling.compareTo(requestDto.getAmount()) <= 0) {
				filledQuote.addOrder(order);
			} else {
				BigDecimal leftoverVolume = order.getVolume().subtract(projectedFilling.subtract(requestDto.getAmount()));
				filledQuote.addOrder(new Order(order.getPrice(), leftoverVolume));
				break;
			}
		}
		return new QuoteResponseDto(filledQuote.getTotal(), filledQuote.getPrice(), requestDto.getQuoteCurrency());
	}

	private QuoteResponseDto calculateReverseQuote(QuoteRequestDto requestDto, Orderbook orderbook) {
		Map<PriceType, List<Order>> reversePrices = new EnumMap<>(PriceType.class);
		reversePrices.put(PriceType.asks, getReverseOrders(orderbook.getPrices().get(PriceType.bids)));
		reversePrices.put(PriceType.bids, getReverseOrders(orderbook.getPrices().get(PriceType.asks)));
		Orderbook reverseOrderbook = new Orderbook(orderbook.getQuoteCurrency(), orderbook.getBaseCurrency(), reversePrices);
		return calculateQuote(requestDto, reverseOrderbook);
	}

	private List<Order> getReverseOrders(List<Order> orders) {
		List<Order> reverseOrders = new ArrayList<>();
		for (Order order : orders) {
			BigDecimal reversePrice = BigDecimal.ONE.divide(order.getPrice(), 8, RoundingMode.HALF_UP);
			BigDecimal reverseVolume = reversePrice.multiply(order.getVolume());
			reverseOrders.add(new Order(reversePrice, reverseVolume));
		}
		return reverseOrders;
	}
}
