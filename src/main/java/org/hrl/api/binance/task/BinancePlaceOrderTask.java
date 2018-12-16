/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/11
 */

package org.hrl.api.binance.task;

import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.constant.BinanceApiConstants;
import org.hrl.api.binance.domain.OrderSide;
import org.hrl.api.binance.domain.OrderType;
import org.hrl.api.binance.domain.TimeInForce;
import org.hrl.api.binance.domain.account.NewOrder;
import org.hrl.api.binance.domain.account.NewOrderResponse;
import org.hrl.api.binance.domain.account.Trade;
import org.hrl.api.binance.util.BinanceUtils;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderStatus;
import org.hrl.api.rsp.MOrderType;
import org.hrl.api.rsp.MPlaceOrderRsp;
import org.hrl.api.rsp.MTimeInForce;
import org.hrl.api.rsp.MTrade;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BinancePlaceOrderTask implements Callable<MPlaceOrderRsp> {

    private BinanceApiRestClient binanceApiRestClient;
    private MPlaceOrderRequest mPlaceOrderRequest;

    public BinancePlaceOrderTask(BinanceApiRestClient binanceApiRestClient, MPlaceOrderRequest mPlaceOrderRequest) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.mPlaceOrderRequest = mPlaceOrderRequest;
    }

    @Override
    public MPlaceOrderRsp call() throws Exception {
        NewOrder order = new NewOrder();
        String symbol = BinanceUtils.toSymbol(mPlaceOrderRequest.getBaseCoin(), mPlaceOrderRequest.getQuoteCoin());
        order.setSymbol(symbol);
        order.setPrice(String.valueOf(mPlaceOrderRequest.getPrice()));
        order.setQuantity(String.valueOf(mPlaceOrderRequest.getQuantity()));
        OrderType orderType = mPlaceOrderRequest.getType() == MOrderType.market ? OrderType.MARKET : OrderType.LIMIT;
        order.setType(orderType);
        order.setTimeInForce(TimeInForce.GTC); //good until cancel

        OrderSide orderSide = mPlaceOrderRequest.getSide() == MOrderSide.buy? OrderSide.BUY : OrderSide.SELL;
        order.setSide(orderSide);

        order.setTimestamp(System.currentTimeMillis());
        order.setRecvWindow(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW);


        NewOrderResponse newOrderResponse = binanceApiRestClient.newOrder(order);
        MPlaceOrderRsp mPlaceOrderRsp = new MPlaceOrderRsp();

        mPlaceOrderRsp.setOrderId(String.valueOf(newOrderResponse.getOrderId()));
        mPlaceOrderRsp.setSymbol(newOrderResponse.getSymbol());
        mPlaceOrderRsp.setOrderId(String.valueOf(newOrderResponse.getOrderId()));
        mPlaceOrderRsp.setClientOrderId(newOrderResponse.getClientOrderId());
        mPlaceOrderRsp.setTransactTime(newOrderResponse.getTransactTime());
        mPlaceOrderRsp.setPrice(newOrderResponse.getPrice());
        mPlaceOrderRsp.setOrigQty(newOrderResponse.getOrigQty());
        mPlaceOrderRsp.setExecutedQty(newOrderResponse.getExecutedQty());
        mPlaceOrderRsp.setCummulativeQuoteQty(newOrderResponse.getCummulativeQuoteQty());

        MOrderStatus mOrderStatus = MOrderStatus.valueOf(newOrderResponse.getStatus().toString());
        mPlaceOrderRsp.setStatus(mOrderStatus);

        MTimeInForce mTimeInForce = MTimeInForce.valueOf(newOrderResponse.getTimeInForce().toString());
        mPlaceOrderRsp.setTimeInForce(mTimeInForce);

        MOrderType mOrderType = newOrderResponse.getType() == OrderType.LIMIT ? MOrderType.limit : MOrderType.market;
        mPlaceOrderRsp.setType(mOrderType);

        MOrderSide mOrderSide = newOrderResponse.getSide() == OrderSide.BUY ? MOrderSide.buy : MOrderSide.sell;
        mPlaceOrderRsp.setSide(mOrderSide);

        List<MTrade> mTradeList = new ArrayList<>();

        for(Trade trade : newOrderResponse.getFills()) {
            MTrade mTrade = BinanceUtils.toMTrade(trade);
            mTradeList.add(mTrade);
        }

        mPlaceOrderRsp.setFills(mTradeList);

        return mPlaceOrderRsp;
    }


}
