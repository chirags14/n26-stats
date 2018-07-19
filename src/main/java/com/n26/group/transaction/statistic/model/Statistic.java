package com.n26.group.transaction.statistic.model;

import java.util.DoubleSummaryStatistics;

public class Statistic {

	double sum;

	double avg;

	double max;

	double min;

	long count;
	
	 public Statistic(DoubleSummaryStatistics doubleSummaryStatistics){
	        this.avg = doubleSummaryStatistics.getAverage();
	        this.sum = doubleSummaryStatistics.getSum();
	        this.count = doubleSummaryStatistics.getCount();
	        this.max = doubleSummaryStatistics.getMax();
	        this.min = doubleSummaryStatistics.getMin();
	    }

	public Statistic() {
	}

	public Statistic(double sum, double avg, double max, double min, long count) {
		this.sum= sum;
		this.avg=avg;
		this.max=max;
		this.min=min;
		this.count=count;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

}
