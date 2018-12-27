package org.hrl.api.huobi.client;

import java.util.HashMap;
import java.util.Map;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.huobi.response.*;
import org.hrl.api.huobi.task.*;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.hrl.domain.PriceQuantityPrecisionPair;
import org.json.JSONArray;
import org.json.JSONObject;

/*
限制频率为10秒100次（单个APIKEY维度限制，建议行情API访问也要加上签名，否则限频会更严格
 */

public class MHuobiAsyncRestClient implements MAsyncRestClient {

    private static Map<String, PriceQuantityPrecisionPair> symbolPrecisionMap = new HashMap<>();

    private String accountId;

    ExecutorService executorService = Executors.newFixedThreadPool(300);

    ApiClient apiClient;

    public final String accessKeyId;
    public final String accessKeySecret;
    public final String assetPassword;

    public MHuobiAsyncRestClient(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = null;

        this.apiClient = new ApiClient(accessKeyId, accessKeySecret);
    }

    public MHuobiAsyncRestClient(String accessKeyId, String accessKeySecret, String assetPassword) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = assetPassword;

        this.apiClient = new ApiClient(accessKeyId, accessKeySecret, assetPassword);
    }

    public static void initSymbolPrecision(JSONArray symbolPrecisionJsonArr) {
        for (int i = 0; i < symbolPrecisionJsonArr.length(); i++) {
            JSONObject symbolPrecisionJson = symbolPrecisionJsonArr.getJSONObject(i);
            String baseCurrency = symbolPrecisionJson.getString("base-currency");
            String quoteCurrency = symbolPrecisionJson.getString("quote-currency");
            int pricePrecision = symbolPrecisionJson.getInt("price-precision");
            int amountPrecision = symbolPrecisionJson.getInt("amount-precision");
            symbolPrecisionMap
                .put(baseCurrency + quoteCurrency, new PriceQuantityPrecisionPair(pricePrecision, amountPrecision));
        }
    }

    /**
     * 查询交易对
     *
     * @return List of symbols.
     */
    public FutureTask<List<Symbol>> getSymbols() {

        GetSymbolTask getSymbolTask = new GetSymbolTask(apiClient);
        FutureTask<List<Symbol>> futureTask = new FutureTask(getSymbolTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    /**
     * GET /market/depth 获取 Market MDepth 数据
     *
     * "bids": 买盘,[price(成交价), amount(成交量)], 按price降序,
     * "asks": 卖盘,[price(成交价), amount(成交量)], 按price升序
     */
    public FutureTask<MDepth> depth(String baseCoin, String quoteCoin) {

        DepthTask depthTask = new DepthTask(apiClient, baseCoin, quoteCoin);
        FutureTask<MDepth> futureTask = new FutureTask<>(depthTask);
        executorService.execute(futureTask);

        return futureTask;
    }

    public FutureTask<MPlaceOrderRsp> placeOrder(MPlaceOrderRequest request) {

        request.setAccountId(accountId);

        PlaceOrderTask placeOrderTask = new PlaceOrderTask(apiClient, request);
        FutureTask<MPlaceOrderRsp> futureTask = new FutureTask<>(placeOrderTask);
        executorService.execute(futureTask);
        return futureTask;

    }

    /**
     * GET /v1/order/orders/{order-id} 查询某个订单详情
     */
    public FutureTask<MQueryOrderRsp> queryOrder(MQueryOrderRequest request) {
        request.setAccountId(accountId);
        QueryOrderTask queryOrderTask = new QueryOrderTask(apiClient, request);
        FutureTask<MQueryOrderRsp> futureTask = new FutureTask<>(queryOrderTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public FutureTask<MGetAccountsRsp> getAccounts() {
        GetAccountsTask getAccountsTask = new GetAccountsTask(apiClient);
        FutureTask<MGetAccountsRsp> futureTask = new FutureTask<>(getAccountsTask);
        executorService.execute(futureTask);
        return futureTask;
    }


    /**
     * POST /v1/order/orders/{order-id}/submitcancel 申请撤销一个订单请求
     */
    public FutureTask<MCancelOrderRsp> cancelOrder(MCancelOrderRequest mCancelOrderRequest) {

        CancelOrderTask cancelOrderTask = new CancelOrderTask(apiClient, mCancelOrderRequest);
        FutureTask<MCancelOrderRsp> futureTask = new FutureTask<>(cancelOrderTask);
        executorService.execute(futureTask);
        return futureTask;
    }


    public FutureTask<MGetBalanceRsp> getBalance(MGetBalanceRequest mGetBalanceRequest) {

        mGetBalanceRequest.setAccountId(accountId);

        GetBalanceTask getBalanceTask = new GetBalanceTask(apiClient, mGetBalanceRequest);
        FutureTask<MGetBalanceRsp> futureTask = new FutureTask<MGetBalanceRsp>(getBalanceTask);
        executorService.execute(futureTask);
        return futureTask;
    }

    public static void main(String[] args) {

    }

    @Override
    public double getTradeFee() {
        return 0.002;
    }

    public void close() {
        executorService.shutdown();
    }
}
