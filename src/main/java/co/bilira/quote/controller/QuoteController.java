package co.bilira.quote.controller;

import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import co.bilira.quote.service.InvalidAmountException;
import co.bilira.quote.service.NoMarketException;
import co.bilira.quote.service.QuoteService;
import co.bilira.quote.util.ConnectionUnavailableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class QuoteController {
	private final QuoteService quoteService;

	public QuoteController(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	@PostMapping(value = "/quote", produces = {"application/json"})
	public @ResponseBody
	QuoteResponseDto quote(@RequestBody QuoteRequestDto requestDto) throws IOException, NoMarketException, InvalidAmountException, ConnectionUnavailableException {
		return quoteService.quote(requestDto);
	}
}
