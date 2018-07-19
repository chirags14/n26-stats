package com.n26.group.transaction.statistic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

import com.n26.group.transaction.statistic.model.Statistic;
import com.n26.group.transaction.statistic.model.Transaction;
import com.n26.group.transaction.statistic.service.TransactionHousekeepingSerivce;
import com.n26.group.transaction.statistic.service.TransactionStatisticService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatisticTest {

	private static final String TRANSACTION_URL = "/transactions";

	private static final String STATISTIC_URL = "/statistics";
	
	@Autowired
	private TransactionStatisticService transactionStatisticService;
	
	@Value(value = "${config.n26.transaction.statistic.time.interval}")
	private String timeInterval;

	@Autowired
	private TestRestTemplate restTemplate;

	private void postTransaction(Transaction t, HttpStatus status) {
		ResponseEntity<String> response = this.restTemplate.postForEntity(TRANSACTION_URL, t, String.class);
		assertThat("Status unexpected", response.getStatusCode(), is(status));
	}

	private Statistic getStatistic(HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(headers);
		ResponseEntity<Statistic> response = restTemplate.exchange(STATISTIC_URL, HttpMethod.GET, entity,
				Statistic.class);

		assertThat("Status unexpected", response.getStatusCode(), is(status));
		return response.getBody();
	}

	@Test
	public void testValidTransaction() {
		Transaction txn1 = new Transaction();
		txn1.setAmount(new Double("300.00"));
		txn1.setTimestamp(Instant.now().toEpochMilli());

		Transaction txn2 = new Transaction();
		txn2.setAmount(new Double("302.00"));
		txn2.setTimestamp(Instant.now().toEpochMilli());

		postTransaction(txn1, HttpStatus.CREATED);
		postTransaction(txn2, HttpStatus.CREATED);

		Statistic statistic = getStatistic(HttpStatus.OK);
		assertStats(statistic, new Double("302.0000"), new Double("300.0000"), new Double("301.0000"), 2L,
				new Double("602.0000"));
	}

	@Test
	public void testOldOrEmptyTransactions() {
		transactionStatisticService.cleanUpOldTransactions((Instant.now().toEpochMilli() + Long.valueOf(timeInterval)));
		Transaction txn1 = new Transaction();
		txn1.setAmount(new Double("300.00"));
		txn1.setTimestamp(Instant.now().toEpochMilli() - (Long.valueOf(timeInterval) + 10));

		postTransaction(txn1, HttpStatus.NO_CONTENT);
		Statistic statistic = getStatistic(HttpStatus.OK);
		assertStats(statistic, 0.0, 0.0, 0.0, 0L, 0.0);
	}

	@Test
	public void testOldButValidTransaction() {
		transactionStatisticService.cleanUpOldTransactions((Instant.now().toEpochMilli() + Long.valueOf(timeInterval)));
		Double testAmount = new Double("305.00");

		Transaction txn1 = new Transaction();
		txn1.setAmount(new Double("305.00"));
		txn1.setTimestamp(Instant.now().toEpochMilli() - (Long.valueOf(timeInterval) - 10000));

		postTransaction(txn1, HttpStatus.CREATED);
		Statistic statistic = getStatistic(HttpStatus.OK);
		assertStats(statistic, testAmount, testAmount, testAmount, 1L, testAmount);
	}

	@Test
	public void testDeleteProcess() throws InterruptedException {
		transactionStatisticService.cleanUpOldTransactions((Instant.now().toEpochMilli() + Long.valueOf(timeInterval)));
		Transaction txn1 = new Transaction();
		txn1.setAmount(new Double("300.00"));
		txn1.setTimestamp(Instant.now().toEpochMilli() - (Long.valueOf(timeInterval) - 3000));

		Transaction txn2 = new Transaction();
		txn2.setAmount(new Double("302.00"));
		txn2.setTimestamp(Instant.now().toEpochMilli() - (Long.valueOf(timeInterval) - 2000));

		postTransaction(txn1, HttpStatus.CREATED);
		postTransaction(txn2, HttpStatus.CREATED);
		Thread.sleep(4000);
		Statistic statistic = getStatistic(HttpStatus.OK);
		assertStats(statistic, 0.0, 0.0, 0.0, 0L, 0.0);
	}

	private void assertStats(Statistic statistic, Double max, Double min, Double avg, Long count, Double sum) {
		assertThat("Max not expected", statistic.getMax(), is(max));
		assertThat("Min not expected", statistic.getMin(), is(min));
		assertThat("Avg not expected", statistic.getAvg(), is(avg));
		assertThat("Count not expected", statistic.getCount(), is(count));
		assertThat("Sum not expected", statistic.getSum(), is(sum));
	}

}
