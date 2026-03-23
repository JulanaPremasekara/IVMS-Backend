package com.example.demo.model;

import com.example.demo.Config.RabbitMQConfig;
import com.example.demo.dto.ProductSummaryDTO;
import com.example.demo.service.ProcurementClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockListener {

    @Autowired
    private ProcurementClient procurementClient;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleStockMessage(ProductSummaryDTO summary) {
        System.out.println("Listener received stock update for: " + summary.getName());

        // Check the threshold logic locally (Stateless)
        if (summary.getCurrentStock() <= summary.getReorderThreshold()) {
            System.out.println("LOW STOCK ALERT! Notifying Procurement...");
            procurementClient.callProcurement(summary);
        } else {
            System.out.println("Stock level is safe (" + summary.getCurrentStock() + ")");
        }
    }
}