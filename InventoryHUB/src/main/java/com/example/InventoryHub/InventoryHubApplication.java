package com.example.InventoryHub;

import com.example.InventoryHub.model.StockProducer;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryHubApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	CommandLineRunner testQueue(StockProducer producer) {
		return args -> {
			// We are sending a simple "Hello" to the queue
			producer.publishStockUpdate("Test: New SKU Registered!");
		};
	}

}
