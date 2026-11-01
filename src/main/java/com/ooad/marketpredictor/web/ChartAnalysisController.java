package com.ooad.marketpredictor.web;

import com.ooad.marketpredictor.domain.Asset;
import com.ooad.marketpredictor.domain.ChartAnalysis;
import com.ooad.marketpredictor.service.ChartAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChartAnalysisController {
    private final ChartAnalysisService service;
    public ChartAnalysisController(ChartAnalysisService service) { this.service = service; }
    @GetMapping("/assets") public List<Asset> assets() { return service.getAssets(); }
    @PostMapping(value = "/analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChartAnalysis analyze(@RequestParam("chart") MultipartFile chart, @RequestParam("symbol") String symbol,
                                 @RequestParam("timeframe") String timeframe) {
        return service.analyze(chart, symbol, timeframe);
    }
}
