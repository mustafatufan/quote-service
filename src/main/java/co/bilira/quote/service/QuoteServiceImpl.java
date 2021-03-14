package co.bilira.quote.service;

import co.bilira.quote.model.FilledQuote;
import co.bilira.quote.model.Order;
import co.bilira.quote.model.Orderbook;
import co.bilira.quote.model.PriceType;
import co.bilira.quote.model.QuoteAction;
import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {

	private final ApiService apiService;

	public QuoteServiceImpl(ApiService apiService) {
		this.apiService = apiService;
	}

	@Value("${url.orderbook.ftx}")
	private String orderbookUrl = "https://ftx.com/api/markets/%s/orderbook?depth=100";

	@Override
	public QuoteResponseDto quote(QuoteRequestDto requestDto) throws NoMarketException, InvalidAmountException, ConnectionUnavailableException {
		if (requestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String errorMessage = String.format("Amount %s is invalid. It should be bigger than zero.", requestDto.getAmount().toString());
			throw new InvalidAmountException(errorMessage);
		}

		Orderbook orderbook = getOrderbook(requestDto.getBaseCurrency(), requestDto.getQuoteCurrency());
		return getQuoteResponse(requestDto, orderbook);
	}

	private Orderbook getOrderbook(String baseCurrency, String quoteCurrency) throws NoMarketException, ConnectionUnavailableException {
		String actualBaseCurrency = baseCurrency;
		String actualQuoteCurrency = quoteCurrency;
		String url = getUrl(baseCurrency, quoteCurrency);
		JSONObject result = apiService.readJsonFromUrl(url);
		if (!result.getBoolean("success")) {
			String reverseUrl = getUrl(quoteCurrency, baseCurrency);
			result = apiService.readJsonFromUrl(reverseUrl);
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
		} else {
			return calculateReverseQuote(requestDto, orderbook);
		}
	}

	private QuoteResponseDto calculateQuote(QuoteRequestDto requestDto, Orderbook orderbook) {
		List<Order> orders = selectAskOrBidPrices(requestDto, orderbook);
		FilledQuote filledQuote = new FilledQuote();
		for (Order order : orders) {
			BigDecimal projectedFilling = filledQuote.getVolume().add(order.getVolume());
			int compareResult = projectedFilling.compareTo(requestDto.getAmount());
			if (compareResult < 0) {
				filledQuote.addOrder(order);
			} else {
				if (compareResult == 0) {
					filledQuote.addOrder(order);
				} else {
					BigDecimal leftoverVolume = order.getVolume().subtract(projectedFilling.subtract(requestDto.getAmount()));
					filledQuote.addOrder(new Order(order.getPrice(), leftoverVolume));
				}
				break;
			}
		}
		return new QuoteResponseDto(filledQuote.getTotal(), filledQuote.getPrice(), requestDto.getQuoteCurrency());
	}

	private List<Order> selectAskOrBidPrices(QuoteRequestDto requestDto, Orderbook orderbook) {
		if (requestDto.getAction().equals(QuoteAction.buy)) {
			return orderbook.getPrices().get(PriceType.asks);
		} else {
			return orderbook.getPrices().get(PriceType.bids);
		}
	}

	private QuoteResponseDto calculateReverseQuote(QuoteRequestDto requestDto, Orderbook orderbook) {
		return calculateQuote(requestDto, Orderbook.reverseOrderbook(orderbook));
	}
}
