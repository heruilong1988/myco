package org.hrl.api.binance.task;

import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.domain.account.Order;
import org.hrl.api.binance.domain.account.request.OrderStatusRequest;
import org.hrl.api.binance.util.BinanceUtils;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.MQueryOrderRsp;
import org.hrl.api.rsp.MTimeInForce;

import java.util.concurrent.Callable;

public class BinanceQueryOrderTask implements Callable<MQueryOrderRsp> {

    private BinanceApiRestClient binanceApiRestClient;
    private MQueryOrderRequest mQueryOrderRequest;


    public BinanceQueryOrderTask(BinanceApiRestClient binanceApiRestClient, MQueryOrderRequest mQueryOrderRequest) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.mQueryOrderRequest = mQueryOrderRequest;
    }

    @Override
    public MQueryOrderRsp call() throws Exception {

        String symbol = BinanceUtils.toSymbol(mQueryOrderRequest.getBaseCoin(), mQueryOrderRequest.getQuoteCoin());
        String orderId = mQueryOrderRequest.getOrderId();

        String clientOrderId = mQueryOrderRequest.getClientOrderId();

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(symbol, Long.parseLong(orderId));

        Order order = binanceApiRestClient.getOrderStatus(orderStatusRequest);

        MQueryOrderRsp mQueryOrderRsp = new MQueryOrderRsp();
        mQueryOrderRsp.setOrderId(String.valueOf(order.getOrderId()));
        mQueryOrderRsp.setClientOrderId(order.getClientOrderId());
        mQueryOrderRsp.setSymbol(order.getSymbol());
        mQueryOrderRsp.setPrice(order.getPrice());
        mQueryOrderRsp.setQuantity(order.getOrigQty());
        mQueryOrderRsp.setFieldQuantity(order.getExecutedQty());
        mQueryOrderRsp.setState(BinanceUtils.toMOrderstatus(order.getStatus()));
        mQueryOrderRsp.setmTimeInForce(MTimeInForce.valueOf(order.getTimeInForce().toString()));
        mQueryOrderRsp.setType(BinanceUtils.toMOrderType(order.getType()));
        mQueryOrderRsp.setSide(BinanceUtils.toMOrderSide(order.getSide()));
        mQueryOrderRsp.setStopPrice(order.getStopPrice());
        mQueryOrderRsp.setIcebergQuantity(order.getIcebergQty());
        mQueryOrderRsp.setTime(order.getTime());

        return mQueryOrderRsp;
    }
}
