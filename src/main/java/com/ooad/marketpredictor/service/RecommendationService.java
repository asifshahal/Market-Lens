package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.domain.MarketSentiment;
import com.ooad.marketpredictor.domain.Recommendation;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {
    public Recommendation recommend(MarketSentiment sentiment, int confidence) {
        if (confidence < 55 || sentiment == MarketSentiment.NEUTRAL)
            return new Recommendation("HOLD", "The chart signal is mixed or confidence is low. Wait for clearer confirmation.");
        if (sentiment == MarketSentiment.BULLISH)
            return new Recommendation("BUY", "The chart appears bullish. Review the marked risk levels before making any decision.");
        return new Recommendation("SELL", "The chart appears bearish. Review the marked risk levels before making any decision.");
    }
}
