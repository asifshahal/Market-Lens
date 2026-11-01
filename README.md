# Market Lens — OOAD College Project

A compact chart screenshot analyzer for Indian stocks and cryptocurrencies. Users upload a chart, select its asset and timeframe, and receive a structured visual analysis with trend, support/resistance, technical observations, and a study-oriented BUY / HOLD / SELL signal. The app also fetches current market data and RSI/SMA indicators for context.

> Educational use only, not financial advice. Generative AI can misread screenshots or produce inaccurate levels. Verify analysis independently. Uploaded screenshots are processed in memory and are not stored by this application.

## Run locally

Requirements: Java 17+ and Maven 3.9+. Create a Gemini API key in [Google AI Studio](https://aistudio.google.com/app/apikey). Keep it private; do not put it in frontend code, `application.properties`, or source control.

In Antigravity, open **Terminal → New Terminal**. In the macOS zsh terminal, run this by itself; the key you type at the prompt will not be shown:

```zsh
read -s "GEMINI_API_KEY?Paste your Gemini API key (hidden): "; echo
```

Paste your key at the prompt and press Return. Then start the app in that same terminal:

```zsh
export GEMINI_API_KEY
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). You can optionally set `GEMINI_MODEL` to a model available to your API key. The default is `gemini-3.8-flash`.

## Supported assets

| Type | Symbols |
|---|---|
| NSE stocks | INR | `RELIANCE.NS`, `TCS.NS`, `INFY.NS`, `HDFCBANK.NS`, `ICICIBANK.NS` |
| Crypto | USD | `BTC`, `ETH`, `SOL`, `BNB`, `XRP` |

Screenshots: PNG or JPEG, up to 5 MB. Supported timeframes: 15m, 30m, 1h, 4h, 1d, 1w.

## API

- `GET /api/assets` — supported assets and type.
- `POST /api/analysis` — multipart form with `chart` image, `symbol`, and `timeframe`.

## OOAD package map

- `domain`: `Asset` with `Stock` / `Crypto`, `MarketData`, `TechnicalIndicator`, `ChartAnalysis`, `PriceLevel`, `Recommendation`, and sentiment/trend enums.
- `data`: `MarketDataProvider` interface with Yahoo Finance and CoinGecko adapters.
- `prediction`: `ChartAnalysisEngine` interface and `GeminiChartAnalysisEngine` implementation.
- `service`: `ChartAnalysisService` coordinates image validation, live data, indicators, and recommendation rules.
- `web`: multipart analysis endpoint and API error mapping.
- `src/main/resources/static`: HTML, CSS, and JavaScript dashboard.

The app has no login, database, history, payments, or trade execution. The design maps to use-case, class, sequence, and component diagrams.

## Verify

```bash
mvn test
```
