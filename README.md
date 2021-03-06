# n26-stats-api

### Introduction

This is an API using https://projects.spring.io/spring-boot, using Maven for compiling and building the application.

The main use case for this API is to calculate real time statistic from the last 60 seconds. There will be two APIs, one of them is called every time a transaction is made. It is also the sole input of this rest API. The other one returns the statistic based of the transactions of the last 60.

### List of API's

1. ``POST /transactions`` - saves the given transaction if valid for stats summary
2. ``GET /statistics`` - gets the aggregated summary statistics

## Specs

### Transactions

Every Time a new transaction happened, this endpoint will be called.

Where:

* Amount is double specifying transaction amount
* Timestamp is epoch in millis in UTC time zone specifying transaction time

#### Request sample
POST /transactions HTTP/1.1
Content-Type: application/json
```
{
    "amount": 300.6,
    "timestamp": 1512890276038
}
```
#### Success response sample

```
201 Created
```

#### Error response sample - timestamp is older than 60 seconds
```
204 No Content
```

### Statistics
This is the main endpoint of this task, this endpoint have to execute in constant time
and memory (O(1)). It returns the statistic based on the transactions which happened
in the last 60 seconds.

#### Request sample
----
GET /statistics HTTP/1.1
Accept: application/json
----

### Should return:
----
HTTP/1.1 200 OK
Content-Type: application/json
----
----
{
    "sum": 500.8,
    "avg": 500.8,
    "max": 500.8,
    "min": 500.8,
    "count": 1
}
----

Where:

* sum is a double specifying the total sum of transaction value in the last 60 seconds
* avg is a double specifying the average amount of transaction value in the last 60 seconds
* max is a double specifying single highest transaction value in the last 60 seconds
* min is a double specifying single lowest transaction value in the last 60 seconds
* count is a long specifying the total number of transactions happened in the last 60 seconds
	    
### Build
You can build using:

  $ mvn clean install

### Run
You can run using:

  $ mvn spring-boot:run

The REST endpoints from the command line:

    $ curl http://localhost:8080/statistics

