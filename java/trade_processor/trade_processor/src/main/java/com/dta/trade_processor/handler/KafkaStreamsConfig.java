package com.dta.trade_processor.handler;

import com.dta.trade_processor.service.TradeProcessingService;
import com.dta.trade_processor.service.validation.ValidationResult;
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

    @Value("${app.kafka.dlq-topic}")
    private String dlqTopic;

    @Autowired
    private TradeProcessingService tradeProcessingService;

    @Bean
    public KStream<String, String> processTradeStream(StreamsBuilder builder) {
        KStream<String, String> inputStream = builder.stream(inputTopic);

        // Validate and parse incoming messages
        KStream<String, ValidationResult> validatedStream = inputStream
                .mapValues(tradeProcessingService::validateAndParse);

        // Branch based on validation result
        KStream<String, ValidationResult>[] branches = validatedStream.branch(
                (key, result) -> result.isValid(),      // Valid Messages
                (key, result) -> true                   // Invalid Messages
        );

        branches[0]
                .mapValues(ValidationResult::getRawTradeEvent)
                .mapValues(tradeProcessingService::processValidTrade)
                .to(outputTopic);

        branches[1]
                .mapValues(ValidationResult::getOriginalMessage)
                .to(dlqTopic);

        return inputStream;
    }
}
