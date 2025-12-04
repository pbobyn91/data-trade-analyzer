package com.dta.trade_processor.service.validation;

import com.dta.trade_processor.model.RawTradeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {
    private final boolean valid;
    private final String originalMessage;
    private final RawTradeEvent rawTradeEvent;

    public static ValidationResult valid(String originalMessage, RawTradeEvent rawTradeEvent) {
        return new ValidationResult(true, originalMessage, rawTradeEvent);
    }

    public static ValidationResult invalid(String originalMessage) {
        return new ValidationResult(false, originalMessage, null);
    }
}
