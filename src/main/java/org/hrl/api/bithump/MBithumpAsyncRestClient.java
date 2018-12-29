/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/29
 */

package org.hrl.api.bithump;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.hrl.api.MAsyncRestClient;
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
import org.hrl.domain.ExchangeInfo;

public class MBithumpAsyncRestClient implements MAsyncRestClient{

    ExecutorService executorService = Executors.newFixedThreadPool(300);



    @Override
    public FutureTask<MDepth> depth(String baseCoin, String quoteCoin) {
        return null;
    }

    @Override
    public FutureTask<MPlaceOrderRsp> placeOrder(MPlaceOrderRequest request) {
        return null;
    }

    @Override
    public FutureTask<MQueryOrderRsp> queryOrder(MQueryOrderRequest request) {
        return null;
    }

    @Override
    public FutureTask<MCancelOrderRsp> cancelOrder(MCancelOrderRequest request) {
        return null;
    }

    @Override
    public FutureTask<MGetAccountsRsp> getAccounts() {
        return null;
    }

    @Override
    public FutureTask<MGetBalanceRsp> getBalance(MGetBalanceRequest mGetBalanceRequest) {
        return null;
    }

    @Override
    public ExchangeInfo getExchangeInfo(String baseCoin, String quoteCoin) {
        return null;
    }

    @Override
    public double getTradeFee() {
        return 0;
    }
}
