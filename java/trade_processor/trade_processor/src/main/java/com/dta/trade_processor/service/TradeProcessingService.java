package com.dta.trade_processor.service;

import com.dta.trade_processor.model.RawTradeEvent;
import com.dta.trade_processor.service.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeProcessingService {

    @Autowired
    private ObjectMapper objectMapper;

    public String processValidTrade(RawTradeEvent rawTradeEvent) {
        // Implement trade processing logic here
        // Convert to a logger
        System.out.println("Processing trade: " + rawTradeEvent);
        return "processed-trade-json";
    }

    public ValidationResult validateAndParse(String rawTradeJson) {
        try {
            // Parse JSON into RawTradeEvent
            RawTradeEvent rawTradeEvent = objectMapper.readValue(rawTradeJson, RawTradeEvent.class);

            // Business validation
            if (rawTradeEvent.getVolume() <= 0 || rawTradeEvent.getPrice() < 10.00) {
                return ValidationResult.invalid(rawTradeJson);
            }

            return ValidationResult.valid(rawTradeJson, rawTradeEvent);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing error
            return ValidationResult.invalid(rawTradeJson);
        }
    }
}
