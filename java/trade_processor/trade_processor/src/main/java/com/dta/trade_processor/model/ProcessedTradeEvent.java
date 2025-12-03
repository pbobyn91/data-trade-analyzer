package com.dta.trade_processor.model;

import lombok.Data;

@Data
public class ProcessedTradeEvent {
    private String tradeId;
    private String symbol;
    private Double price;
    private Integer volume;
    private String riskScore;
    private Boolean validationStatus;
    private Long processedAt;
}
