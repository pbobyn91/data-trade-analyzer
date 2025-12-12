package com.dta.data_aggregator.handler;

import com.dta.data_aggregator.service.VWAPAggregationService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaStreamConfig {

    @Value("${app.kafka.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    @Autowired
    private VWAPAggregationService aggregationService;

    @Bean
    public KStream<String, String> processVWAPStream(StreamsBuilder builder) {
        KStream<String, String> inputStream = builder.stream(inputTopic);

        inputStream
                .mapValues(aggregationService::processTradeForVWAP)
                .to(outputTopic);

        return inputStream;
    }
}
