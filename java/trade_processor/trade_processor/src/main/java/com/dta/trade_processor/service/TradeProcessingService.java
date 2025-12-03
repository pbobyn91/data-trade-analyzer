package com.dta.trade_processor.service;

import org.springframework.stereotype.Service;

@Service
public class TradeProcessingService {

    public String processRawTrade(String rawTradeJson) {
        // Implement trade processing logic here
        // Convert to a logger
        System.out.println("Processing trade: " + rawTradeJson);
        return rawTradeJson;
    }
}
