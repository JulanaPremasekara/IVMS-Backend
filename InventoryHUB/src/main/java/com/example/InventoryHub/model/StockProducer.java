package com.example.InventoryHub.model;

import com.example.InventoryHub.Config.RabbitMQConfig;
import com.example.InventoryHub.dto.ProductSummaryDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendStockUpdate(ProductSummaryDTO summary) {
        System.out.println("Sending stock update message to RabbitMQ for: " + summary.getName());

        // FIX: Changed 'stockData' to 'summary' to match the parameter above
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                summary
        );
    }

    public void publishStockUpdate(String message) {
        try {
            // FIX: Use the constants from RabbitMQConfig instead of hardcoded strings
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );
            System.out.println("Successfully sent message: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send message to RabbitMQ: " + e.getMessage());
        }
    }
}