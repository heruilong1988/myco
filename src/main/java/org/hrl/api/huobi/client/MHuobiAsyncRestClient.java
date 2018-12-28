package org.hrl.api.huobi.client;

import org.hrl.api.MAsyncRestClient;
import org.hrl.api.huobi.response.Symbol;
import org.hrl.api.huobi.task.*;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.*;
import org.hrl.config.PrecisionConfig;
import org.hrl.domain.ExchangeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/*
限制频率为10秒100次（单个APIKEY维度限制，建议行情API访问也要加上签名，否则限频会更严格
 */

@Component
public class MHuobiAsyncRestClient implements MAsyncRestClient {

    private  Map<String, ExchangeInfo> symbolExchangeInfoMap = new HashMap<>();

    private String accountId = "3341355";

    ExecutorService executorService = Executors.newFixedThreadPool(300);

    ApiClient apiClient;

    PrecisionConfig precisionConfig;

    public final String accessKeyId;
    public final String accessKeySecret;
    public final String assetPassword;

    public MHuobiAsyncRestClient(@Value("${huobi-accesskey}") String accessKeyId,
                                 @Value(("${huobi-secretkey}")) String accessKeySecret, @Autowired PrecisionConfig precisionConfig) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = null;

        this.precisionConfig = precisionConfig;

        this.apiClient = new ApiClient(accessKeyId, accessKeySecret);
    }

    /*
    public MHuobiAsyncRestClient(String accessKeyId, String accessKeySecret, String assetPassword) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.assetPassword = assetPassword;

        this.apiClient = new ApiClient(accessKeyId, accessKeySecret, assetPassword);
    }
    */

    @PostConstruct
    public void init()  {
        initExchangeInfo(precisionConfig.getHuobiPrecisionStr());
    }

    public void initExchangeInfo(String huobiPrecisionStr) throws JSONException {

        JSONArray symbolPrecisionJsonArr = new JSONObject(huobiPrecisionStr).getJSONArray("data");

        for (int i = 0; i < symbolPrecisionJsonArr.length(); i++) {
            JSONObject symbolPrecisionJson = symbolPrecisionJsonArr.getJSONObject(i);
            String baseCurrency = symbolPrecisionJson.getString("base-currency");
            String quoteCurrency = symbolPrecisionJson.getString("quote-currency");
            int pricePrecision = symbolPrecisionJson.getInt("price-precision");
            int amountPrecision = symbolPrecisionJson.getInt("amount-precision");
            ExchangeInfo exchangeInfo = new ExchangeInfo();
            exchangeInfo.setQuantityPrecision(amountPrecision);
            exchangeInfo.setPricePrecision(pricePrecision);
            symbolExchangeInfoMap.put(baseCurrency + quoteCurrency, exchangeInfo);
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
     * <p>
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

    @Override
    public ExchangeInfo getExchangeInfo(String baseCoin, String quoteCoin) {
        return symbolExchangeInfoMap.get(baseCoin + quoteCoin);
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
