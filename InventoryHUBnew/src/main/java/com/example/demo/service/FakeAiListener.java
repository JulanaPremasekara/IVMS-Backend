package com.example.demo.service;

import com.example.demo.dto.StockAdjustmentDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class FakeAiListener {

    @RabbitListener(queues = "inventory_ai_queue")
    public void receiveFakeMessage(StockAdjustmentDTO message) {
        System.out.println("--- FAKE AI WORKER START ---");
        System.out.println("Received Message for Product: " + message.getProductId());
        System.out.println("Proposed New Limit: " + message.getNewSafetyLimit());
        System.out.println("Adjustment Reason: " + message.getAdjustmentType());

        // Simulate "Processing"
        if (message.getNewSafetyLimit() < 10) {
            System.out.println("FAKE AI RESULT: Warning! This stock level is dangerously low.");
        } else {
            System.out.println("FAKE AI RESULT: Stock level looks good.");
        }

        System.out.println("--- FAKE AI WORKER END ---");
    }
}