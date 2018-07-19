package com.n26.group.transaction.statistic.service;

import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;

import javaslang.collection.HashMap;
import javaslang.control.Option;

/**
 * @author chirag suthar
 * 
 *         TransactionStatisticService : this class will be responsible to add
 *         ,get and delete transaction statics for Statics servcie.
 *
 */
@Service
public class TransactionStatisticService {

	/**
	 * Logger
	 */
	private final static Logger LOG = Logger.getLogger(TransactionHousekeepingSerivce.class.getName());

	/**
	 * This is the time interval for statics service and can be changes as required
	 */
	@Value(value = "${config.n26.transaction.statistic.time.interval}")
	private String timeInterval;

	// ConcurrentHashMap is used for storing stats because this cache will be
	// accessed both from incoming live request
	// as well as scheduler which prunes older transactions
	private ConcurrentHashMap<Long, DoubleSummaryStatistics> transactionStaticsHolder = new ConcurrentHashMap<>();

	/**
	 * DoubleSummaryStatistics to hold statics
	 */
	DoubleSummaryStatistics statsSummary;

	/**
	 * @param transaction
	 *            Method to create new transaction
	 */
	public void newTransaction(Transaction transaction) {
		double value = transaction.getAmount();
		// synchronized DoubleSummaryStatistics as it will also be used for clean up
		// operation
		synchronized (this) {
			if (statsSummary == null) {
				synchronized (this) {
					statsSummary = new DoubleSummaryStatistics();
				}
			}
			statsSummary.accept(value);
		}
		// update Statistic
		new Statistic(statsSummary);
		transactionStaticsHolder.put(transaction.getTimestamp(), statsSummary);
	}

	/**
	 * This method will be responsible to get last 60 seconds statics
	 * 
	 * @return
	 */
	public Statistic getLast60SecondsStats() {
		// Check if transactionStaticsHolder empty or not , if its empty there are no
		// recent tranction in given timeinterval i.e 60 seconds
		if (transactionStaticsHolder.isEmpty()) {
			return new Statistic(0.0, 0.0, 0.0, 0.0, 0L);
		} else {
			return new Statistic(statsSummary);
		}

	}

	/**
	 * This method will be used to clean up and aggregate DoubleSummaryStatistics
	 * from transactionStaticsHolder map
	 * 
	 * @param time
	 */
	public void cleanUpOldTransactions(Long time) {
		// remove the entry from cache which is older than last sixty seconds if present
		// which will return Option.Some on which
		// we recalculate the stats again, else if the timeKey is not present it returns
		// None on which we do nothing
		for (Map.Entry<Long, DoubleSummaryStatistics> entry : transactionStaticsHolder.entrySet()) {
			if (entry.getKey() < time) {
				LOG.info("Found Transaction older than 60 Sec hence it will be deleted ");
				Option<DoubleSummaryStatistics> doubleSummaryStatistics = Option
						.of(transactionStaticsHolder.remove(entry.getKey()));
				doubleSummaryStatistics.map(a -> {
					synchronized (this) {
						statsSummary = aggregate();
					}
					return statsSummary;
				});
			}
		}
	}

	/**
	 * @return aggregate all the statistics data (used while recalculation)
	 */
	DoubleSummaryStatistics aggregate() {
		return HashMap.ofAll(transactionStaticsHolder).foldLeft(new DoubleSummaryStatistics(), (acc, perSecondStat) -> {
			acc.combine(perSecondStat._2());
			return acc;
		});

	}
}
