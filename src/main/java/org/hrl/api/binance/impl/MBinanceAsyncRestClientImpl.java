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

public class MBinanceAsyncRestClientImpl implements MAsyncRestClient{

    private BinanceApiRestClient binanceApiRestClient;
    private ExecutorService executorService;

    public MBinanceAsyncRestClientImpl(BinanceApiRestClient binanceApiRestClient) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.executorService = Executors.newFixedThreadPool(1000);
    }

    public MBinanceAsyncRestClientImpl(String accesskey, String secretkey) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(accesskey, secretkey);
        this.binanceApiRestClient = factory.newRestClient();
        this.executorService = Executors.newFixedThreadPool(1000);
    }

    public FutureTask<MDepth> depth(String baseCoin, String quoteCoin){
        BinanceDepthTask depthTask = new BinanceDepthTask(binanceApiRestClient, baseCoin, quoteCoin);
        FutureTask<MDepth> futureTask = new FutureTask<>(depthTask);
        executorService.execute(futureTask);

        return futureTask;
    }

    public FutureTask<MPlaceOrderRsp> placeOrder(MPlaceOrderRequest request){

        BinancePlaceOrderTask binancePlaceOrderTask = new BinancePlaceOrderTask(binanceApiRestClient, request);
        FutureTask<MPlaceOrderRsp> futureTask = new FutureTask<>(binancePlaceOrderTask);
        executorService.execute(futureTask);
        return futureTask;

    }

    public FutureTask<MQueryOrderRsp> queryOrder(MQueryOrderRequest request){
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

    public FutureTask<MGetAccountsRsp> getAccounts(){
        BinanceGetAccountsTask binanceGetAccountsTask = new BinanceGetAccountsTask(binanceApiRestClient);
        FutureTask<MGetAccountsRsp> futureTask = new FutureTask<MGetAccountsRsp>(binanceGetAccountsTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public FutureTask<MGetBalanceRsp> getBalance(MGetBalanceRequest mGetBalanceRequest){
        BinanceGetBalanceTask binanceGetBalanceTask = new BinanceGetBalanceTask(binanceApiRestClient);
        FutureTask<MGetBalanceRsp> futureTask = new FutureTask<MGetBalanceRsp>(binanceGetBalanceTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    @Override
    public double getTradeFee() {
        return 0.001;
    }
}
