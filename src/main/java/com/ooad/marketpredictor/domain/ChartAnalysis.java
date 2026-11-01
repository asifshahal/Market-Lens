package com.ooad.marketpredictor.domain;

import java.time.Instant;
import java.util.List;

public record ChartAnalysis(Asset asset, String timeframe, double currentPrice, String currency,
                            Instant marketDataAsOf, String dataSource, TechnicalIndicator marketIndicators,
                            MarketSentiment marketSentiment, int sentimentScore, int confidencePercent,
                            String pattern, MarketTrend trend, String summary, List<String> observations,
                            PriceLevel support, PriceLevel resistance, String entryZone, String stopLoss,
                            List<String> targets, Recommendation recommendation, String disclaimer,
                            String analyzedBy) { }
