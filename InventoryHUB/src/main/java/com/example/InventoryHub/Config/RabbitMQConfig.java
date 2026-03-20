package com.example.InventoryHub.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "inventory_ai_queue";
    // ADD THESE: The Exchange and Routing Key
    public static final String EXCHANGE_NAME = "inventory_exchange";
    public static final String ROUTING_KEY = "inventory_routing_key";

    @Bean
    public Queue aiQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // ADD THIS: The Exchange (The "Router")
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // ADD THIS: The Binding (Connects the Queue to the Exchange)
    @Bean
    public Binding binding(Queue aiQueue, TopicExchange exchange) {
        return BindingBuilder.bind(aiQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    // Add this inside your RabbitMQConfig class
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}