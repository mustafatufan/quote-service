package co.bilira.quote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class QuoteResponseDto {
	@JsonProperty(value = "total", required = true)
	BigDecimal total;

	@JsonProperty(value = "price", required = true)
	BigDecimal price;

	@JsonProperty(value = "currency", required = true)
	String currency;

	public QuoteResponseDto() {
		// TODO: delete later
	}
}
