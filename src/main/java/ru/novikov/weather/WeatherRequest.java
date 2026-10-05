package ru.novikov.weather;

import java.net.URI;

public record WeatherRequest(double latitude, double longitude, int limit) {

    public static final String API_URL = "https://api.weather.yandex.ru/v2/forecast";
    public static final double DEFAULT_LATITUDE = 55.75;
    public static final double DEFAULT_LONGITUDE = 37.62;
    public static final int DEFAULT_LIMIT = 7;
    public static final int MAX_LIMIT = 7;

    public WeatherRequest {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Широта должна быть от -90 до 90: " + latitude);
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Долгота должна быть от -180 до 180: " + longitude);
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("Число суток должно быть от 1 до " + MAX_LIMIT + ": " + limit);
        }
    }

    public static WeatherRequest fromArgs(String[] args) {
        if (args.length == 0) {
            return new WeatherRequest(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_LIMIT);
        }
        if (args.length != 3) {
            throw new IllegalArgumentException("Укажите три аргумента: широта долгота число_суток, или не указывайте ничего");
        }
        return new WeatherRequest(
                parseDouble(args[0], "широта"),
                parseDouble(args[1], "долгота"),
                parseInt(args[2], "число суток"));
    }

    public URI toUri() {
        return URI.create(API_URL + "?lat=" + latitude + "&lon=" + longitude + "&limit=" + limit);
    }

    private static double parseDouble(String value, String name) {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Не число (" + name + "): " + value, e);
        }
    }

    private static int parseInt(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Не целое число (" + name + "): " + value, e);
        }
    }
}
