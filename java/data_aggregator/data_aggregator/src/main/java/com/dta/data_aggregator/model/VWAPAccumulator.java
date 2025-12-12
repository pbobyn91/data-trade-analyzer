package com.dta.data_aggregator.model;

import lombok.Data;

@Data
public class VWAPAccumulator {
    private double totalPriceVolume;
    private int totalVolume;
    private int tradeCount;
}
