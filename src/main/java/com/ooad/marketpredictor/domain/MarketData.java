package com.ooad.marketpredictor.domain;

import java.time.Instant;

public record MarketData(Instant timestamp, double open, double high, double low, double close, double volume) { }
