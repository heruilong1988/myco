/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/29
 */

package org.hrl.api.binance.task;

import java.util.List;
import java.util.concurrent.Callable;
import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.domain.account.Order;
import org.hrl.api.binance.domain.account.request.OrderRequest;
import org.hrl.api.binance.util.BinanceUtils;
import org.hrl.api.req.MQueryOpenOrderRequest;
import org.hrl.api.rsp.MQueryOrderRsp;

public class BinanceQueryOpenOrdersTask implements Callable<MQueryOrderRsp> {

    private BinanceApiRestClient binanceApiRestClient;
    private MQueryOpenOrderRequest mQueryOpenOrderRequest;


    @Override
    public MQueryOrderRsp call() throws Exception {
        String symbol = BinanceUtils
            .toSymbol(mQueryOpenOrderRequest.getBaseCoin(), mQueryOpenOrderRequest.getQuoteCoin());

        OrderRequest orderRequest = new OrderRequest(symbol);

        List<Order> orders = binanceApiRestClient.getOpenOrders(orderRequest);


        return null;
    }
}
