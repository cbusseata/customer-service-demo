package com.example.demo.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@ConfigurationProperties(prefix = "spring.task")
@ConfigurationPropertiesScan
@Setter
public class AsyncConfig {
    private String threadNamePrefix;
    private Map<String, Integer> execution;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(execution.get("core-size"));
        executor.setMaxPoolSize(execution.get("max-size"));
        executor.setQueueCapacity(execution.get("queue-capacity"));
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();

        return executor;
    }
}
