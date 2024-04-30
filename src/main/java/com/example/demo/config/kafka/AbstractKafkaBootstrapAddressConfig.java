package com.example.demo.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the "customer" topic.
 */
@Configuration
public abstract class AbstractKafkaBootstrapAddressConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    protected String bootstrapAddress;
}
