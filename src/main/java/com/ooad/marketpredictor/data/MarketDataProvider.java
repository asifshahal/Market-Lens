package com.ooad.marketpredictor.data;

import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.MarketData;
import java.util.List;

public interface MarketDataProvider {
    boolean supports(Asset asset);
    List<MarketData> getDailyHistory(Asset asset, int days);
    String getProviderName();
}
