package com.dta.trade_processor.model;

import com.dta.data_aggregator.model.RiskScore;
import lombok.Data;

@Data
public class ProcessedTradeEvent {
    private String tradeId;
    private String symbol;
    private Double price;
    private Integer volume;
    private RiskScore riskScore;
    private Boolean validationStatus;
    private Long processedAt;
}
