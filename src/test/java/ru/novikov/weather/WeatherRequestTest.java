package ru.novikov.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WeatherRequestTest {

    @Test
    void usesMoscowAndSevenDaysWithoutArguments() {
        WeatherRequest request = WeatherRequest.fromArgs(new String[0]);

        assertEquals(new WeatherRequest(55.75, 37.62, 7), request);
    }

    @Test
    void acceptsCommaAsDecimalSeparator() {
        WeatherRequest request = WeatherRequest.fromArgs(new String[] {"59,94", "30,31", "3"});

        assertEquals(new WeatherRequest(59.94, 30.31, 3), request);
    }

    @Test
    void putsCoordinatesAndLimitIntoUri() {
        WeatherRequest request = new WeatherRequest(55.75, 37.62, 5);

        assertEquals("https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=5", request.toUri().toString());
    }

    @Test
    void rejectsLatitudeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"91", "37.62", "5"}));
    }

    @Test
    void rejectsLimitOutsideOneToSeven() {
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"55.75", "37.62", "0"}));
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"55.75", "37.62", "8"}));
    }

    @Test
    void rejectsValuesThatAreNotNumbers() {
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"abc", "37.62", "5"}));
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"NaN", "37.62", "5"}));
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"55.75", "37.62", "2.5"}));
    }

    @Test
    void rejectsWrongNumberOfArguments() {
        assertThrows(IllegalArgumentException.class, () -> WeatherRequest.fromArgs(new String[] {"55.75", "37.62"}));
    }
}
