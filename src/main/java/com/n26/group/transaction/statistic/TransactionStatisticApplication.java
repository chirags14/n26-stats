package com.n26.group.transaction.statistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class TransactionStatisticApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionStatisticApplication.class, args);
    }
}
