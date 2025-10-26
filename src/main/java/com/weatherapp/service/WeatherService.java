package com.weatherapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.service.model.WeatherResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class WeatherService {
    // Load API key from environment variable for better security
    // Example: set OPENWEATHER_API_KEY=your_key_here
    private static final String API_KEY = System.getenv("OPENWEATHER_API_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();

    public WeatherResponse fetchByCity(String city) throws IOException, InterruptedException {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IOException("Missing API key. Set the environment variable OPENWEATHER_API_KEY before running.");
        }

        String url = String.format("%s?q=%s&units=metric&appid=%s", BASE_URL, encode(city), API_KEY);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        int status = resp.statusCode();
        if (status == 200) {
            return gson.fromJson(resp.body(), WeatherResponse.class);
        } else {
            try {
                JsonObject o = JsonParser.parseString(resp.body()).getAsJsonObject();
                String message = o.has("message") ? o.get("message").getAsString() : "HTTP " + status;
                throw new IOException("API error: " + message);
            } catch (Exception ex) {
                throw new IOException("API error: HTTP " + status);
            }
        }
    }

    private static String encode(String s) {
        return s.replace(" ", "%20");
    }
}
