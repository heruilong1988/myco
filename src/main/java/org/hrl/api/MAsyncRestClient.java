package org.hrl.api;

import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.*;
import org.hrl.domain.ExchangeInfo;
import org.hrl.domain.PriceQuantityPrecisionPair;

import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * 订单最多取小数位后8位
 */

public interface MAsyncRestClient {


    FutureTask<MDepth> depth(String baseCoin, String quoteCoin);

    FutureTask<MPlaceOrderRsp> placeOrder(MPlaceOrderRequest request);

    FutureTask<MQueryOrderRsp> queryOrder(MQueryOrderRequest request);

    FutureTask<MCancelOrderRsp> cancelOrder(MCancelOrderRequest request);

    FutureTask<MGetAccountsRsp> getAccounts();

    FutureTask<MGetBalanceRsp> getBalance(MGetBalanceRequest mGetBalanceRequest);

    ExchangeInfo getExchangeInfo(String baseCoin, String quoteCoin);
    double getTradeFee();
}
