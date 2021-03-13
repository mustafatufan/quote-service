package co.bilira.quote.service;

import co.bilira.quote.model.QuoteRequestDto;
import co.bilira.quote.model.QuoteResponseDto;

import java.io.IOException;

public interface QuoteService {
	QuoteResponseDto quote(QuoteRequestDto requestDto) throws IOException, NoMarketException;
}
