package com.dta.trade_processor.model;

import lombok.Data;

@Data
public class RawTradeEvent {
    private String tradeId;
    private String symbol;
    private Double price;
    private Integer volume;
    private Long timestamp;
}
