package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.data.MarketDataProvider;
import com.ooad.marketpredictor.domain.*;
import com.ooad.marketpredictor.prediction.ChartAnalysisEngine;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class ChartAnalysisService {
    private static final Pattern TIMEFRAME = Pattern.compile("(?i)(1m|5m|15m|30m|1h|4h|1d|1w)");
    private static final String DISCLAIMER = "Educational technical analysis only, not financial advice. AI may misread chart images; verify levels independently.";
    private final AssetCatalog catalog; private final List<MarketDataProvider> providers; private final ChartAnalysisEngine engine;
    private final TechnicalIndicatorService indicators; private final RecommendationService recommendations; private final ImageValidationService imageValidation;

    public ChartAnalysisService(AssetCatalog catalog, List<MarketDataProvider> providers, ChartAnalysisEngine engine,
        TechnicalIndicatorService indicators, RecommendationService recommendations, ImageValidationService imageValidation) {
        this.catalog = catalog; this.providers = providers; this.engine = engine; this.indicators = indicators;
        this.recommendations = recommendations; this.imageValidation = imageValidation;
    }
    public List<Asset> getAssets() { return catalog.getAll(); }

    public ChartAnalysis analyze(MultipartFile screenshot, String symbol, String timeframe) {
        String mimeType = imageValidation.validate(screenshot);
        if (timeframe == null || !TIMEFRAME.matcher(timeframe.trim()).matches())
            throw new IllegalArgumentException("Select a supported chart timeframe.");
        Asset asset = catalog.find(symbol);
        if (!engine.isConfigured()) throw new IllegalStateException("Gemini is not configured. Set GEMINI_API_KEY, then restart the app.");
        MarketDataProvider provider = providers.stream().filter(p -> p.supports(asset)).findFirst()
            .orElseThrow(() -> new IllegalStateException("No live market data provider is configured."));
        List<MarketData> history = provider.getDailyHistory(asset, 120);
        MarketData latest = history.get(history.size() - 1);
        TechnicalIndicator marketIndicators = indicators.calculate(history);
        GeminiChartResult visual = engine.analyze(read(screenshot), mimeType, asset, timeframe.trim().toUpperCase(Locale.ROOT), latest.close(), asset.getCurrency());
        Recommendation recommendation = recommendations.recommend(visual.marketSentiment(), visual.confidence());
        return new ChartAnalysis(asset, timeframe.trim().toUpperCase(Locale.ROOT), latest.close(), asset.getCurrency(),
            latest.timestamp(), provider.getProviderName(), marketIndicators, visual.marketSentiment(), visual.sentimentScore(),
            visual.confidence(), visual.pattern(), visual.trend(), visual.summary(), visual.observations(), visual.support(),
            visual.resistance(), visual.entryZone(), visual.stopLoss(), visual.targets(), recommendation, DISCLAIMER, engine.getModelName());
    }

    private byte[] read(MultipartFile file) {
        try { return file.getBytes(); }
        catch (Exception e) { throw new IllegalArgumentException("The screenshot could not be read."); }
    }
}
