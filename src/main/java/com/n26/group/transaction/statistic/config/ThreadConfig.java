package com.n26.group.transaction.statistic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration

public class ThreadConfig {

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(25);
        executor.setMaxPoolSize(25);
        executor.setThreadNamePrefix("n26_task_executor_thread");
        executor.initialize();
        return executor;
    }
}
