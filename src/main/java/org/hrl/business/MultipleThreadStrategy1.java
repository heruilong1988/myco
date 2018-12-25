package org.hrl.business;

import org.hrl.api.MAsyncRestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleThreadStrategy1 {


    private MAsyncRestClient asyncRestClientPlatformA;
    private MAsyncRestClient asyncRestClientPlatformB;

    private double profitThreshold;
    private long reqIntervalMillis;


    public MultipleThreadStrategy1(MAsyncRestClient asyncRestClientPlatformA, MAsyncRestClient asyncRestClientPlatformB,
        double profitThreshold, long reqIntervalMillis) {
        this.asyncRestClientPlatformA = asyncRestClientPlatformA;
        this.asyncRestClientPlatformB = asyncRestClientPlatformB;
        this.profitThreshold = profitThreshold;
        this.reqIntervalMillis = reqIntervalMillis;
    }

    public void start() {
        String quoteCoin = "usdt";
        //String[] baseCoinArr = {"btc","eth","xrp","trx","ltc","etc","eos"};
        String[] baseCoinArr = {"eos"};

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < baseCoinArr.length; i++) {
            Strategy1 strategy = new Strategy1(asyncRestClientPlatformA, asyncRestClientPlatformB, baseCoinArr[i],
                quoteCoin, profitThreshold, reqIntervalMillis);
            Strategy1DataCollectTask strategy1Task = new Strategy1DataCollectTask(strategy);
            executorService.execute(strategy1Task);
        }
    }

}
