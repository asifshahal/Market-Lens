package com.ooad.marketpredictor.domain;

public final class Crypto extends Asset {
    private final String providerId;
    public Crypto(String symbol, String name, String currency, String providerId) {
        super(symbol, name, currency); this.providerId = providerId;
    }
    public String getProviderId() { return providerId; }
    @Override public String getType() { return "CRYPTO"; }
}
