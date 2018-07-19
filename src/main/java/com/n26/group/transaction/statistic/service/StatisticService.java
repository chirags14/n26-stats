package com.n26.group.transaction.statistic.service;

import java.util.DoubleSummaryStatistics;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.util.TransactionStatisticUtils;

@Service
public class StatisticService {
	
	final static Logger LOG = Logger.getLogger(StatisticService.class);

	private StatisticCacheService statsCacheService;
	private TransactionStatisticUtils timeHelperUtility;
	private DoubleSummaryStatistics statsSummary;

	StatisticService(StatisticCacheService statsCacheService, TransactionStatisticUtils timeHelperUtility) {
		this.statsCacheService = statsCacheService;
		this.timeHelperUtility = timeHelperUtility;
		this.statsSummary = new DoubleSummaryStatistics();
	}

	/**
	 * @return get stats based on transactions happened in last 60 seconds.If the
	 *         cache is empty, returns the default stats
	 */
	public Statistic getSummary() {
		if (statsCacheService.isCacheEmpty()) {
			Statistic statsSummary = new Statistic();
			statsSummary.setAvg(0.0);
			statsSummary.setMax(0.0);
			statsSummary.setMin(0.0);
			statsSummary.setSum(0.0);
			statsSummary.setCount(0L);
			return new Statistic(0.0, 0.0, 0.0, 0.0, 0L);
		} else {
			return new Statistic(statsSummary);
		}
	}

	/**
	 * @param transaction
	 * @return save the incoming transaction data and calculate the updated stats
	 *         summary At the same time, update the cache with the new transaction
	 */
	Statistic saveStats(Transaction transaction) {

		// this operation is wrapped in synchronized block as statsSummary object is
		// even accessed by the scheduled job
		// which is responsible for cleaning old transactions
		synchronized (this) {
			statsSummary.accept(transaction.getAmount());
		}

		Statistic updatedStatsSummary = new Statistic(statsSummary);

		// update the per second stats buffer with the new transaction
		statsCacheService.upsertStatsInCache(timeHelperUtility.convertTimeInMillisToSeconds(transaction.getTimestamp()),
				transaction.getAmount());

		return updatedStatsSummary;
	}

	/**
	 * @return this is a scheduled job which runs every second to clean/prune
	 *         transactions which are older than 60 seconds from the current time.
	 *         This is a async task which runs in a separate thread.
	 */
	@Async
	@Scheduled(fixedDelay = TransactionStatisticUtils.MILLIS_FOR_ONE_SECOND, initialDelay = TransactionStatisticUtils.MILLIS_FOR_ONE_SECOND)
	public DoubleSummaryStatistics cleanOldStatsPerSecond() {
		long nowInSeconds = timeHelperUtility.currentSeconds();
		LOG.info("Job running to clean older transactions. current time: {} "+nowInSeconds);
		long oldestTimeInSeconds = nowInSeconds - 60;

		// remove the entry from cache which is older than last sixty seconds if present
		// which will return Option.Some on which
		// we recalculate the stats again, else if the timeKey is not present it returns
		// None on which we do nothing
		return statsCacheService.removeFromCache(oldestTimeInSeconds).map(a -> {
			synchronized (this) {
				LOG.info("ReCalculating stats summary");
				statsSummary = statsCacheService.aggregate();
			}
			LOG.info("updated aggregated stats: {}"+ new Statistic(statsSummary));
			// log.info("updated aggregated stats: {}", new StatsSummary(statsSummary));
			return statsSummary;
		}).getOrElse(() -> {
			LOG.info("stats older than 60 seconds not found for cleaning");
			return statsSummary;
		});
	}

}
