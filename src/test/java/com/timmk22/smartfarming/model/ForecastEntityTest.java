package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ForecastEntityTest {

    @Test
    @DisplayName("Should create forecast and set fields")
    void shouldCreateForecastAndSetFields() {
        Recommendation recommendation = new Recommendation();
        recommendation.setRecommendationId(1L);

        Forecast forecast = new Forecast();
        forecast.setForecastId(10L);
        forecast.setRecommendation(recommendation);
        forecast.setLatitude(new BigDecimal("41.123456"));
        forecast.setLongitude(new BigDecimal("22.654321"));
        forecast.setTimezone("Europe/Skopje");
        forecast.setForecastDays(7);
        forecast.setTime(LocalDateTime.of(2026, 5, 6, 10, 0));
        forecast.setTemp2mMax(new BigDecimal("28.50"));
        forecast.setTemp2mMin(new BigDecimal("14.20"));
        forecast.setSunrise(LocalDateTime.of(2026, 5, 6, 5, 10));
        forecast.setSunset(LocalDateTime.of(2026, 5, 6, 19, 30));
        forecast.setPercProbMax(new BigDecimal("60.00"));
        forecast.setRainSum(new BigDecimal("12.30"));
        forecast.setShowersSum(new BigDecimal("3.40"));
        forecast.setSnowfallSum(new BigDecimal("0.00"));

        assertThat(forecast.getForecastId()).isEqualTo(10L);
        assertThat(forecast.getRecommendation()).isEqualTo(recommendation);
        assertThat(forecast.getLatitude()).isEqualByComparingTo("41.123456");
        assertThat(forecast.getLongitude()).isEqualByComparingTo("22.654321");
        assertThat(forecast.getTimezone()).isEqualTo("Europe/Skopje");
        assertThat(forecast.getForecastDays()).isEqualTo(7);
        assertThat(forecast.getTime()).isEqualTo(LocalDateTime.of(2026, 5, 6, 10, 0));
        assertThat(forecast.getTemp2mMax()).isEqualByComparingTo("28.50");
        assertThat(forecast.getTemp2mMin()).isEqualByComparingTo("14.20");
        assertThat(forecast.getSunrise()).isEqualTo(LocalDateTime.of(2026, 5, 6, 5, 10));
        assertThat(forecast.getSunset()).isEqualTo(LocalDateTime.of(2026, 5, 6, 19, 30));
        assertThat(forecast.getPercProbMax()).isEqualByComparingTo("60.00");
        assertThat(forecast.getRainSum()).isEqualByComparingTo("12.30");
        assertThat(forecast.getShowersSum()).isEqualByComparingTo("3.40");
        assertThat(forecast.getSnowfallSum()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Should initialize hourly forecasts list by default")
    void shouldInitializeHourlyForecastsListByDefault() {
        Forecast forecast = new Forecast();

        assertThat(forecast.getHourlyForecasts()).isNotNull();
        assertThat(forecast.getHourlyForecasts()).isEmpty();
    }
}