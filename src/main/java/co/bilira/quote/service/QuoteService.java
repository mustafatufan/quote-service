package co.bilira.quote.service;

import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;
import co.bilira.quote.util.ConnectionUnavailableException;

import java.io.IOException;

public interface QuoteService {
	QuoteResponseDto quote(QuoteRequestDto requestDto) throws IOException, NoMarketException, InvalidAmountException, ConnectionUnavailableException;
}
