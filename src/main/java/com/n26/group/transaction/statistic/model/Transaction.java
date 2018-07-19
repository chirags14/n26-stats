package com.n26.group.transaction.statistic.model;


import com.n26.group.transaction.statistic.util.TransactionStatisticUtils;

import javaslang.control.Option;
import lombok.*;

@EqualsAndHashCode
@ToString
public class Transaction {

    public Transaction(Double amount, Long timestamp) {
		super();
		this.amount = amount;
		this.timestamp = timestamp;
	}

    public Transaction (){
    	
    }
	private static final int MAX_TIME_DIFFERENCE = 60;

    private Double amount;
    private Long timestamp;

    /**
     * @param currentMillis current time in milliseconds
     * @return check whether the transaction timestamp is from the last 60 seconds.
     */
    public Option<Transaction> isTxnTimeInLastSixtySeconds(long currentMillis){
        long diff = (currentMillis - timestamp)/ TransactionStatisticUtils.MILLIS_FOR_ONE_SECOND;
        return (diff > 0 && diff <= MAX_TIME_DIFFERENCE ? Option.of(this) : Option.none());
    }

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
