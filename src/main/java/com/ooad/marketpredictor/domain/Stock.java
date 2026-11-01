package com.ooad.marketpredictor.domain;

public final class Stock extends Asset {
    private final String exchange;
    public Stock(String symbol, String name, String currency, String exchange) {
        super(symbol, name, currency); this.exchange = exchange;
    }
    public String getExchange() { return exchange; }
    @Override public String getType() { return "STOCK"; }
}
