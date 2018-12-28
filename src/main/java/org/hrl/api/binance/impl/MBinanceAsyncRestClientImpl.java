/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/11
 */

package org.hrl.api.binance.impl;


import org.hrl.api.MAsyncRestClient;
import org.hrl.api.binance.BinanceApiClientFactory;
import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.task.BinanceCancelOrderTask;
import org.hrl.api.binance.task.BinanceDepthTask;
import org.hrl.api.binance.task.BinanceGetAccountsTask;
import org.hrl.api.binance.task.BinanceGetBalanceTask;
import org.hrl.api.binance.task.BinancePlaceOrderTask;
import org.hrl.api.binance.task.BinanceQueryOrderTask;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.MCancelOrderRsp;
import org.hrl.api.rsp.MDepth;
import org.hrl.api.rsp.MGetAccountsRsp;
import org.hrl.api.rsp.MGetBalanceRsp;
import org.hrl.api.rsp.MPlaceOrderRsp;
import org.hrl.api.rsp.MQueryOrderRsp;
import org.hrl.config.PrecisionConfig;
import org.hrl.domain.ExchangeInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


/*
"rateLimits": [
        {
            "rateLimitType": "REQUEST_WEIGHT",
            "interval": "MINUTE",
            "intervalNum": 1,
            "limit": 1200
        },
        {
            "rateLimitType": "ORDERS",
            "interval": "SECOND",
            "intervalNum": 1,
            "limit": 10
        },
        {
            "rateLimitType": "ORDERS",
            "interval": "DAY",
            "intervalNum": 1,
            "limit": 100000
        }
    ]
 */

@Component
public class MBinanceAsyncRestClientImpl implements MAsyncRestClient {

    private BinanceApiRestClient binanceApiRestClient;
    private ExecutorService executorService;

    private PrecisionConfig precisionConfig;

    private Map<String, ExchangeInfo> symbolExchangeInfoMap = new HashMap<>();

    /*
    public MBinanceAsyncRestClientImpl(BinanceApiRestClient binanceApiRestClient) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.executorService = Executors.newFixedThreadPool(300);
    }
    */

    public MBinanceAsyncRestClientImpl(@Value("${binance-accesskey}") String accesskey, @Value("${binance-secretkey}") String secretkey,
                                       @Autowired PrecisionConfig precisionConfig) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(accesskey, secretkey);
        this.binanceApiRestClient = factory.newRestClient();
        this.executorService = Executors.newFixedThreadPool(300);
        this.precisionConfig = precisionConfig;
    }

    @PostConstruct
    public void init() {
        JSONArray precisionArr = new JSONObject(precisionConfig.getBinancePrecisionStr()).getJSONArray("symbols");
        for (int i = 0; i < precisionArr.length(); i++) {

            JSONObject symbolExchangeInfoJson = precisionArr.getJSONObject(i);

            if(symbolExchangeInfoJson.getString("baseAsset").equalsIgnoreCase("qtum")) {

                JSONArray filterArray = symbolExchangeInfoJson.getJSONArray("filters");
                JSONObject priceFilter = filterArray.getJSONObject(0);
                double priceFilterTickSize = priceFilter.getDouble("tickSize");

                JSONObject lotSize = filterArray.getJSONObject(2);
                double lotSizeStepSize = lotSize.getDouble("stepSize");

                ExchangeInfo exchangeInfo = new ExchangeInfo();

                exchangeInfo.setQuantityPrecision(calcPrecision(lotSizeStepSize));
                exchangeInfo.setPricePrecision(calcPrecision(priceFilterTickSize));

                String baseCurrency = symbolExchangeInfoJson.getString("baseAsset").toLowerCase();
                String quoteCurrency = symbolExchangeInfoJson.getString("quoteAsset").toLowerCase();

                symbolExchangeInfoMap.put(baseCurrency + quoteCurrency, exchangeInfo);
            }
        }
        System.out.println("binance init finished");
    }

    private int calcPrecision(double num) {

        if (num > 1) {
            return 0;
        }

        int precision = 0;
        while (num < 1) {
            precision++;
            num = num * 10;
        }
        return precision;
    }

    public FutureTask<MDepth> depth(String baseCoin, String quoteCoin) {
        BinanceDepthTask depthTask = new BinanceDepthTask(binanceApiRestClient, baseCoin, quoteCoin);
        FutureTask<MDepth> futureTask = new FutureTask<>(depthTask);
        executorService.execute(futureTask);

        return futureTask;
    }

    public FutureTask<MPlaceOrderRsp> placeOrder(MPlaceOrderRequest request) {

        BinancePlaceOrderTask binancePlaceOrderTask = new BinancePlaceOrderTask(binanceApiRestClient, request);
        FutureTask<MPlaceOrderRsp> futureTask = new FutureTask<>(binancePlaceOrderTask);
        executorService.execute(futureTask);
        return futureTask;

    }

    public FutureTask<MQueryOrderRsp> queryOrder(MQueryOrderRequest request) {
        BinanceQueryOrderTask binanceQueryOrderTask = new BinanceQueryOrderTask(binanceApiRestClient, request);
        FutureTask<MQueryOrderRsp> futureTask = new FutureTask<MQueryOrderRsp>(binanceQueryOrderTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public FutureTask<MCancelOrderRsp> cancelOrder(MCancelOrderRequest request) {
        BinanceCancelOrderTask binanceCancelOrderTask = new BinanceCancelOrderTask(binanceApiRestClient, request);
        FutureTask<MCancelOrderRsp> futureTask = new FutureTask<MCancelOrderRsp>(binanceCancelOrderTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public FutureTask<MGetAccountsRsp> getAccounts() {
        BinanceGetAccountsTask binanceGetAccountsTask = new BinanceGetAccountsTask(binanceApiRestClient);
        FutureTask<MGetAccountsRsp> futureTask = new FutureTask<MGetAccountsRsp>(binanceGetAccountsTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public FutureTask<MGetBalanceRsp> getBalance(MGetBalanceRequest mGetBalanceRequest) {
        BinanceGetBalanceTask binanceGetBalanceTask = new BinanceGetBalanceTask(binanceApiRestClient);
        FutureTask<MGetBalanceRsp> futureTask = new FutureTask<MGetBalanceRsp>(binanceGetBalanceTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    @Override
    public ExchangeInfo getExchangeInfo(String baseCoin, String quoteCoin) {
        return symbolExchangeInfoMap.get(baseCoin + quoteCoin);
    }

    @Override
    public double getTradeFee() {
        return 0.001;
    }
}
