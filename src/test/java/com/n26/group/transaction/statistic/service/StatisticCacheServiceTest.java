package com.n26.group.transaction.statistic.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.n26.group.transaction.statistic.service.StatisticCacheService;

import java.util.DoubleSummaryStatistics;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StatisticCacheServiceTest {

    @InjectMocks
    private
    StatisticCacheService statsCacheService;

    @Test
    public void shouldSaveGivenTimeKeyAndValueAndReturnDoubleSummaryStatistics() {

        DoubleSummaryStatistics doubleSummaryStatistics = statsCacheService.upsertStatsInCache(5678L, 78.0);
        assertThat(doubleSummaryStatistics.getAverage(), is(78.0));
        assertThat(doubleSummaryStatistics.getCount(), is(1L));
        assertThat(doubleSummaryStatistics.getSum(), is(78.0));
        assertThat(doubleSummaryStatistics.getMax(), is(78.0));
        assertThat(doubleSummaryStatistics.getMin(), is(78.0));

    }

    @Test
    public void shouldReturnUpdatedSummaryIfGivenTimeKeyIsAlreadyPresentInStatsBuffer() {
        DoubleSummaryStatistics doubleSummaryStatistics = statsCacheService.upsertStatsInCache(5678L, 78.0);
        statsCacheService.upsertStatsInCache(5678L, 79.0);
        assertThat(doubleSummaryStatistics.getAverage(), is(78.5));
        assertThat(doubleSummaryStatistics.getCount(), is(2L));
        assertThat(doubleSummaryStatistics.getSum(), is(157.0));
        assertThat(doubleSummaryStatistics.getMax(), is(79.0));
        assertThat(doubleSummaryStatistics.getMin(), is(78.0));
    }

    @Test
    public void shouldRemoveTheGivenTimeKeyFromTheStatsBuffer() {
        statsCacheService.upsertStatsInCache(1234L, 7.0);
        statsCacheService.upsertStatsInCache(5678L, 8.0);
        statsCacheService.removeFromCache(1234L);
        assertFalse(statsCacheService.getFromCache(1234L).isDefined());
        assertTrue(statsCacheService.getFromCache(5678L).isDefined());

    }

    @Test
    public void shouldReturnTheAggregateOfAllStatsPresentInTheStatsBuffer(){
        statsCacheService.upsertStatsInCache(1234L, 7.0);
        statsCacheService.upsertStatsInCache(5678L, 8.0);
        statsCacheService.upsertStatsInCache(5678L, 9.0);
        DoubleSummaryStatistics aggregatedDoubleSummaryStats = statsCacheService.aggregate();
        assertThat(aggregatedDoubleSummaryStats.getCount(), is(3L));
        assertThat(aggregatedDoubleSummaryStats.getSum(), is(24.0));
        assertThat(aggregatedDoubleSummaryStats.getAverage(), is(8.0));
        assertThat(aggregatedDoubleSummaryStats.getMin(), is(7.0));
        assertThat(aggregatedDoubleSummaryStats.getMax(), is(9.0));

    }
}