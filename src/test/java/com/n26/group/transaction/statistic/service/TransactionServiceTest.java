package com.n26.group.transaction.statistic.service;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.service.StatisticService;
import com.n26.group.transaction.statistic.service.TransactionService;
import com.n26.group.transaction.statistic.util.TransactionStatisticUtils;

import javaslang.control.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.DoubleSummaryStatistics;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    private TransactionStatisticUtils timeHelperUtilityMock;

    @Mock
    private StatisticService statsService;


    @InjectMocks
    private
    TransactionService transactionService;

    @Test
    public void shouldSaveAValidTransactionSuccessfully() {

        Transaction validTransaction = new Transaction(43.8, 1525937802000L);
        when(timeHelperUtilityMock.currentMillis()).thenReturn(1525937817000L);
        when(statsService.saveStats(validTransaction)).thenReturn(new Statistic(new DoubleSummaryStatistics()));
        Option<Statistic> maybeStatSummary = transactionService.saveTransaction(validTransaction);
        assertTrue(maybeStatSummary.isDefined());
    }

    @Test
    public void shouldReturnNoneMonadAndNotSaveTheTransactionIfTransactionIsOlder() {

        Transaction inValidTransaction = new Transaction(46.8, 1525937802000L);
        when(timeHelperUtilityMock.currentMillis()).thenReturn(1525938021000L);
        verify(statsService, times(0)).saveStats(inValidTransaction);
        Option<Statistic> maybeStatSummary = transactionService.saveTransaction(inValidTransaction);
        assertFalse(maybeStatSummary.isDefined());
    }


}