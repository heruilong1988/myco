package org.hrl.api.huobi.client;



import org.hrl.api.huobi.response.Account;
import org.hrl.api.huobi.response.Symbol;
import org.hrl.api.req.MCancelOrderRequest;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.MCancelOrderRsp;
import org.hrl.api.rsp.MDepth;

import org.hrl.api.rsp.MGetAccountsRsp;
import org.hrl.api.rsp.MGetBalanceRsp;
import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderType;
import org.hrl.api.rsp.MPlaceOrderRsp;
import org.hrl.api.rsp.MQueryOrderRsp;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {

    static String API_KEY;
    static String API_SECRET;
    static ApiClient client = new ApiClient(API_KEY, API_SECRET);
    static MHuobiAsyncRestClient MHuobiAsyncRestClient;
    
    public static void main(String[] args) throws IOException {

        System.setProperty("socksProxyHost", "172.31.1.162");
        System.setProperty("http.proxyPort","1080");
        /*
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "1080");

// 对https也开启代理
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "1080");

*/
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));

        API_KEY = properties.getProperty("huobi-accesskey");
        API_SECRET = properties.getProperty("huobi-secretkey");

        MHuobiAsyncRestClient = new MHuobiAsyncRestClient(API_KEY, API_SECRET);

        try {
            //apiSample();
            asyncSample();
            System.out.println();
        } catch (ApiException e) {
            System.err.println("API Error! err-code: " + e.getErrCode() + ", err-msg: " + e.getMessage());
            e.printStackTrace();
        }

        MHuobiAsyncRestClient.close();
    }

    static void asyncSample() {
        //asyncGetSymbol();
        //asyncDepth();
        //asyncGetAccounts();
        //asyncGetBalance();
        asyncPlaceOrder();

    }

    static void asyncGetSymbol() {
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

    static void asyncGetAccounts() {
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
    static void asyncDepth() {
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

    static void asyncGetBalance() {
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

    static void asyncPlaceOrder() {
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

    static void asyncQueryOrder() {
        MQueryOrderRequest mQueryOrderRequest = new MQueryOrderRequest();
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

    static void asyncCancelOrder() {
        MCancelOrderRequest mCancelOrderRequest = new MCancelOrderRequest();
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
    
    static void apiSample() {
        // create ApiClient using your org.hrl.api key and org.hrl.api secret:
        ApiClient client = new ApiClient(API_KEY, API_SECRET);
        /*
        // get symbol list:
        print(client.getSymbols());

        //获取 K 线
        //------------------------------------------------------ kline -------------------------------------------------------
        KlineResponse kline = client.kline("btcusdt", "5min", "100");
        print(kline);

        //------------------------------------------------------ merged -------------------------------------------------------

        MergedResponse merged = client.merged("ethusdt");
        print(merged);

        //------------------------------------------------------ depth -------------------------------------------------------

        DepthRequest depthRequest = new DepthRequest();
        depthRequest.setSymbol("btcusdt");
        depthRequest.setType("step0");
        DepthResponse depth = client.depth(depthRequest);
        print(depth);

        //------------------------------------------------------ trade -------------------------------------------------------
        TradeResponse trade = client.trade("ethusdt");
        print(trade);

        //------------------------------------------------------ historyTrade -------------------------------------------------------
        HistoryTradeResponse historyTrade = client.historyTrade("ethusdt", "20");
        print(historyTrade);

        //------------------------------------------------------ historyTrade -------------------------------------------------------
        DetailResponse detailTrade = client.detail("ethusdt");
        print(detailTrade);

        //------------------------------------------------------ symbols -------------------------------------------------------
        SymbolsResponse symbols = client.symbols("btcusdt");
        print(symbols);

        //------------------------------------------------------ Currencys -------------------------------------------------------
        CurrencysResponse currencys = client.currencys("btcusdt");
        print(currencys);

        //------------------------------------------------------ Currencys -------------------------------------------------------
        TimestampResponse timestamp = client.timestamp();
        print(timestamp);

        //------------------------------------------------------ accounts -------------------------------------------------------
        AccountsResponse accounts = client.accounts();
        print(accounts);

        //------------------------------------------------------ balance -------------------------------------------------------
        List<Accounts> list = (List<Accounts>) accounts.getData();
        BalanceResponse balance = client.balance(String.valueOf(list.get(0).getId()));
        BalanceResponse balance2 = client.balance(String.valueOf(list.get(1).getId()));

        print(balance); //spot
        print(balance2);//otc

        Long orderId = 123L;
        if (!list.isEmpty()) {
            // find account id:
            Accounts account = list.get(0);
            long accountId = account.getId();
            // create order:
            CreateOrderRequest createOrderReq = new CreateOrderRequest();
            createOrderReq.accountId = String.valueOf(accountId);
            createOrderReq.amount = "0.02";
            createOrderReq.price = "0.1";
            createOrderReq.symbol = "eosusdt";
            createOrderReq.type = CreateOrderRequest.OrderType.BUY_LIMIT;
            createOrderReq.source = "org.hrl.api";

            //------------------------------------------------------ 创建订单  -------------------------------------------------------
            orderId = client.createOrder(createOrderReq);
            print(orderId);
            // place order:

            
        }

        //------------------------------------------------------ submitcancel 取消订单 -------------------------------------------------------

//    SubmitcancelResponse submitcancel = client.submitcancel(orderId.toString());
//    print(submitcancel);

        //------------------------------------------------------ submitcancel 批量取消订单-------------------------------------------------------
//    String[] orderList = {"727554767","727554766",""};
//    String[] orderList = {String.valueOf(orderId)};
        List orderList = new ArrayList();
        orderList.add(orderId);
        BatchcancelResponse submitcancels = client.submitcancels(orderList);
        print(submitcancels);

        //------------------------------------------------------ ordersDetail 订单详情 -------------------------------------------------------
        OrdersDetailResponse ordersDetail = client.ordersDetail(String.valueOf(orderId));
        print(ordersDetail);

        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
//    String.valueOf(orderId)
        MatchresultsOrdersDetailResponse matchresults = client.matchresults("714746923");
        print(ordersDetail);

        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
//    String.valueOf(orderId)
        IntrustOrdersDetailRequest req = new IntrustOrdersDetailRequest();
        req.symbol = "btcusdt";
        req.types = IntrustOrdersDetailRequest.OrderType.BUY_LIMIT;
//    req.startDate = "2018-01-01";
//    req.endDate = "2018-01-14";
        req.states = IntrustOrdersDetailRequest.OrderStates.FILLED;
//    req.from = "";
//    req.direct = "";
//    req.size = "";


//    public String symbol;	   //true	string	交易对		btcusdt, bccbtc, rcneth ...
//    public String types;	   //false	string	查询的订单类型组合，使用','分割		buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖
//    public String startDate;   //false	string	查询开始日期, 日期格式yyyy-mm-dd
//    public String endDate;	   //false	string	查询结束日期, 日期格式yyyy-mm-dd
//    public String states;	   //true	string	查询的订单状态组合，使用','分割		pre-submitted 准备提交, submitted 已提交, partial-filled 部分成交,
//    // partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
//    public String from;	       //false	string	查询起始 ID
//    public String direct;	   //false	string	查询方向		prev 向前，next 向后
//    public String size;	       //false	string	查询记录大小


        //------------------------------------------------------ order 查询当前委托、历史委托 -------------------------------------------------------

        IntrustDetailResponse intrustDetail = client.intrustOrdersDetail(req);
        print(intrustDetail);
        */

    // get accounts:
    List<Account> accounts1 = client.getAccounts();
    print(accounts1);

    }

    static void print(Object obj) {
        try {
            System.out.println(JsonUtil.writeValue(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
