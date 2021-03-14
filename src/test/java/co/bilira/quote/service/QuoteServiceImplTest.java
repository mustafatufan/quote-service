package co.bilira.quote.service;

import co.bilira.quote.model.QuoteAction;
import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class QuoteServiceImplTest {
	@Mock
	static ApiService apiService;

	@InjectMocks
	static QuoteService quoteService = new QuoteServiceImpl(apiService);

	@BeforeEach
	public void setup() throws ConnectionUnavailableException, JSONException, IOException {
		JSONObject orderbook = getJsonObjectFromResources("orderbook.json");
		JSONObject nomarket = getJsonObjectFromResources("nomarket.json");

		MockitoAnnotations.initMocks(this);
		when(apiService.readJsonFromUrl("https://ftx.com/api/markets/BTC/TRYB/orderbook?depth=100")).thenReturn(orderbook);
		when(apiService.readJsonFromUrl("https://ftx.com/api/markets/TRYB/BTC/orderbook?depth=100")).thenReturn(nomarket);
		quoteService = new QuoteServiceImpl(apiService);
	}

	@Test
	void quoteBuyTest() throws ConnectionUnavailableException, NoMarketException, InvalidAmountException {
		QuoteRequestDto requestDto = new QuoteRequestDto(QuoteAction.buy, "BTC", "TRYB", BigDecimal.valueOf(0.1));
		QuoteResponseDto responseDto = quoteService.quote(requestDto);
		assertEquals(BigDecimal.valueOf(46050.3), responseDto.getTotal());
		assertEquals(BigDecimal.valueOf(460503), responseDto.getPrice());
		assertEquals("TRYB", responseDto.getCurrency());
	}

	@Test
	void quoteSellTest() throws ConnectionUnavailableException, NoMarketException, InvalidAmountException {
		QuoteRequestDto requestDto = new QuoteRequestDto(QuoteAction.sell, "BTC", "TRYB", BigDecimal.valueOf(0.1));
		QuoteResponseDto responseDto = quoteService.quote(requestDto);
		assertEquals(BigDecimal.valueOf(46050.25), responseDto.getTotal());
		assertEquals(BigDecimal.valueOf(460502.5), responseDto.getPrice());
		assertEquals("TRYB", responseDto.getCurrency());
	}

	@Test
	void reverseQuoteBuyTest() throws ConnectionUnavailableException, NoMarketException, InvalidAmountException {
		QuoteRequestDto requestDto = new QuoteRequestDto(QuoteAction.buy, "TRYB", "BTC", BigDecimal.valueOf(46050.25));
		QuoteResponseDto responseDto = quoteService.quote(requestDto);

		assertEquals(new BigDecimal(0.1).setScale(4, RoundingMode.HALF_UP).doubleValue(),
				responseDto.getTotal().setScale(4, RoundingMode.HALF_UP).doubleValue());

		assertEquals(new BigDecimal(0.00000217).setScale(8, RoundingMode.HALF_UP).doubleValue(),
				responseDto.getPrice().setScale(8, RoundingMode.HALF_UP).doubleValue());

		assertEquals("BTC", responseDto.getCurrency());
	}

	@Test
	void reverseQuoteSellTest() throws ConnectionUnavailableException, NoMarketException, InvalidAmountException {
		QuoteRequestDto requestDto = new QuoteRequestDto(QuoteAction.sell, "TRYB", "BTC", BigDecimal.valueOf(46050.25));
		QuoteResponseDto responseDto = quoteService.quote(requestDto);

		assertEquals(new BigDecimal(0.1).setScale(4, RoundingMode.HALF_UP).doubleValue(),
				responseDto.getTotal().setScale(4, RoundingMode.HALF_UP).doubleValue());

		assertEquals(new BigDecimal(0.00000217).setScale(8, RoundingMode.HALF_UP).doubleValue(),
				responseDto.getPrice().setScale(8, RoundingMode.HALF_UP).doubleValue());

		assertEquals("BTC", responseDto.getCurrency());
	}

	private JSONObject getJsonObjectFromResources(String fileName) throws IOException, JSONException {
		String file = String.format("src/test/resources/%s", fileName);
		String json = new String(Files.readAllBytes(Paths.get(file)));
		return new JSONObject(json);
	}
}
