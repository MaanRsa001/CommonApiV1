package com.maan.eway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "customTaskExecutor")
    public Executor customTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // minimum threads always alive
        executor.setMaxPoolSize(20);   // maximum threads
        executor.setKeepAliveSeconds(120); // idle threads above core die after 2 min
        executor.setQueueCapacity(100); // tasks waiting queue
        executor.setThreadNamePrefix("My-Custom-Task-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "mtpTaskExecutor")
    public Executor mtpTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("mtp-async-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "mtpPollingExecutor")
    public Executor mtpPollingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mtp-poll-");
        executor.initialize();
        return executor;
    }
}
