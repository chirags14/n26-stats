package com.n26.group.transaction.statistic.service;


import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.util.TransactionStatisticUtils;

import javaslang.concurrent.Future;
import javaslang.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TransactionService {

    private StatisticService statsService;

    private TransactionStatisticUtils timeHelperUtility;

    /**
     * @param statsService is responsible for persisting the data and calculating stats summary
     */
    TransactionService(StatisticService statsService, TransactionStatisticUtils timeHelperUtility) {
        this.statsService = statsService;
        this.timeHelperUtility = timeHelperUtility;
    }


    /**
     * @param transaction
     * @return save transaction and return the statistics summary which is a Option<T>
     */
    public Option<Statistic> saveTransaction(Transaction transaction) {

        // check whether the transaction is from last sixty seconds which returns a Option<T>
        // if Option<T> is a success, asynchronously save the stats data using Future abstraction by passing it to stats service layer
        // if Option<T> is a None, signifies it is a older transaction, so return Option.none

        return transaction.isTxnTimeInLastSixtySeconds(timeHelperUtility.currentMillis())
                .flatMap(txn -> Future.of(() -> statsService.saveStats(txn)).toOption())
                .orElse(() -> {
                    //log.info("Skipping transaction: {}", transaction);
                    return Option.none();
                });
    }
}
