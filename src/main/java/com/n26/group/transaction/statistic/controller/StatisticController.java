package com.n26.group.transaction.statistic.controller;


import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.service.StatisticService;

@RestController
public class StatisticController {
	
	final static Logger LOG = Logger.getLogger(StatisticController.class);

    private StatisticService statsService;

    StatisticController(StatisticService statsService){
        this.statsService = statsService;
    }

    /**
     * @return get stats based on transactions happened in last 60 seconds
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)

    public Statistic getStatsSummary(){
    	LOG.debug("Recevied request to get statistics for last 60 seconds...");
        return statsService.getSummary();
    }
}
