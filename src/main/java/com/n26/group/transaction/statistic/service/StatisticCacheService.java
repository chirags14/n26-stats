package com.n26.group.transaction.statistic.service;

import javaslang.collection.HashMap;
import javaslang.control.Option;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This component acts as a in-memory cache/buffer for provided statistics data
 */

@Service
public class StatisticCacheService {

    // ConcurrentHashMap is used for storing stats because this cache will be accessed both from incoming live request
    // as well as scheduler which prunes older transactions
    private ConcurrentHashMap<Long, DoubleSummaryStatistics> perSecondStatBuckets = new ConcurrentHashMap<>();


    /**
     * @param timeKey
     * @param value
     * @return save the incoming stats if the timeKey is already present in the statsBucket, else create a new bucket
     */
    DoubleSummaryStatistics upsertStatsInCache(Long timeKey, Double value){

        // try to get the existing timestamp from cache.If the result is Option.Some, it indicates that the key is present in cache
        // where we update the existing stats, else create a new bucket for the new timestamp in cache
        DoubleSummaryStatistics statistics = getFromCache(timeKey)
                .map(existingPerSecStat -> {
                    existingPerSecStat.accept(value);
                    return existingPerSecStat;
                })
                .getOrElse(() -> {
                    DoubleSummaryStatistics newPerSecondStatistic = new DoubleSummaryStatistics();
                    newPerSecondStatistic.accept(value);
                    return newPerSecondStatistic;
                });

        perSecondStatBuckets.put(timeKey, statistics);
        return statistics;
    }

    /**
     * @param timeKey
     * @return get the summary for the specified key from cache if exists else return None monad type
     */
    Option<DoubleSummaryStatistics> getFromCache(Long timeKey){
        return Option.of(perSecondStatBuckets.get(timeKey));
    }


    /**
     * @param timeKey
     * @return removeFromCache the timeKey if present in perSecStatBuckets and return Option.Some
     * if timeKey is not present it returns Option.None
     */
    Option<DoubleSummaryStatistics> removeFromCache(Long timeKey){

        // the operation is wrapped in Option<T> monad, so that even if the function remove throws a NPE due to null key, the
        // resultant output of the function will be None which is more typesafe
        return Option.of(perSecondStatBuckets.remove(timeKey));
    }

    /**
     * @return aggregate all the statistics data (used while recalculation)
     */
    DoubleSummaryStatistics aggregate(){
        return HashMap.ofAll(perSecondStatBuckets)
                .foldLeft(new DoubleSummaryStatistics(), (acc, perSecondStat) -> {
                    acc.combine(perSecondStat._2());
                    return acc;
                });

    }

    /**
     * @return checks if the cache is empty returns true or else false
     */
    boolean isCacheEmpty(){return perSecondStatBuckets.isEmpty();}
}
