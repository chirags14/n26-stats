package com.n26.group.transaction.statistic.service;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.service.StatisticCacheService;
import com.n26.group.transaction.statistic.service.StatisticService;
import com.n26.group.transaction.statistic.util.TransactionStatisticUtils;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.DoubleSummaryStatistics;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticServiceTest {

    private StatisticService statsService;

    private TransactionStatisticUtils timeHelperUtility;

    @Before
    public void setUp() {
        timeHelperUtility = mock(TransactionStatisticUtils.class);
        statsService = new StatisticService(new StatisticCacheService(), timeHelperUtility);
    }

    @Test
    public void shouldBeAbleToSaveTheIncomingTransactionDataAndReturnUpdatedStatsSummary() {

        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Transaction transaction = new Transaction(45.0, 12345000L);
        Statistic statsSummary = statsService.saveStats(transaction);

        assertThat(statsSummary.getCount(), is(1L));
        assertThat(statsSummary.getAvg(), is(45.0));
        assertThat(statsSummary.getSum(), is(45.0));
        assertThat(statsSummary.getMin(), is(45.0));
        assertThat(statsSummary.getMax(), is(45.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryGivenThereAreAlreadyTransactionsInBuffer() {

        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Transaction transaction1 = new Transaction(45.0, 12345000L);
        Transaction transaction2 = new Transaction(47.0, 12345000L);
        Transaction transaction3 = new Transaction(49.0, 12345000L);
        statsService.saveStats(transaction1);
        statsService.saveStats(transaction2);
        Statistic statsSummary = statsService.saveStats(transaction3);

        assertThat(statsSummary.getCount(), is(3L));
        assertThat(statsSummary.getAvg(), is(47.0));
        assertThat(statsSummary.getSum(), is(141.0));
        assertThat(statsSummary.getMin(), is(45.0));
        assertThat(statsSummary.getMax(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnUpdatedSummaryIfThereAreOlderTransactionsThan60SecondsWhichAreToBeCleaned() {

        when(timeHelperUtility.convertTimeInMillisToSeconds(12288000L)).thenReturn(12288L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeHelperUtility.currentSeconds()).thenReturn(12348L);

        Transaction oldTransaction = new Transaction(45.0, 12288000L);
        Transaction newTransactionOne = new Transaction(47.0, 12345000L);
        Transaction newTransactionTwo = new Transaction(49.0, 12345000L);
        statsService.saveStats(oldTransaction);
        statsService.saveStats(newTransactionOne);
        Statistic statsSummary = statsService.saveStats(newTransactionTwo);

        DoubleSummaryStatistics updatedSummaryStatistics = statsService.cleanOldStatsPerSecond();

        assertThat(updatedSummaryStatistics.getCount(), is(2L));
        assertThat(updatedSummaryStatistics.getAverage(), is(48.0));
        assertThat(updatedSummaryStatistics.getSum(), is(96.0));
        assertThat(updatedSummaryStatistics.getMin(), is(47.0));
        assertThat(updatedSummaryStatistics.getMax(), is(49.0));
    }

    @Test
    public void shouldBeAbleToReturnOriginalSummaryIfThereAreNoOlderTransactionsThan60SecondsWhichAreToBeCleaned() {

        when(timeHelperUtility.convertTimeInMillisToSeconds(12346000L)).thenReturn(12346L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeHelperUtility.currentSeconds()).thenReturn(12348L);

        Transaction oldTransaction = new Transaction(45.0, 12346000L);
        Transaction newTransactionOne = new Transaction(47.0, 12345000L);
        Transaction newTransactionTwo = new Transaction(49.0, 12345000L);
        statsService.saveStats(oldTransaction);
        statsService.saveStats(newTransactionOne);
        Statistic statsSummary = statsService.saveStats(newTransactionTwo);

        DoubleSummaryStatistics updatedSummaryStatistics = statsService.cleanOldStatsPerSecond();

        assertThat(updatedSummaryStatistics.getCount(), is(3L));
        assertThat(updatedSummaryStatistics.getAverage(), is(47.0));
        assertThat(updatedSummaryStatistics.getSum(), is(141.0));
        assertThat(updatedSummaryStatistics.getMin(), is(45.0));
        assertThat(updatedSummaryStatistics.getMax(), is(49.0));
    }

    @Test
    public void shouldReturnDefaultStatsIfThereAreNoTransactionsInCache(){
        Statistic statsSummary = statsService.getSummary();

        assertThat(statsSummary.getCount(), CoreMatchers.is(0L));
        assertThat(statsSummary.getSum(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getAvg(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getMax(), CoreMatchers.is(0.0));
        assertThat(statsSummary.getMin(), CoreMatchers.is(0.0));
    }

    @Test
    public void shouldBeAbleToReturnSummaryAsPerDataFromCache() {

        when(timeHelperUtility.convertTimeInMillisToSeconds(12346000L)).thenReturn(12288L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);
        when(timeHelperUtility.convertTimeInMillisToSeconds(12345000L)).thenReturn(12345L);

        Transaction oldTransaction = new Transaction(45.0, 12346000L);
        Transaction newTransactionOne = new Transaction(47.0, 12345000L);
        Transaction newTransactionTwo = new Transaction(49.0, 12345000L);
        statsService.saveStats(oldTransaction);
        statsService.saveStats(newTransactionOne);
        Statistic statsSummary = statsService.saveStats(newTransactionTwo);

        assertThat(statsSummary.getCount(), is(3L));
        assertThat(statsSummary.getAvg(), is(47.0));
        assertThat(statsSummary.getSum(), is(141.0));
        assertThat(statsSummary.getMin(), is(45.0));
        assertThat(statsSummary.getMax(), is(49.0));
    }
}