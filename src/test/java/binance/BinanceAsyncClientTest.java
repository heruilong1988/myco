/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/12
 */

package binance;

import api.binance.BinanceApiClientFactory;
import api.binance.BinanceApiRestClient;
import api.binance.impl.MBinanceAsyncRestClientImpl;
import api.req.MCancelOrderRequest;
import api.req.MGetBalanceRequest;
import api.req.MPlaceOrderRequest;
import api.req.MQueryOrderRequest;
import api.rsp.MCancelOrderRsp;
import api.rsp.MDepth;
import api.rsp.MGetAccountsRsp;
import api.rsp.MGetBalanceRsp;
import api.rsp.MOrderSide;
import api.rsp.MOrderType;
import api.rsp.MPlaceOrderRsp;
import api.rsp.MQueryOrderRsp;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinanceAsyncClientTest {


    static BinanceApiRestClient binanceApiRestClient;
    static MBinanceAsyncRestClientImpl mBinanceAsyncRestClient;

    @BeforeClass
    public static void before() throws IOException {

        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "1080");

        //System.setProperty("socksProxyHost", "172.31.1.162");
        //System.setProperty("http.proxyPort","1080");

        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));

        String API_KEY = properties.getProperty("binance-accesskey");
        String API_SECRET = properties.getProperty("binance-secretkey");
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, API_SECRET);
        binanceApiRestClient  = factory.newRestClient();

        mBinanceAsyncRestClient = new MBinanceAsyncRestClientImpl(binanceApiRestClient);

    }

    @Test
    public void testDepth() {
        FutureTask<MDepth> mDepthFutureTask = mBinanceAsyncRestClient.depth("btc", "usdt");

        try {
            MDepth mDepth = mDepthFutureTask.get();
            System.out.println(mDepth);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAccounts() {
        FutureTask<MGetAccountsRsp> futureTask = mBinanceAsyncRestClient.getAccounts();

        try {
            MGetAccountsRsp mGetAccountsRsp = futureTask.get();
            System.out.println(mGetAccountsRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBalances() {
        MGetBalanceRequest mGetBalanceRequest = new MGetBalanceRequest();

        FutureTask<MGetBalanceRsp> futureTask = mBinanceAsyncRestClient.getBalance(mGetBalanceRequest);

        try {
            MGetBalanceRsp mGetBalanceRsp = futureTask.get();
            System.out.println(mGetBalanceRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPlaceOrder(){
        MPlaceOrderRequest mPlaceOrderRequest = new MPlaceOrderRequest();
        mPlaceOrderRequest.setBaseCoin("eth");
        mPlaceOrderRequest.setQuoteCoin("usdt");
        mPlaceOrderRequest.setPrice(60);
        mPlaceOrderRequest.setQuantity(0.2);
        mPlaceOrderRequest.setSide(MOrderSide.buy);
        mPlaceOrderRequest.setType(MOrderType.limit);
        mPlaceOrderRequest.setAccountId(null);

        FutureTask<MPlaceOrderRsp> futureTask = mBinanceAsyncRestClient.placeOrder(mPlaceOrderRequest);

        try {
            MPlaceOrderRsp mPlaceOrderRsp = futureTask.get();
            System.out.println(mPlaceOrderRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testQueryOrder(){

        String orderId = "150427526";
        String clientOrderId = "ak5LneEm7xv8r0meqJanOx";
        MQueryOrderRequest mQueryOrderRequest = new MQueryOrderRequest();
        mQueryOrderRequest.setBaseCoin("eth");
        mQueryOrderRequest.setQuoteCoin("usdt");
        mQueryOrderRequest.setOrderId(orderId);

        FutureTask<MQueryOrderRsp> futureTask = mBinanceAsyncRestClient.queryOrder(mQueryOrderRequest);

        try {
            MQueryOrderRsp mQueryOrderRsp = futureTask.get();
            System.out.println(mQueryOrderRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCancelOrder() {
        MCancelOrderRequest mCancelOrderRequest = new MCancelOrderRequest();
        String orderId = "150427526";
        mCancelOrderRequest.setBaseCoin("eth");
        mCancelOrderRequest.setQuoteCoin("usdt");
        mCancelOrderRequest.setOrderId(orderId);

        FutureTask<MCancelOrderRsp> futureTask = mBinanceAsyncRestClient.cancelOrder(mCancelOrderRequest);

        try {
            MCancelOrderRsp mCancelOrderRsp = futureTask.get();
            System.out.println(mCancelOrderRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
