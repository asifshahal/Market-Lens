package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.domain.MarketData;
import com.ooad.marketpredictor.domain.TechnicalIndicator;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TechnicalIndicatorService {
    public TechnicalIndicator calculate(List<MarketData> history) {
        List<Double> closes = history.stream().map(MarketData::close).toList();
        double current = closes.get(closes.size() - 1), previous = closes.get(closes.size() - 2);
        return new TechnicalIndicator(sma(closes, 20), sma(closes, 50), rsi(closes, 14), (current - previous) / previous * 100);
    }
    private double sma(List<Double> values, int period) { int start = Math.max(0, values.size() - period); return values.subList(start, values.size()).stream().mapToDouble(Double::doubleValue).average().orElse(0); }
    private double rsi(List<Double> v, int period) {
        int start = Math.max(1, v.size() - period); double gains = 0, losses = 0; int n = 0;
        for (int i = start; i < v.size(); i++) { double diff = v.get(i) - v.get(i - 1); if (diff > 0) gains += diff; else losses -= diff; n++; }
        if (losses == 0) return gains == 0 ? 50 : 100;
        double rs = (gains / Math.max(1, n)) / (losses / Math.max(1, n)); return 100 - 100 / (1 + rs);
    }
}
