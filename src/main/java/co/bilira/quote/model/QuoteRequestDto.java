package co.bilira.quote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class QuoteRequestDto {
	@JsonProperty(value = "action", required = true)
	private QuoteAction action;

	@JsonProperty(value = "base_currency", required = true)
	private String baseCurrency;

	@JsonProperty(value = "quote_currency", required = true)
	private String quoteCurrency;

	@JsonProperty(value = "amount", required = true)
	private BigDecimal amount;

	public QuoteRequestDto(QuoteAction action, String baseCurrency, String quoteCurrency, BigDecimal amount) {
		this.action = action;
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		this.amount = amount;
	}

	public QuoteAction getAction() {
		return this.action;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}
}
