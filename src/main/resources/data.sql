-- Smart Farming sample seed data for local development only.
-- Loaded via application-local.yml with spring.sql.init.mode=always.
-- Inserts are idempotent so the file can run multiple times safely.

-- -----------------------------------------------------------------------------
-- Users
-- -----------------------------------------------------------------------------
-- INSERT INTO users (user_id, name, email, password)
-- VALUES
--     (1, 'Alice Johnson', 'alice@smartfarm.test', '$2a$10$7QvQ8mYFZf8q7J3vBfV8oO2r4D6QY5wXg1F4fN8kP5m2Z8G5mV3rK'),
--     (2, 'Mark Peterson', 'mark@smartfarm.test', '$2a$10$7QvQ8mYFZf8q7J3vBfV8oO2r4D6QY5wXg1F4fN8kP5m2Z8G5mV3rK')
-- ON CONFLICT (user_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Crop reference data
-- -----------------------------------------------------------------------------
INSERT INTO crop (crop_id, name)
VALUES
    (1, 'Wheat'),
    (2, 'Corn'),
    (3, 'Tomato'),
    (4, 'Sunflower')
ON CONFLICT (crop_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Soil types
-- -----------------------------------------------------------------------------
INSERT INTO soil_type (soil_id, name)
VALUES
    (1, 'Loam'),
    (2, 'Clay'),
    (3, 'Sandy'),
    (4, 'Silt')
ON CONFLICT (soil_id) DO NOTHING;

-- -----------------------------------------------------------------------------
-- Planting entries
-- -----------------------------------------------------------------------------
-- INSERT INTO planting_information (
--     planting_id,
--     area,
--     latitude,
--     longitude,
--     location_name,
--     irrigation_type,
--     current_status,
--     expected_harvest_date,
--     planting_date,
--     notes,
--     user_id,
--     crop_id,
--     soil_id
-- )
-- VALUES
--     (1, 12.50, 41.9973, 21.4280, 'North Field', 'DRIP', 'HEALTHY', '2026-07-20', '2026-03-15', 'Primary wheat field with stable moisture.', 1, 1, 1),
--     (2, 8.20, 41.9958, 21.4345, 'East Field', 'SPRINKLER', 'WARNING', '2026-08-02', '2026-03-22', 'Corn showing early nutrient stress.', 1, 2, 2),
--     (3, 2.40, 42.0011, 21.4218, 'Greenhouse A', 'MANUAL', 'HEALTHY', '2026-06-18', '2026-04-01', 'Tomato greenhouse crop for fresh market.', 1, 3, 3),
--     (4, 14.80, 41.9907, 21.4402, 'South Block', 'RAIN_FED', 'CRITICAL', '2026-09-10', '2026-02-10', 'Sunflower block affected by pest pressure.', 2, 4, 4),
--     (5, 9.30, 41.9892, 21.4166, 'West Field', 'FLOOD', 'WARNING', '2026-07-28', '2026-03-05', 'Wheat field under observation after heavy rain.', 2, 1, 1)
-- ON CONFLICT (planting_id) DO NOTHING;
--
-- -- -----------------------------------------------------------------------------
-- -- AI recommendations
-- -- -----------------------------------------------------------------------------
-- INSERT INTO recommendation (
--     recommendation_id,
--     recommendation_text,
--     created_at,
--     planting_id
-- )
-- VALUES
--     (1, 'Maintain the current drip irrigation schedule and continue monitoring soil moisture every 48 hours. The wheat stand looks healthy and uniform.', '2026-05-01T09:00:00', 1),
--     (2, 'Apply a moderate nitrogen top-dressing and inspect leaves for early signs of disease. Reduce overhead watering until the crop recovers.', '2026-05-01T09:15:00', 2),
--     (3, 'Keep the greenhouse temperature stable, remove lower damaged leaves, and continue careful manual irrigation for the tomato crop.', '2026-05-01T09:30:00', 3),
--     (4, 'Prioritize pest control measures immediately and inspect surrounding rows for spread. The sunflower block needs urgent intervention.', '2026-05-01T09:45:00', 4),
--     (5, 'Improve drainage around the wheat field and delay irrigation until the soil profile stabilizes after recent rainfall.', '2026-05-01T10:00:00', 5)
-- ON CONFLICT (recommendation_id) DO NOTHING;
--
-- -- -----------------------------------------------------------------------------
-- -- Forecasts linked to recommendations
-- -- -----------------------------------------------------------------------------
-- INSERT INTO forecast (
--     forecast_id,
--     recom_id,
--     latitude,
--     longitude,
--     timezone,
--     forecast_days,
--     time,
--     temp_2m_max,
--     temp_2m_min,
--     sunrise,
--     sunset,
--     perc_prob_max,
--     rain_sum,
--     showers_sum,
--     snowfall_sum
-- )
-- VALUES
--     (1, 1, 41.9973, 21.4280, 'Europe/Skopje', 3, '2026-05-02T00:00:00', 24.50, 11.20, '2026-05-02T05:22:00', '2026-05-02T19:10:00', 12.00, 0.20, 0.00, 0.00),
--     (2, 2, 41.9958, 21.4345, 'Europe/Skopje', 3, '2026-05-02T00:00:00', 26.10, 13.40, '2026-05-02T05:22:00', '2026-05-02T19:10:00', 34.00, 1.40, 0.80, 0.00),
--     (3, 3, 42.0011, 21.4218, 'Europe/Skopje', 2, '2026-05-02T00:00:00', 28.00, 15.10, '2026-05-02T05:22:00', '2026-05-02T19:10:00', 8.00, 0.00, 0.00, 0.00),
--     (4, 4, 41.9907, 21.4402, 'Europe/Skopje', 4, '2026-05-02T00:00:00', 23.40, 10.60, '2026-05-02T05:22:00', '2026-05-02T19:10:00', 46.00, 3.10, 1.80, 0.00),
--     (5, 5, 41.9892, 21.4166, 'Europe/Skopje', 3, '2026-05-02T00:00:00', 22.80, 10.20, '2026-05-02T05:22:00', '2026-05-02T19:10:00', 28.00, 1.00, 0.50, 0.00)
-- ON CONFLICT (forecast_id) DO NOTHING;
--
-- -- -----------------------------------------------------------------------------
-- -- Hourly forecast samples
-- -- -----------------------------------------------------------------------------
-- INSERT INTO hourly_forecast (
--     id,
--     forecast_id,
--     time,
--     temp_2m,
--     relat_hum_2m,
--     cloud_cover,
--     wind_speed_10m,
--     soil_moisture_9_to_27cm,
--     direct_norm_irradiance,
--     vapour_pressure_deficit,
--     evapotranspiration
-- )
-- VALUES
--     (1, 1, '2026-05-02T06:00:00', 15.20, 78.00, 12.00, 6.40, 0.31, 120.00, 0.42, 0.05),
--     (2, 1, '2026-05-02T12:00:00', 23.80, 52.00, 18.00, 9.10, 0.29, 760.00, 1.10, 0.18),
--     (3, 2, '2026-05-02T06:00:00', 16.10, 74.00, 24.00, 7.20, 0.35, 100.00, 0.48, 0.06),
--     (4, 2, '2026-05-02T12:00:00', 25.40, 49.00, 40.00, 11.30, 0.32, 710.00, 1.25, 0.21),
--     (5, 3, '2026-05-02T06:00:00', 17.00, 70.00, 8.00, 4.80, 0.27, 160.00, 0.39, 0.04),
--     (6, 4, '2026-05-02T06:00:00', 14.40, 81.00, 58.00, 10.20, 0.38, 60.00, 0.35, 0.03),
--     (7, 4, '2026-05-02T12:00:00', 22.10, 58.00, 72.00, 13.50, 0.36, 540.00, 0.98, 0.12),
--     (8, 5, '2026-05-02T06:00:00', 14.90, 79.00, 35.00, 8.70, 0.33, 140.00, 0.44, 0.05)
-- ON CONFLICT (id) DO NOTHING;
--
