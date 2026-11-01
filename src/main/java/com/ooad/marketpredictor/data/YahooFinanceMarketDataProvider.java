package com.ooad.marketpredictor.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.MarketData;
import com.ooad.marketpredictor.domain.Stock;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceMarketDataProvider implements MarketDataProvider {
    private final RestClient client = RestClient.builder().baseUrl("https://query1.finance.yahoo.com").defaultHeader("User-Agent", "Mozilla/5.0 MarketPredictor/1.0").build();
    private final ObjectMapper mapper;
    public YahooFinanceMarketDataProvider(ObjectMapper mapper) { this.mapper = mapper; }
    @Override public boolean supports(Asset asset) { return asset instanceof Stock; }
    @Override public String getProviderName() { return "Yahoo Finance (unofficial chart endpoint)"; }
    @Override public List<MarketData> getDailyHistory(Asset asset, int days) {
        try {
            String json = client.get().uri(uri -> uri.path("/v8/finance/chart/{symbol}").queryParam("range", days + "d").queryParam("interval", "1d").build(asset.getSymbol())).retrieve().body(String.class);
            JsonNode root = mapper.readTree(json).path("chart");
            if (!root.path("error").isMissingNode() && !root.path("error").isNull()) throw new IllegalStateException("Market data provider returned an error");
            JsonNode result = root.path("result").path(0);
            JsonNode timestamps = result.path("timestamp");
            JsonNode quote = result.path("indicators").path("quote").path(0);
            List<MarketData> rows = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                double close = value(quote.path("close").path(i));
                if (!Double.isFinite(close) || close <= 0) continue;
                rows.add(new MarketData(Instant.ofEpochSecond(timestamps.path(i).asLong()), value(quote.path("open").path(i)), value(quote.path("high").path(i)), value(quote.path("low").path(i)), close, value(quote.path("volume").path(i))));
            }
            if (rows.size() < 10) throw new IllegalStateException("Not enough recent market data is available");
            return List.copyOf(rows);
        } catch (Exception e) {
            if (e instanceof IllegalStateException state) throw state;
            throw new IllegalStateException("Unable to retrieve Indian stock data right now", e);
        }
    }
    private double value(JsonNode node) { return node.isNumber() ? node.asDouble() : Double.NaN; }
}
