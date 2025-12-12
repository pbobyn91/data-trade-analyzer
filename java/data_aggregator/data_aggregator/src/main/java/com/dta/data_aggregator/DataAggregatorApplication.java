package com.dta.data_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class DataAggregatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataAggregatorApplication.class, args);
	}

}
