package com.dta.trade_processor.service;

import com.dta.trade_processor.model.RawTradeEvent;
import com.dta.trade_processor.service.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradeProcessingService {

    @Autowired
    private ObjectMapper objectMapper;

    public String processValidTrade(RawTradeEvent rawTradeEvent) {
        // Implement trade processing logic here
        // Convert to a logger
        log.info("Processing trade: {}", rawTradeEvent);
        return "processed-trade-json";
    }

    public ValidationResult validateAndParse(String rawTradeJson) {
        log.debug("Validating trade message");
        try {
            // Parse JSON into RawTradeEvent
            RawTradeEvent rawTradeEvent = objectMapper.readValue(rawTradeJson, RawTradeEvent.class);

            // Business validation
            if (rawTradeEvent.getVolume() <= 0 || rawTradeEvent.getPrice() < 10.00) {
                log.warn("Trade validation failed - tradeId: {}, volume: {}, price: {}",
                        rawTradeEvent.getTradeId(), rawTradeEvent.getVolume(), rawTradeEvent.getPrice());
                return ValidationResult.invalid(rawTradeJson);
            }

            log.info("Trade validation successful - tradeId: {}", rawTradeEvent.getTradeId());
            return ValidationResult.valid(rawTradeJson, rawTradeEvent);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing error
            log.error("JSON parsing failed: {}", e.getMessage());
            return ValidationResult.invalid(rawTradeJson);
        }
    }
}
