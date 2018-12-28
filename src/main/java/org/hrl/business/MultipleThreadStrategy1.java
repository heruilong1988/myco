package org.hrl.business;

import org.hrl.api.MAsyncRestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleThreadStrategy1 {


    private MAsyncRestClient asyncRestClientPlatformA;
    private MAsyncRestClient asyncRestClientPlatformB;

    private double profitThreshold;
    private long reqIntervalMillis;
    private int maxTradeQtyQuoteCoin;
    private int maxInprogressOrderPairNum;

    public MultipleThreadStrategy1(MAsyncRestClient asyncRestClientPlatformA, MAsyncRestClient asyncRestClientPlatformB,
        double profitThreshold, long reqIntervalMillis, int maxTradeQtyQuoteCoin, int maxInprogressOrderPairNum) {
        this.asyncRestClientPlatformA = asyncRestClientPlatformA;
        this.asyncRestClientPlatformB = asyncRestClientPlatformB;
        this.profitThreshold = profitThreshold;
        this.reqIntervalMillis = reqIntervalMillis;
        this.maxTradeQtyQuoteCoin = maxTradeQtyQuoteCoin;
        this.maxInprogressOrderPairNum = maxInprogressOrderPairNum;
    }

    public void start() {
        String quoteCoin = "usdt";
        //String[] baseCoinArr = {"btc","eth","xrp","trx","ltc","etc","eos"};
        String[] baseCoinArr = {"eos"};

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < baseCoinArr.length; i++) {
            Strategy1 strategy = new Strategy1(asyncRestClientPlatformA, asyncRestClientPlatformB, baseCoinArr[i],
                quoteCoin, profitThreshold, reqIntervalMillis, maxTradeQtyQuoteCoin, maxInprogressOrderPairNum);
            Strategy1Task strategy1Task = new Strategy1Task(strategy);
            executorService.execute(strategy1Task);
        }
    }

}
