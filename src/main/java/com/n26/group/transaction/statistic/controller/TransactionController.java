package com.n26.group.transaction.statistic.controller;


import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.service.TransactionService;

import javaslang.control.Option;
import lombok.extern.slf4j.Slf4j;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TransactionController {
	
	
	final static Logger LOG = Logger.getLogger(TransactionController.class);

    private TransactionService transactionService;

    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    /**
     * @param transaction
     * @return saves the incoming transaction data
     */

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity saveTransaction(@RequestBody Transaction transaction){
    	LOG.debug("Recevied request to add transaction...");
        // save the incoming transaction
        Option<Statistic> maybeStatsSummary = transactionService.saveTransaction(transaction);

        // if transaction is saved successfully based on constraints then return 201 or else return 204
        return maybeStatsSummary.map(summary -> new ResponseEntity(HttpStatus.CREATED))
                .getOrElse(new ResponseEntity(HttpStatus.NO_CONTENT));

    }

}
