package com.ooad.marketpredictor.prediction;

import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.GeminiChartResult;

public interface ChartAnalysisEngine {
    default boolean isConfigured() { return true; }
    GeminiChartResult analyze(byte[] image, String mimeType, Asset asset, String timeframe,
                              double currentPrice, String currency);
    String getModelName();
}
