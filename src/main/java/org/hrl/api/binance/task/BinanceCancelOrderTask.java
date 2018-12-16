/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/12
 */

package org.hrl.api.binance.task;

import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.domain.account.request.CancelOrderRequest;
import org.hrl.api.binance.domain.account.request.CancelOrderResponse;
import org.hrl.api.binance.util.BinanceUtils;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.rsp.MCancelOrderRsp;
import java.util.concurrent.Callable;

public class BinanceCancelOrderTask implements Callable<MCancelOrderRsp> {

    private BinanceApiRestClient binanceApiRestClient;
    private MCancelOrderRequest mCancelOrderRequest;

    public BinanceCancelOrderTask(BinanceApiRestClient binanceApiRestClient,
        MCancelOrderRequest mCancelOrderRequest) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.mCancelOrderRequest = mCancelOrderRequest;
    }

    @Override
    public MCancelOrderRsp call() throws Exception {

        String orderId = mCancelOrderRequest.getOrderId();

        String symbol = BinanceUtils.toSymbol(mCancelOrderRequest.getBaseCoin(), mCancelOrderRequest.getQuoteCoin());

        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest(symbol, Long.parseLong(orderId));
        CancelOrderResponse cancelOrderResponse = binanceApiRestClient.cancelOrder(cancelOrderRequest);

        MCancelOrderRsp mCancelOrderRsp = new MCancelOrderRsp();
        mCancelOrderRsp.setOrderId(cancelOrderResponse.getOrderId());
        mCancelOrderRsp.setClientOrderId(cancelOrderResponse.getClientOrderId());
        mCancelOrderRsp.setOriClientOrderId(cancelOrderResponse.getOrigClientOrderId());
        mCancelOrderRsp.setSymbol(cancelOrderResponse.getSymbol());

        return mCancelOrderRsp;
    }
}
