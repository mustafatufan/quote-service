package co.bilira.quote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class QuoteRequestDto {
	@JsonProperty(value = "action", required = true)
	QuoteAction action;

	@JsonProperty(value = "base_currency", required = true)
	String baseCurrency;

	@JsonProperty(value = "quote_currency", required = true)
	String quoteCurrency;

	@JsonProperty(value = "amount", required = true)
	BigDecimal amount;
}
