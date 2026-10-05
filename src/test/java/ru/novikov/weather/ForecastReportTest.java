package ru.novikov.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ForecastReportTest {

    private static final String MOSCOW_RESPONSE = readResource("/forecast-moscow-2026-10-05.json");

    @Test
    void readsCurrentTemperatureFromFact() {
        ForecastReport report = ForecastReport.parse(MOSCOW_RESPONSE, 5);

        assertEquals(11, report.currentTemperature());
    }

    @Test
    void averagesDayTemperatureOverLimitDays() {
        ForecastReport report = ForecastReport.parse(MOSCOW_RESPONSE, 5);

        assertEquals(5, report.days());
        assertEquals(13.8, report.averageDayTemperature(), 1e-9);
    }

    @Test
    void takesOnlyFirstLimitDays() {
        ForecastReport report = ForecastReport.parse(MOSCOW_RESPONSE, 2);

        assertEquals(2, report.days());
        assertEquals(12.5, report.averageDayTemperature(), 1e-9);
    }

    @Test
    void usesAvailableDaysWhenForecastIsShorterThanLimit() {
        ForecastReport report = ForecastReport.parse(MOSCOW_RESPONSE, 7);

        assertEquals(5, report.days());
        assertEquals(13.8, report.averageDayTemperature(), 1e-9);
    }

    @Test
    void failsWithoutFactTemperature() {
        assertThrows(IllegalArgumentException.class,
                () -> ForecastReport.parse("{\"forecasts\":[{\"parts\":{\"day\":{\"temp_avg\":1}}}]}", 1));
    }

    @Test
    void namesTheDateWhenDayTemperatureIsMissing() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ForecastReport.parse("{\"fact\":{\"temp\":1},\"forecasts\":[{\"date\":\"2026-10-05\",\"parts\":{}}]}", 1));

        assertTrue(error.getMessage().contains("2026-10-05"));
    }

    @Test
    void failsOnResponseThatIsNotJson() {
        assertThrows(IllegalArgumentException.class, () -> ForecastReport.parse("<html>Forbidden</html>", 1));
    }

    private static String readResource(String name) {
        try (InputStream stream = ForecastReportTest.class.getResourceAsStream(name)) {
            if (stream == null) {
                throw new IllegalStateException("Нет тестового файла " + name);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + name, e);
        }
    }
}
