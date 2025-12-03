package com.dta.trade_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class TradeProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeProcessorApplication.class, args);
	}

}
