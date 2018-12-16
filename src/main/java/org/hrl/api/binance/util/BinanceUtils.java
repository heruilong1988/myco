/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/11
 */

package org.hrl.api.binance.util;

import org.hrl.api.binance.domain.OrderSide;
import org.hrl.api.binance.domain.OrderStatus;
import org.hrl.api.binance.domain.OrderType;
import org.hrl.api.binance.domain.account.Trade;
import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderStatus;
import org.hrl.api.rsp.MOrderType;
import org.hrl.api.rsp.MTrade;

public class BinanceUtils {

    public static String toSymbol(String baseCoin, String quoteCoin) {
        return
            baseCoin.toUpperCase() + quoteCoin.toUpperCase();
    }

    public static MTrade toMTrade(Trade trade) {
        MTrade mTrade = new MTrade();
        mTrade.setCommission(trade.getCommission());
        mTrade.setCommissionAsset(trade.getCommissionAsset());
        mTrade.setPrice(trade.getPrice());
        mTrade.setQuantity(trade.getQty());
        mTrade.setSymbol(trade.getSymbol());
        mTrade.setTime(trade.getTime());
        mTrade.setTradeId(String.valueOf(trade.getId()));
        return mTrade;
    }

    public static MOrderStatus toMOrderstatus(OrderStatus orderStatus) {
        return MOrderStatus.valueOf(orderStatus.toString());
    }

    public static MOrderType toMOrderType(OrderType orderType) {
        if(orderType == OrderType.LIMIT) {
            return MOrderType.limit;
        }

        return MOrderType.market;
    }

    public static MOrderSide toMOrderSide(OrderSide orderSide) {
        if(orderSide == OrderSide.BUY) {
            return MOrderSide.buy;
        }

        return MOrderSide.sell;
    }
}
