package co.bilira.quote.service;

import org.json.JSONObject;

public interface ApiService {
	JSONObject readJsonFromUrl(String url) throws ConnectionUnavailableException;
}
