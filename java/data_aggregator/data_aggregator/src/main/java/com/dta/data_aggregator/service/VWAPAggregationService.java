package com.dta.data_aggregator.service;

import com.dta.data_aggregator.model.VWAPAccumulator;
import org.springframework.stereotype.Service;
import com.dta.trade_processor.model.ProcessedTradeEvent;

@Service
public class VWAPAggregationService {

    public ProcessedTradeEvent parseProcessedTrade(String json) {
        return new ProcessedTradeEvent();
    }

    public VWAPAccumulator addTradeToAccumulator(String key, ProcessedTradeEvent trade, VWAPAccumulator accumulator) {
        return new VWAPAccumulator();
    }

    public String calculateVWAP(VWAPAccumulator accumulator) {
        return "";
    }

}
