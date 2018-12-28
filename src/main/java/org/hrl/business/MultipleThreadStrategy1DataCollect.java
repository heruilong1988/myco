package org.hrl.business;

import org.hrl.api.MAsyncRestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleThreadStrategy1DataCollect {

    private MAsyncRestClient asyncRestClientPlatformA;
    private MAsyncRestClient asyncRestClientPlatformB;
    private double profitThreshold;
    private long reqIntervalMillis;
    private int maxTradeQtyQuoteCoin;
    private int maxInprogressOrderPairNum;

    private String[] baseCoinArr;

    public MultipleThreadStrategy1DataCollect(MAsyncRestClient asyncRestClientPlatformA,
        MAsyncRestClient asyncRestClientPlatformB, String[] baseCoinArr,
        double profitThreshold, long reqIntervalMillis, int maxTradeQtyQuoteCoin, int maxInprogressOrderPairNum) {
        this.asyncRestClientPlatformA = asyncRestClientPlatformA;
        this.asyncRestClientPlatformB = asyncRestClientPlatformB;
        this.baseCoinArr = baseCoinArr;
        this.profitThreshold = profitThreshold;
        this.reqIntervalMillis = reqIntervalMillis;
        this.maxTradeQtyQuoteCoin = maxTradeQtyQuoteCoin;
        this.maxInprogressOrderPairNum = maxInprogressOrderPairNum;
    }

    public void start() {

        //String[] baseCoinArr = {"btc","eth","xrp","trx","ltc","etc","eos"};
        String quoteCoin = "usdt";
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < baseCoinArr.length; i++) {
            Strategy1 strategy = new Strategy1(asyncRestClientPlatformA, asyncRestClientPlatformB, baseCoinArr[i],
                quoteCoin, profitThreshold, reqIntervalMillis, maxTradeQtyQuoteCoin, maxInprogressOrderPairNum);

            Strategy1DataCollectTask strategy1Task = new Strategy1DataCollectTask(strategy);
            executorService.execute(strategy1Task);
        }

    }
}
