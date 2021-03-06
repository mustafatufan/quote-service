package co.bilira.quote.service;

import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;

public interface QuoteService {
	QuoteResponseDto quote(QuoteRequestDto requestDto) throws NoMarketException, InvalidAmountException, ConnectionUnavailableException;
}
