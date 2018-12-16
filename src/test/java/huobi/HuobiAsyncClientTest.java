package huobi;

import org.hrl.api.huobi.client.ApiClient;
import org.hrl.api.huobi.client.MHuobiAsyncRestClient;
import org.hrl.api.huobi.response.Symbol;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class HuobiAsyncClientTest {


    static String API_KEY;
    static String API_SECRET;
    static ApiClient client;
    static MHuobiAsyncRestClient MHuobiAsyncRestClient;

    @BeforeClass
    public static void beforeClass() {

        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "1080");


        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "1080");

        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        API_KEY = properties.getProperty("huobi-accesskey");
        API_SECRET = properties.getProperty("huobi-secretkey");

        //client = new ApiClient(API_KEY, API_SECRET);
        MHuobiAsyncRestClient = new MHuobiAsyncRestClient(API_KEY,API_SECRET);
    }

    @Ignore
    @Test
    public void asyncGetSymbol() {
        FutureTask<List<Symbol>> futureTask = MHuobiAsyncRestClient.getSymbols();
        if(futureTask.isDone()) {
            System.out.println("is done");
        }else{
            System.out.print("not done yet");
        }

        try {
            List<Symbol> symbols = futureTask.get();
            System.out.println(symbols);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void asyncGetAccounts() {
        FutureTask<MGetAccountsRsp> futureTask = MHuobiAsyncRestClient.getAccounts();
        if(futureTask.isDone()) {
            System.out.println("is done");
        }else{
            System.out.print("not done yet");
        }

        try {
            MGetAccountsRsp mGetAccountsRsp = futureTask.get();
            System.out.println(mGetAccountsRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void asyncDepth() {
        FutureTask<MDepth> depthFutureTask = MHuobiAsyncRestClient.depth("btc","usdt");
        if(depthFutureTask.isDone()) {
            System.out.println("is done");
        }else{
            System.out.print("not done yet");
        }

        try {
            MDepth mDepth = depthFutureTask.get();

            System.out.println(mDepth);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void asyncGetBalance() {
        //accountId:3341355
        MGetBalanceRequest mGetBalanceRequest = new MGetBalanceRequest();
        mGetBalanceRequest.setAccountId(String.valueOf(3341355));
        FutureTask<MGetBalanceRsp> futureTask = MHuobiAsyncRestClient.getBalance(mGetBalanceRequest);
        if(futureTask.isDone()) {
            System.out.println("done");
        }else{
            System.out.println("not done");
        }

        try{
            MGetBalanceRsp mGetBalanceRsp = futureTask.get();
            System.out.println(mGetBalanceRsp);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void asyncPlaceOrder() {
        MPlaceOrderRequest mPlaceOrderRequest = new MPlaceOrderRequest();
        mPlaceOrderRequest.setAccountId(String.valueOf(3341355));
        mPlaceOrderRequest.setQuantity(0.001);
        mPlaceOrderRequest.setSide(MOrderSide.buy);
        mPlaceOrderRequest.setType(MOrderType.limit);
        mPlaceOrderRequest.setBaseCoin("eth");
        mPlaceOrderRequest.setQuoteCoin("usdt");
        mPlaceOrderRequest.setPrice(1);

        FutureTask<MPlaceOrderRsp> futureTask = MHuobiAsyncRestClient.placeOrder(mPlaceOrderRequest);
        if(futureTask.isDone()) {
            System.out.println("done");
        }else {
            System.out.println("not done");
        }

        try {
            MPlaceOrderRsp mPlaceOrderRsp = futureTask.get();
            System.out.println(mPlaceOrderRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void asyncQueryOrder() {
        MQueryOrderRequest mQueryOrderRequest = new MQueryOrderRequest();
        String orderId = "19146094788";
        mQueryOrderRequest.setOrderId(orderId);

        FutureTask<MQueryOrderRsp> futureTask = MHuobiAsyncRestClient.queryOrder(mQueryOrderRequest);

        try {
            MQueryOrderRsp mQueryOrderRsp = futureTask.get();
            System.out.println(mQueryOrderRsp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void asyncCancelOrder() {
        MCancelOrderRequest mCancelOrderRequest = new MCancelOrderRequest();
        String orderd ="19146094788";
        mCancelOrderRequest.setOrderId(orderd);

        FutureTask<MCancelOrderRsp> futureTask = MHuobiAsyncRestClient.cancelOrder(mCancelOrderRequest);

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
