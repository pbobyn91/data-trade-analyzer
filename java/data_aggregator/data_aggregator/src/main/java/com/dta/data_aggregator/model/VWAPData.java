package com.dta.data_aggregator.model;

import lombok.Data;

@Data
public class VWAPData {
    private String symbol;
    private double vwap;
    private int totalVolume;
    private int tradeCount;
    private long windowStart;
    private long windowEnd;
    private long calculationTimestamp;
}
