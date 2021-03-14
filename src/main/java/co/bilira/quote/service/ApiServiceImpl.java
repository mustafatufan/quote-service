package co.bilira.quote.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class ApiServiceImpl implements ApiService {

	private static final Logger log = LoggerFactory.getLogger(ApiServiceImpl.class);

	@Override
	public JSONObject readJsonFromUrl(String url) throws ConnectionUnavailableException {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "quote-service");
			connection.connect();
			InputStream is;
			if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				is = connection.getInputStream();
			} else {
				is = connection.getErrorStream();
			}
			try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String jsonText = readAll(rd);
				return new JSONObject(jsonText);
			} finally {
				is.close();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new ConnectionUnavailableException();
		}
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
}
