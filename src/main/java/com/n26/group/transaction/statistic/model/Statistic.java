package com.n26.group.transaction.statistic.model;


import lombok.*;

import java.util.DoubleSummaryStatistics;

@EqualsAndHashCode
@ToString
public class Statistic {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;

    public Statistic(){
    	
    }
    public Statistic(DoubleSummaryStatistics doubleSummaryStatistics){
        this.avg = doubleSummaryStatistics.getAverage();
        this.sum = doubleSummaryStatistics.getSum();
        this.count = doubleSummaryStatistics.getCount();
        this.max = doubleSummaryStatistics.getMax();
        this.min = doubleSummaryStatistics.getMin();
    }
    public Statistic(Double avg,Double sum,Double max,Double min,Long count){
        this.avg = avg;
        this.sum = sum;
        this.max = max;
        this.min = min;
        this.count = count;
        return ;}
    
	public Double getSum() {
		return sum;
	}
	public void setSum(Double sum) {
		this.sum = sum;
	}
	public Double getAvg() {
		return avg;
	}
	public void setAvg(Double avg) {
		this.avg = avg;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
    
}
