package co.bilira.quote.service;

public class ConnectionUnavailableException extends Exception {
	public ConnectionUnavailableException() {
		super("Connection is unavailable");
	}
}
