package com.example.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerKafkaListener {
    @KafkaListener(topics = "customer", groupId = "foo")
    public void listenToCustomerTopic(String msg) {
        log.info("customer message received: " + msg);

        // Do something
    }
}
