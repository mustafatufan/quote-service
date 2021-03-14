package co.bilira.quote.controller;

import co.bilira.quote.service.InvalidAmountException;
import co.bilira.quote.service.NoMarketException;
import co.bilira.quote.service.ConnectionUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@ControllerAdvice
public class QuoteControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(QuoteControllerExceptionHandler.class);

	@ExceptionHandler({NoMarketException.class})
	public ResponseEntity<String> throwNoMarketException(NoMarketException ex) {
		return error(NOT_FOUND, ex);
	}

	@ExceptionHandler({InvalidAmountException.class})
	public ResponseEntity<String> throwInvalidAmountException(InvalidAmountException ex) {
		return error(BAD_REQUEST, ex);
	}

	@ExceptionHandler({ConnectionUnavailableException.class})
	public ResponseEntity<String> throwConnectionUnavailableException(ConnectionUnavailableException ex) {
		return error(NOT_FOUND, ex);
	}

	private ResponseEntity<String> error(HttpStatus status, Exception ex) {
		log.error(ex.getMessage());
		return ResponseEntity.status(status).body(ex.getMessage());
	}
}
