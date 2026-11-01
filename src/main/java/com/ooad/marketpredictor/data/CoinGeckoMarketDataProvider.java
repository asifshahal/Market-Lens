package com.ooad.marketpredictor.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.Crypto;
import com.ooad.marketpredictor.domain.MarketData;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoinGeckoMarketDataProvider implements MarketDataProvider {
    private final RestClient client = RestClient.builder().baseUrl("https://api.coingecko.com/api/v3").build();
    private final ObjectMapper mapper;
    public CoinGeckoMarketDataProvider(ObjectMapper mapper) { this.mapper = mapper; }
    @Override public boolean supports(Asset asset) { return asset instanceof Crypto; }
    @Override public String getProviderName() { return "CoinGecko"; }
    @Override public List<MarketData> getDailyHistory(Asset asset, int days) {
        try {
            String json = client.get().uri(uri -> uri.path("/coins/{id}/market_chart").queryParam("vs_currency", "usd").queryParam("days", days).queryParam("interval", "daily").build(((Crypto) asset).getProviderId())).retrieve().body(String.class);
            JsonNode points = mapper.readTree(json).path("prices");
            List<MarketData> rows = new ArrayList<>();
            double previous = Double.NaN;
            for (JsonNode point : points) {
                if (point.size() < 2) continue;
                double close = point.path(1).asDouble(Double.NaN);
                if (!Double.isFinite(close) || close <= 0) continue;
                double open = Double.isFinite(previous) ? previous : close;
                rows.add(new MarketData(Instant.ofEpochMilli(point.path(0).asLong()), open, Math.max(open, close), Math.min(open, close), close, 0));
                previous = close;
            }
            if (rows.size() < 10) throw new IllegalStateException("Not enough recent market data is available");
            return List.copyOf(rows);
        } catch (Exception e) {
            if (e instanceof IllegalStateException state) throw state;
            throw new IllegalStateException("Unable to retrieve cryptocurrency data right now", e);
        }
    }
}
