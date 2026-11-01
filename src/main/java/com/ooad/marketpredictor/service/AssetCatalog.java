package com.ooad.marketpredictor.service;

import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.Crypto;
import com.ooad.marketpredictor.domain.Stock;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Locale;

@Component
public class AssetCatalog {
    private final List<Asset> assets = List.of(
        new Stock("RELIANCE.NS", "Reliance Industries", "INR", "NSE"), new Stock("TCS.NS", "Tata Consultancy Services", "INR", "NSE"),
        new Stock("INFY.NS", "Infosys", "INR", "NSE"), new Stock("HDFCBANK.NS", "HDFC Bank", "INR", "NSE"), new Stock("ICICIBANK.NS", "ICICI Bank", "INR", "NSE"),
        new Crypto("BTC", "Bitcoin", "USD", "bitcoin"), new Crypto("ETH", "Ethereum", "USD", "ethereum"), new Crypto("SOL", "Solana", "USD", "solana"),
        new Crypto("BNB", "BNB", "USD", "binancecoin"), new Crypto("XRP", "XRP", "USD", "ripple"));
    public List<Asset> getAll() { return assets; }
    public Asset find(String symbol) {
        if (symbol == null || symbol.isBlank()) throw new IllegalArgumentException("Choose a supported asset.");
        String normalized = symbol.toUpperCase(Locale.ROOT);
        return assets.stream().filter(a -> a.getSymbol().equalsIgnoreCase(normalized) || (a instanceof Crypto && a.getSymbol().equalsIgnoreCase(normalized + "USDT")))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown asset: " + symbol));
    }
}
