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
import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {

	@Value("${url.orderbook.ftx}")
	private String orderbookUrl;

	@Override
	public QuoteResponseDto quote(QuoteRequestDto requestDto) throws IOException, NoMarketException {
		Orderbook orderbook = getOrderbook(requestDto.getBaseCurrency(), requestDto.getQuoteCurrency());
		return getQuoteResponse(requestDto, orderbook);
	}

	private Orderbook getOrderbook(String baseCurrency, String quoteCurrency) throws IOException, NoMarketException {
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
				String market = String.format("%s/%s or %s/%s", baseCurrency, quoteCurrency, quoteCurrency, baseCurrency);
				String message = String.format("There is no %s market.", market);
				throw new NoMarketException(message);
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
		return calculateQuote(requestDto, Orderbook.reverseOrderbook(orderbook));
	}
}
