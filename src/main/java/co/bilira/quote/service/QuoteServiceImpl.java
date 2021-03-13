package co.bilira.quote.service;

import co.bilira.quote.model.Orderbook;
import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import co.bilira.quote.util.HttpUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
		// TODO: make calculation
		return new QuoteResponseDto();
	}
}
