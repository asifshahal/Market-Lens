package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.data.MarketDataProvider;
import com.ooad.marketpredictor.domain.*;
import com.ooad.marketpredictor.prediction.ChartAnalysisEngine;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChartAnalysisServiceTest {
    @Test void combinesVisualAnalysisWithLiveMarketContext() {
        AssetCatalog catalog = new AssetCatalog();
        MarketDataProvider provider = new MarketDataProvider() {
            @Override public boolean supports(Asset asset) { return asset instanceof Stock; }
            @Override public List<MarketData> getDailyHistory(Asset asset, int days) {
                return java.util.stream.IntStream.range(0, 60)
                    .mapToObj(i -> new MarketData(Instant.parse("2026-10-01T00:00:00Z").plusSeconds(i * 86_400L), 100 + i, 101 + i, 99 + i, 100 + i, 1000))
                    .toList();
            }
            @Override public String getProviderName() { return "Test market feed"; }
        };
        ChartAnalysisEngine engine = mock(ChartAnalysisEngine.class);
        when(engine.isConfigured()).thenReturn(true);
        when(engine.analyze(any(), eq("image/png"), any(), eq("1D"), eq(159.0), eq("INR"))).thenReturn(
            new GeminiChartResult(MarketSentiment.BULLISH, 72, 80, "Ascending channel", MarketTrend.UPTREND,
                "Price is moving higher in the selected chart.", List.of("Higher highs are visible."),
                new PriceLevel("150–153", "Prior consolidation"), new PriceLevel("165–168", "Recent swing high"),
                "Near support", "Below support", List.of("165", "168"), "Targets follow visible resistance."));
        when(engine.getModelName()).thenReturn("test-model");
        ChartAnalysisService service = new ChartAnalysisService(catalog, List.of(provider), engine,
            new TechnicalIndicatorService(), new RecommendationService(), new ImageValidationService());
        byte[] png = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/8vgAAAAASUVORK5CYII=");
        MockMultipartFile upload = new MockMultipartFile("chart", "chart.png", "image/png", png);

        ChartAnalysis result = service.analyze(upload, "RELIANCE.NS", "1d");

        assertEquals("Reliance Industries", result.asset().getName());
        assertEquals(159.0, result.currentPrice());
        assertEquals("BUY", result.recommendation().action());
        assertEquals("test-model", result.analyzedBy());
    }
}
