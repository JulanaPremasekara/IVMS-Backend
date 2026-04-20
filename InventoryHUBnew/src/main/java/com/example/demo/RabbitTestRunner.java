//package com.example.demo;
//
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RabbitTestRunner implements CommandLineRunner {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    public RabbitTestRunner(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    @Override
//    public void run(String... args) {
//        rabbitTemplate.convertAndSend("inventory_ai_queue", "threshold is 5");
//        System.out.println("Message sent to RabbitMQ");
//    }
//}