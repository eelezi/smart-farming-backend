package com.timmk22.smartfarming.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HourlyForecastEntityTest {

    @Test
    @DisplayName("Should create hourly forecast and set all fields")
    void shouldCreateHourlyForecastAndSetAllFields() {
        Forecast forecast = new Forecast();
        forecast.setForecastId(1L);

        HourlyForecast hourlyForecast = new HourlyForecast();
        hourlyForecast.setId(100L);
        hourlyForecast.setForecast(forecast);
        hourlyForecast.setTime(LocalDateTime.of(2026, 5, 6, 14, 0));
        hourlyForecast.setTemp2m(new BigDecimal("24.50"));
        hourlyForecast.setRelatHum2m(new BigDecimal("55.20"));
        hourlyForecast.setCloudCover(new BigDecimal("40.00"));
        hourlyForecast.setWindSpeed10m(new BigDecimal("12.30"));
        hourlyForecast.setSoilMoisture9To27cm(new BigDecimal("18.75"));
        hourlyForecast.setDirectNormIrradiance(new BigDecimal("650.40"));
        hourlyForecast.setVapourPressureDeficit(new BigDecimal("1.85"));
        hourlyForecast.setEvapotranspiration(new BigDecimal("3.10"));

        assertThat(hourlyForecast.getId()).isEqualTo(100L);
        assertThat(hourlyForecast.getForecast()).isEqualTo(forecast);
        assertThat(hourlyForecast.getTime()).isEqualTo(LocalDateTime.of(2026, 5, 6, 14, 0));
        assertThat(hourlyForecast.getTemp2m()).isEqualByComparingTo("24.50");
        assertThat(hourlyForecast.getRelatHum2m()).isEqualByComparingTo("55.20");
        assertThat(hourlyForecast.getCloudCover()).isEqualByComparingTo("40.00");
        assertThat(hourlyForecast.getWindSpeed10m()).isEqualByComparingTo("12.30");
        assertThat(hourlyForecast.getSoilMoisture9To27cm()).isEqualByComparingTo("18.75");
        assertThat(hourlyForecast.getDirectNormIrradiance()).isEqualByComparingTo("650.40");
        assertThat(hourlyForecast.getVapourPressureDeficit()).isEqualByComparingTo("1.85");
        assertThat(hourlyForecast.getEvapotranspiration()).isEqualByComparingTo("3.10");
    }

    @Test
    @DisplayName("Should create hourly forecast with default constructor")
    void shouldCreateHourlyForecastWithDefaultConstructor() {
        HourlyForecast hourlyForecast = new HourlyForecast();

        assertThat(hourlyForecast).isNotNull();
        assertThat(hourlyForecast.getId()).isNull();
        assertThat(hourlyForecast.getForecast()).isNull();
        assertThat(hourlyForecast.getTime()).isNull();
        assertThat(hourlyForecast.getTemp2m()).isNull();
        assertThat(hourlyForecast.getRelatHum2m()).isNull();
        assertThat(hourlyForecast.getCloudCover()).isNull();
        assertThat(hourlyForecast.getWindSpeed10m()).isNull();
        assertThat(hourlyForecast.getSoilMoisture9To27cm()).isNull();
        assertThat(hourlyForecast.getDirectNormIrradiance()).isNull();
        assertThat(hourlyForecast.getVapourPressureDeficit()).isNull();
        assertThat(hourlyForecast.getEvapotranspiration()).isNull();
    }
}