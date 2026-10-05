package ru.novikov.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public record ForecastReport(double currentTemperature, double averageDayTemperature, int days) {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ForecastReport parse(String json, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Число суток должно быть больше нуля: " + limit);
        }
        JsonNode root = readTree(json);
        JsonNode currentTemperature = root.path("fact").path("temp");
        if (!currentTemperature.isNumber()) {
            throw new IllegalArgumentException("В ответе сервиса нет числа fact.temp");
        }
        JsonNode forecasts = root.path("forecasts");
        if (!forecasts.isArray() || forecasts.isEmpty()) {
            throw new IllegalArgumentException("В ответе сервиса нет прогноза forecasts");
        }
        int days = Math.min(limit, forecasts.size());
        double sum = 0;
        for (int i = 0; i < days; i++) {
            JsonNode forecast = forecasts.get(i);
            JsonNode dayTemperature = forecast.path("parts").path("day").path("temp_avg");
            if (!dayTemperature.isNumber()) {
                throw new IllegalArgumentException("В прогнозе на " + forecast.path("date").asText("сутки №" + (i + 1)) + " нет числа parts.day.temp_avg");
            }
            sum += dayTemperature.asDouble();
        }
        return new ForecastReport(currentTemperature.asDouble(), sum / days, days);
    }

    private static JsonNode readTree(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ответ сервиса не является JSON: " + e.getOriginalMessage(), e);
        }
    }
}
