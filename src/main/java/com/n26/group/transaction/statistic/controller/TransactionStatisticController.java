package com.n26.group.transaction.statistic.controller;

import java.time.Instant;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.service.TransactionStatisticService;

/**
 * @author Chirag Suthar
 * 
 * Controller for Transaction Statistic service
 *
 */
@RestController
public class TransactionStatisticController {

	/**
	 * timeInterval --> get value from application.properties config file
	 */
	@Value(value = "${config.n26.transaction.statistic.time.interval}")
	private Long timeInterval;

	private final static Logger LOG = Logger.getLogger(TransactionStatisticController.class.getName());

	@Autowired
	private TransactionStatisticService transactionStatisticService;

	/**
	 * Method to create/POST transactions for statistic service (sole input of this rest API)
	 * @param transaction
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, path = "/transactions")
	public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
		LOG.debug("Recevied request to add transaction...");
		Long epocMillisLimit = Instant.now().toEpochMilli() - timeInterval;
		if (transaction.getTimestamp() < Instant.now().toEpochMilli() && transaction.getTimestamp() > epocMillisLimit) {
			transactionStatisticService.newTransaction(transaction);
			LOG.debug("Transaction completed successfully !!");
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		LOG.debug("Transaction failed !! , Transaction date is older than last 60 seconds");
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * Method to GET statistics based on configured time interval
	 * @param transaction
	 */
	@RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, path = "/statistics")
	public Statistic getTransactionStatistics() {
		LOG.debug("Recevied request to get statistics for last 60 seconds...");
		return transactionStatisticService.getLast60SecondsStats();
	}

}
