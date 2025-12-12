package com.dta.data_aggregator.handler;

import com.dta.data_aggregator.model.VWAPAccumulator;
import com.dta.data_aggregator.service.VWAPAggregationService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dta.trade_processor.model.ProcessedTradeEvent;

import java.time.Duration;

@Configuration
public class KafkaStreamConfig {

    @Value("${app.kafka.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    @Value("${app.kafka.state-store}")
    private String stateStore;

    @Autowired
    private VWAPAggregationService aggregationService;

    @Bean
    public KStream<String, String> processVWAPStream(StreamsBuilder builder) {
        KStream<String, String> inputStream = builder.stream(inputTopic);

        KStream<String, ProcessedTradeEvent> parsedStream= inputStream
                .mapValues(aggregationService::parseProcessedTrade)
                .selectKey((key, trade) -> trade.getSymbol());

        parsedStream
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
                .aggregate(
                    VWAPAccumulator::new,
                    aggregationService::addTradeToAccumulator,
                    Materialized.as(stateStore)
                )
                .toStream()
                .mapValues(aggregationService::calculateVWAP)
                .to(outputTopic);

        return inputStream;
    }
}
