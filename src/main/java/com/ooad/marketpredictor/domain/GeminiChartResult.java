package com.ooad.marketpredictor.domain;

import java.util.List;

public record GeminiChartResult(MarketSentiment marketSentiment, int sentimentScore, int confidence,
                                String pattern, MarketTrend trend, String summary, List<String> observations,
                                PriceLevel support, PriceLevel resistance, String entryZone,
                                String stopLoss, List<String> targets, String planRationale) { }
