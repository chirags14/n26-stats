package com.n26.group.transaction.statistic.service;

import java.time.Instant;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author chirag suthar
 * 
 * This class will be responsible to delete statics which are 60 seconds old , this process will run every seconds 
 *
 */
@Service
@Component
public class TransactionHousekeepingSerivce {
	
	public static final long MILLIS_FOR_ONE_SECOND = 1000;

	private final static Logger LOG = Logger.getLogger(TransactionHousekeepingSerivce.class.getName());
	
	@Value(value = "${config.n26.transaction.statistic.time.interval}")
	private String timeInterval;

	/**
	 * TransactionStatisticService
	 */
	@Autowired
	TransactionStatisticService transactionStatisticService;

	  /**
	 * Scheduler to delete statics from map
	 */
	@Async
	@Scheduled(fixedDelay = MILLIS_FOR_ONE_SECOND, initialDelay = MILLIS_FOR_ONE_SECOND)
	public void cleanUpTransactions() {
		LOG.debug("Running Transaction housekeeping job to clean up 60 seconds old transactions.");
		transactionStatisticService.cleanUpOldTransactions((Instant.now().toEpochMilli() - Long.valueOf(timeInterval)));
	}

}
