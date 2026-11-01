package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.domain.MarketSentiment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationServiceTest {
    private final RecommendationService service = new RecommendationService();
    @Test void mapsConfidentBullishSentimentToBuy() { assertEquals("BUY", service.recommend(MarketSentiment.BULLISH, 70).action()); }
    @Test void mapsConfidentBearishSentimentToSell() { assertEquals("SELL", service.recommend(MarketSentiment.BEARISH, 70).action()); }
    @Test void holdsWhenConfidenceIsLow() { assertEquals("HOLD", service.recommend(MarketSentiment.BULLISH, 54).action()); }
}
