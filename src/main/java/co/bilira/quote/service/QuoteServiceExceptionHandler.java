package co.bilira.quote.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class QuoteServiceExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({NoMarketException.class})
	public ResponseEntity<String> throwNoMarketException(NoMarketException e) {
		return error(NOT_FOUND, e);
	}

	private ResponseEntity<String> error(HttpStatus status, Exception e) {
		return ResponseEntity.status(status).body(e.getMessage());
	}
}
