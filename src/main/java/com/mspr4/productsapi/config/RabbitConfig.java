package com.mspr4.productsapi.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PRODUCT_QUEUE = "productQueue";

    @Bean
    public Queue productQueue() {
        return new Queue(PRODUCT_QUEUE, true);
    }
}