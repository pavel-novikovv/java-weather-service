package ru.novikov.weather;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class WeatherApp {

    static final String KEY_VARIABLE = "YANDEX_WEATHER_KEY";
    private static final String KEY_HEADER = "X-Yandex-Weather-Key";

    public static void main(String[] args) {
        try {
            String apiKey = readApiKey();
            WeatherRequest request = WeatherRequest.fromArgs(args);
            System.out.println("Запрос: GET " + request.toUri());
            String body = fetch(request, apiKey);
            System.out.println("Полный ответ сервиса:");
            System.out.println(body);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static String readApiKey() {
        String apiKey = System.getenv(KEY_VARIABLE);
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Задайте ключ API Яндекс Погоды в переменной окружения " + KEY_VARIABLE);
        }
        return apiKey.trim();
    }

    private static String fetch(WeatherRequest request, String apiKey) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest httpRequest = HttpRequest.newBuilder(request.toUri())
                .header(KEY_HEADER, apiKey)
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Сервис ответил кодом " + response.statusCode() + ": " + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось получить данные от сервиса: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Запрос к сервису прерван", e);
        }
    }
}
