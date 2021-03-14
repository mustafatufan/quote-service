package co.bilira.quote.util;

public class ConnectionUnavailableException extends Exception {
	public ConnectionUnavailableException() {
		super("Connection is unavailable");
	}
}
