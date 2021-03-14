package co.bilira.quote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class QuoteResponseDto {
	@JsonProperty(value = "total", required = true)
	private BigDecimal total;

	@JsonProperty(value = "price", required = true)
	private BigDecimal price;

	@JsonProperty(value = "currency", required = true)
	private String currency;

	public QuoteResponseDto(BigDecimal total, BigDecimal price, String currency) {
		this.total = total;
		this.price = price;
		this.currency = currency;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}
}
