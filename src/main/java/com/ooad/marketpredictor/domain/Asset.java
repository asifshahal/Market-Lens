package com.ooad.marketpredictor.domain;

public abstract class Asset {
    private final String symbol;
    private final String name;
    private final String currency;
    protected Asset(String symbol, String name, String currency) {
        this.symbol = symbol; this.name = name; this.currency = currency;
    }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getCurrency() { return currency; }
    public abstract String getType();
}
