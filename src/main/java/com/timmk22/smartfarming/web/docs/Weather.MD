# Weather API Documentation

Base URL: `/weather`

This controller provides two endpoints: fetching a 7-day weather forecast for a given set of coordinates via the Open-Meteo API, and geocoding a location name (city/village) into latitude and longitude via OpenStreetMap Nominatim. Neither endpoint requires authentication.

---

## Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/weather` | Get 7-day weather forecast for coordinates | No |
| GET | `/weather/geocode` | Convert location name to coordinates | No |

---

## GET `/weather`

Fetches a 7-day weather forecast for the given coordinates from the Open-Meteo API. Returns both daily and hourly data relevant to agricultural decision-making.

### Request

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| latitude | BigDecimal | Yes | Latitude of the location (-90 to 90) |
| longitude | BigDecimal | Yes | Longitude of the location (-180 to 180) |
| timezone | String | No | Timezone string (e.g. `Europe/Skopje`). Defaults to `UTC` if not provided |

**Example:**
```
GET /weather?latitude=41.9981&longitude=21.4254&timezone=Europe/Skopje
```

### Response

**Status:** `200 OK`

```json
{
  "forecastId": null,
  "latitude": 41.9981,
  "longitude": 21.4254,
  "timezone": "Europe/Skopje",
  "forecastDays": 7,

  "time": "2025-01-01T00:00:00",
  "temp2mMax": 18.5,
  "temp2mMin": 7.2,
  "sunrise": "2025-01-01T06:43:00",
  "sunset": "2025-01-01T16:58:00",
  "percProbMax": 20.0,
  "rainSum": 0.0,
  "showersSum": 0.0,
  "snowfallSum": 0.0,

  "hourlyTime": "2025-01-01T00:00:00",
  "temp2m": 9.1,
  "relatHum2m": 72.0,
  "cloudCover": 45.0,
  "windSpeed10m": 11.3,
  "soilMoisture9To27cm": 0.31,
  "directNormIrradiance": 0.0,
  "vapourPressureDeficit": 0.42,
  "evapotranspiration": 0.05
}
```

**Response Fields:**

**Location:**

| Field | Type | Description |
|-------|------|-------------|
| forecastId | Long | Database ID of the saved forecast (currently `null` — persistence commented out) |
| latitude | BigDecimal | Latitude used for the forecast |
| longitude | BigDecimal | Longitude used for the forecast |
| timezone | String | Timezone used for the forecast |
| forecastDays | Integer | Number of forecast days (always `7`) |

**Daily Fields (index 0 = today):**

| Field | Type | Description |
|-------|------|-------------|
| time | LocalDateTime | Date of the forecast day |
| temp2mMax | BigDecimal | Maximum temperature at 2m height (°C) |
| temp2mMin | BigDecimal | Minimum temperature at 2m height (°C) |
| sunrise | LocalDateTime | Sunrise time |
| sunset | LocalDateTime | Sunset time |
| percProbMax | BigDecimal | Maximum precipitation probability (%) |
| rainSum | BigDecimal | Total rainfall (mm) |
| showersSum | BigDecimal | Total showers (mm) |
| snowfallSum | BigDecimal | Total snowfall (cm) |

**Hourly Fields (index 0 = first hour of today):**

| Field | Type | Description |
|-------|------|-------------|
| hourlyTime | LocalDateTime | Timestamp of the hourly reading |
| temp2m | BigDecimal | Temperature at 2m height (°C) |
| relatHum2m | BigDecimal | Relative humidity at 2m height (%) |
| cloudCover | BigDecimal | Cloud cover (%) |
| windSpeed10m | BigDecimal | Wind speed at 10m height (km/h) |
| soilMoisture9To27cm | BigDecimal | Soil moisture at 9–27cm depth (m³/m³) |
| directNormIrradiance | BigDecimal | Direct normal irradiance (W/m²) |
| vapourPressureDeficit | BigDecimal | Vapour pressure deficit (kPa) |
| evapotranspiration | BigDecimal | Reference evapotranspiration ET₀ (mm) |


---

## GET `/weather/geocode`

Converts a location name (city, village, address) into a list of matching coordinates using the OpenStreetMap Nominatim API. Intended for use in the frontend location search/autocomplete before calling `/weather`.

### Request

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| q | String | Yes | Location name to search (e.g. `Bitola`, `Skopje`, `dracevo`) |

**Example:**
```
GET /weather/geocode?q=Bitola
```

### Response

**Status:** `200 OK`

Returns a list of matching locations. Up to 10 results are returned, ordered by Nominatim relevance.

```json
[
  {
    "displayName": "Bitola, Bitola Municipality, North Macedonia",
    "lat": "41.0297",
    "lon": "21.3325"
  },
  {
    "displayName": "Bitola, Bitola Municipality, North Macedonia (old town)",
    "lat": "41.0311",
    "lon": "21.3347"
  }
]
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| displayName | String | Full human-readable name of the location |
| lat | String | Latitude of the location |
| lon | String | Longitude of the location |

---

## Notes

- The `/weather` endpoint currently returns `forecastId: null` as forecast persistence is temporarily disabled pending recommendation integration.
- Both endpoints call external APIs (Open-Meteo and Nominatim). Response times depend on the availability of those services.
- Nominatim requires a `User-Agent` header by their usage policy, which is set automatically by the backend.