package org.hrl.business;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.hrl.api.binance.impl.MBinanceAsyncRestClientImpl;
import org.hrl.api.huobi.client.MHuobiAsyncRestClient;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.rsp.MGetAccountsRsp;
import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderType;
import org.hrl.config.AppConfig;
import org.hrl.domain.ExchangeInfo;
import org.hrl.domain.PlacedOrderPairVO;
import org.hrl.domain.ProfitableTradePairVO;
import org.hrl.domain.ProfitableTradeVO;
import org.hrl.exception.PlaceOrderException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest()
public class Strategy1UnitTest {


    //@MockBean
    @Autowired
    private MHuobiAsyncRestClient huobiAsyncRestClient;

    @MockBean
    private MBinanceAsyncRestClientImpl binanceAsyncRestClient;


    @Autowired
    private AppConfig appConfig;

    @Ignore
    @Test
    public void testPlaceProfitableTradePairOrderList() throws PlaceOrderException {

        AppConfig a = appConfig;
        String baseCoin = "eos";
        String quoteCoin = "usdt";
        double profitThreshold = appConfig.getProfitThreshold();
        long reqIntervalMillis = appConfig.getReqIntervalMillis();
        double maxTradeQtyQuoteCoin = appConfig.getMaxTradeQtyQuoteCoin();
        int maxInprogressOrderPairNum = appConfig.getMaxInprogressOrderPairNum();
        System.out.println();

        Strategy1 strategy1 = new Strategy1(huobiAsyncRestClient, binanceAsyncRestClient, baseCoin, quoteCoin,
            profitThreshold, reqIntervalMillis,
            maxTradeQtyQuoteCoin, maxInprogressOrderPairNum);



        double profitableTradeVO1Price = 1;
        double profitableTradeVO1Quantity = 1;
        MOrderSide profitableTradeVO1OrderSice = MOrderSide.buy;
        MOrderType profitableTradeVO1OrderType = MOrderType.limit;
        ProfitableTradeVO profitableTradeVO1 = new ProfitableTradeVO(huobiAsyncRestClient, baseCoin, quoteCoin,
            profitableTradeVO1Price, profitableTradeVO1Quantity, profitableTradeVO1OrderSice,
            profitableTradeVO1OrderType);

        MPlaceOrderRequest huobiPlaceOrderReq = new MPlaceOrderRequest();
        huobiPlaceOrderReq.setBaseCoin(baseCoin);
        huobiPlaceOrderReq.setQuoteCoin(quoteCoin);
        huobiPlaceOrderReq.setSide(profitableTradeVO1OrderSice);
        huobiPlaceOrderReq.setType(profitableTradeVO1OrderType);
        huobiPlaceOrderReq.setPrice(profitableTradeVO1Price);
        huobiPlaceOrderReq.setQuantity(profitableTradeVO1Quantity);

        double profitableTradeVO2Price = 2;
        double profitableTradeVO2Quantity = 2;
        MOrderSide profitableTradeVO2OrderSice = MOrderSide.sell;
        MOrderType profitableTradeVO2OrderType = MOrderType.limit;

        ProfitableTradeVO profitableTradeVO2 = new ProfitableTradeVO(binanceAsyncRestClient, baseCoin, quoteCoin,
            profitableTradeVO2Price, profitableTradeVO2Quantity, profitableTradeVO2OrderSice, profitableTradeVO2OrderType);


        ProfitableTradePairVO profitableTradePairVO = new ProfitableTradePairVO(profitableTradeVO1, profitableTradeVO2);
        List<ProfitableTradePairVO> profitableTradePairVOList = new ArrayList<>();

        profitableTradePairVOList.add(profitableTradePairVO);


        List<PlacedOrderPairVO> list = strategy1.placeProfitableTradePairOrderList(profitableTradePairVOList);


        ExchangeInfo  huobiExchangeInfo  = new ExchangeInfo();
        huobiExchangeInfo.setPricePrecision(2);
        when(huobiAsyncRestClient.getExchangeInfo(baseCoin,quoteCoin)).thenReturn(huobiExchangeInfo);

        ExchangeInfo binanceExchangeInfo = new ExchangeInfo();
        binanceExchangeInfo.setPricePrecision(8);
        when(binanceAsyncRestClient.getExchangeInfo(baseCoin,quoteCoin)).thenReturn(huobiExchangeInfo);


        //verify(huobiAsyncRestClient).placeOrder(huobiPlaceOrderReq);
        //verify(huobiAsyncRestClient).placeOrder(any(MPlaceOrderRequest.class));
        //when(huobiAsyncRestClient.placeOrder(any(MPlaceOrderRequest.class))).thenReturn(null);
        //when(huobiAsyncRestClient.getAccounts()).thenReturn(null);

        System.out.println(list);

    }

    @Ignore
    @Test
    public void testGet() {
        if(appConfig.isProxyEnabled()) {
            System.setProperty("socksProxyHost", "172.31.1.162");
            System.setProperty("socksProxyPort", "1080");
        }

        FutureTask<MGetAccountsRsp> futureTask =  huobiAsyncRestClient.getAccounts();
        try {
            MGetAccountsRsp accountsRsp = futureTask.get();
            int a = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("");
    }

}
