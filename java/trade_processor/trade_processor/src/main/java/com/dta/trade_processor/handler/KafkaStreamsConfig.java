package com.dta.trade_processor.handler;

import com.dta.trade_processor.service.TradeProcessingService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaStreamsConfig {

    @Value("${app.kafka.input-topic}")
    private String inputTopic;

    @Value("${app.kafka.output-topic}")
    private String outputTopic;

    @Autowired
    private TradeProcessingService tradeProcessingService;

    @Bean
    public KStream<String, String> processTradeStream(StreamsBuilder builder) {
        KStream<String, String> inputStream = builder.stream(inputTopic);

        inputStream
                .mapValues(tradeProcessingService::processRawTrade)
                .to(outputTopic);

        return inputStream;
    }
}
