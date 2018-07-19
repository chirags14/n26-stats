package com.n26.group.transaction.statistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Chirag Suthar
 * 
 * Main class for Transaction Statistic service
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableAsync
public class TransactionStatisticApplication {

	public static void main(String[] args) {
		System.setProperty("user.timezone", "UTC");
		SpringApplication.run(TransactionStatisticApplication.class, args);
	}
}
