/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/29
 */

package org.hrl.business;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.rsp.MCurrencyBalance;
import org.hrl.api.rsp.MGetBalanceRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BalanceUpdateScheduler {

    private Logger ORDERLOGGER;
    private Logger LOGGER;

    private MAsyncRestClient mAsyncRestClientPlatformA;
    private MAsyncRestClient mAsyncRestClientPlatformB;
    private Strategy1 strategy1;
    private String baseCoin;
    private String quoteCoin;
    private MGetBalanceRequest mGetBalanceRequest;
    private ScheduledExecutorService scheduledExecutorService;

    public BalanceUpdateScheduler(MAsyncRestClient mAsyncRestClientPlatformA,
        MAsyncRestClient mAsyncRestClientPlatformB, Strategy1 strategy1) {
        this.mAsyncRestClientPlatformA = mAsyncRestClientPlatformA;
        this.mAsyncRestClientPlatformB = mAsyncRestClientPlatformB;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
        this.strategy1 = strategy1;
        this.baseCoin = strategy1.getBaseCoin();
        this.quoteCoin = strategy1.getQuoteCoin();
        mGetBalanceRequest = new MGetBalanceRequest();
        mGetBalanceRequest.setBaseCoin(baseCoin);
        mGetBalanceRequest.setQuoteCoin(quoteCoin);

        LOGGER = LoggerFactory.getLogger(baseCoin);
        ORDERLOGGER = LoggerFactory.getLogger(baseCoin + ".Order");
    }

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                FutureTask<MGetBalanceRsp> mGetBalanceRspFutureTaskPlatformA = mAsyncRestClientPlatformA
                    .getBalance(mGetBalanceRequest);

                FutureTask<MGetBalanceRsp> mGetBalanceRspFutureTaskPlatformB = mAsyncRestClientPlatformB
                    .getBalance(mGetBalanceRequest);

                try {
                    MGetBalanceRsp mGetBalanceRspPlatformA = mGetBalanceRspFutureTaskPlatformA.get();

                    Map<String, MCurrencyBalance> currencyBalanceMap = mGetBalanceRspPlatformA.getmBalance()
                        .getmCurrencyBalanceMap();
                    double baseCoinQuantityPlatformA = currencyBalanceMap.get(baseCoin).getTradeBalance();

                    double quoteCoinQuantityPlatformA = currencyBalanceMap.get(quoteCoin).getTradeBalance();

                    strategy1.setAccountBalanceBaseCoinPlatformA(baseCoinQuantityPlatformA);
                    strategy1.setAccountBalanceQuoteCoinPlatformA(quoteCoinQuantityPlatformA);

                    LOGGER.info(
                        "setAccountBalanceBaseCoinPlatformA:{},setAccountBalanceQuoteCoinPlatformA:{},platformA:{}",
                        baseCoinQuantityPlatformA, quoteCoinQuantityPlatformA, mAsyncRestClientPlatformA);

                } catch (InterruptedException e) {
                    LOGGER.info("fail to update balance of platformA:{}", mAsyncRestClientPlatformA);
                } catch (ExecutionException e) {
                    LOGGER.info("fail to update balance of platformA:{}", mAsyncRestClientPlatformA);
                }

                try {
                    MGetBalanceRsp mGetBalanceRspPlatformB = mGetBalanceRspFutureTaskPlatformB.get();

                    Map<String, MCurrencyBalance> currencyBalanceMap = mGetBalanceRspPlatformB.getmBalance()
                        .getmCurrencyBalanceMap();
                    double baseCoinQuantityPlatformB = currencyBalanceMap.get(baseCoin).getTradeBalance();

                    double quoteCoinQuantityPlatformB = currencyBalanceMap.get(quoteCoin).getTradeBalance();

                    strategy1.setAccountBalanceBaseCoinPlatformB(baseCoinQuantityPlatformB);
                    strategy1.setAccountBalanceQuoteCoinPlatformB(quoteCoinQuantityPlatformB);

                    LOGGER.info(
                        "setAccountBalanceBaseCoinPlatformB:{},setAccountBalanceQuoteCoinPlatformB:{},platformB:{}",
                        baseCoinQuantityPlatformB, quoteCoinQuantityPlatformB, mAsyncRestClientPlatformA);

                } catch (InterruptedException e) {
                    LOGGER.info("fail to update balance of platformB:{}", mAsyncRestClientPlatformB);
                } catch (ExecutionException e) {
                    LOGGER.info("fail to update balance of platformB:{}", mAsyncRestClientPlatformB);
                }

            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
