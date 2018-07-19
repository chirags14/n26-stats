package com.n26.group.transaction.statistic.util;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransactionStatisticUtils {

    public static final long MILLIS_FOR_ONE_SECOND = 1000;

    public long currentMillis() {
        return Instant.now().toEpochMilli();
    }

    public long convertTimeInMillisToSeconds(long timeInMillis){ return timeInMillis/MILLIS_FOR_ONE_SECOND;}

    public long currentSeconds(){return convertTimeInMillisToSeconds(currentMillis());}


}
