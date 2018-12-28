package org.hrl.business;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.req.MGetBalanceRequest;
import org.hrl.api.rsp.*;
import org.hrl.domain.*;
import org.hrl.exception.GetBalanceException;
import org.hrl.exception.GetDepthException;
import org.hrl.exception.PlaceOrderException;
import org.hrl.exception.QueryOrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.springframework.beans.factory.annotation.Autowired;

public class Strategy1 {

    private Logger ORDERLOGGER;
    private Logger LOGGER;

    private long businessCount = 0;

    private List<PlacedOrderPairVO> inProgressPlacedOrderPairVOList = new ArrayList<>();
    private List<PlacedOrderPairVO> historyPlacedOrderPairVOList = new ArrayList<>();


    private MAsyncRestClient mAsyncRestClientPlatformA;
    private MAsyncRestClient mAsyncRestClientPlatformB;

    private String baseCoin;
    private String quoteCoin;

    private double maxTradeQtyQuoteCoin = 150; //usdt

    private int maxInprogressOrderPairNum = 10;

    private boolean stop = false;
    private long firstOrderRoundCount = 0;

    //config，利润阈值
    private double profitThreshold = 0.002;

    //config,请求间的间隔
    private long reqIntervalMillis = 200;

    private double accountBalanceQuoteCoinPlatformA = 0.0;
    private double accountBalanceBaseCoinPlatformA = 0.0;

    private double accountBalanceQuoteCoinPlatformB = 0.0;
    private double accountBalanceBaseCoinPlatformB = 0.0;

    private boolean notEnoughBaseCoinBalancePlatformA = false;
    private boolean notEnoughQuoteCoinBalancePlatformA = false;

    private boolean notEnoughtBaseCoinBalancePlatformB = false;
    private boolean notEnoughQuoteCoinBalancePlatformB = false;

    //private ExchangeInfo exchangeInfoPlatformA;
    //private ExchangeInfo exchangeInfoPlatformB;

    //private int tradeQuantityPrecision;

    public Strategy1(MAsyncRestClient mAsyncRestClientPlatformA,
        MAsyncRestClient mAsyncRestClientPlatformB,
        String baseCoin,
        String quoteCoin,
        double profitThreshold,
        long reqIntervalMillis,
        double maxTradeQtyQuoteCoin,
        int maxInprogressOrderPairNum) {

        this.mAsyncRestClientPlatformA = mAsyncRestClientPlatformA;
        this.mAsyncRestClientPlatformB = mAsyncRestClientPlatformB;
        this.baseCoin = baseCoin;
        this.quoteCoin = quoteCoin;
        this.profitThreshold = profitThreshold;
        this.reqIntervalMillis = reqIntervalMillis;
        this.maxTradeQtyQuoteCoin = maxTradeQtyQuoteCoin;
        this.maxInprogressOrderPairNum = maxInprogressOrderPairNum;

        //exchangeInfoPlatformA = mAsyncRestClientPlatformA.getExchangeInfo(baseCoin, quoteCoin);
        //exchangeInfoPlatformB = mAsyncRestClientPlatformB.getExchangeInfo(baseCoin, quoteCoin);

        //tradeQuantityPrecision = Math
        //.min(exchangeInfoPlatformA.getQuantityPrecision(), exchangeInfoPlatformB.getQuantityPrecision());

        LOGGER = LoggerFactory.getLogger(baseCoin);
        ORDERLOGGER = LoggerFactory.getLogger(baseCoin + ".Order");
        LOGGER.info(this.toString());
    }

    public void initAccountBalance() throws GetBalanceException {
        MGetBalanceRequest mGetBalanceRequest = new MGetBalanceRequest();
        //mGetBalanceRequest.setAccountId();
        mGetBalanceRequest.setBaseCoin(baseCoin);
        mGetBalanceRequest.setQuoteCoin(quoteCoin);

        FutureTask<MGetBalanceRsp> balanceFuturePlatformA = mAsyncRestClientPlatformA.getBalance(mGetBalanceRequest);
        FutureTask<MGetBalanceRsp> balanceFuturePlatformB = mAsyncRestClientPlatformB.getBalance(mGetBalanceRequest);

        try {
            MGetBalanceRsp balanceRspPlatformA = balanceFuturePlatformA.get();
            Map<String, MCurrencyBalance> currencyBalanceMapA = balanceRspPlatformA.getmBalance()
                .getmCurrencyBalanceMap();
            accountBalanceBaseCoinPlatformA = currencyBalanceMapA.get(baseCoin).getTradeBalance();
            accountBalanceQuoteCoinPlatformA = currencyBalanceMapA.get(quoteCoin).getTradeBalance();
        } catch (InterruptedException e) {
            LOGGER.info("fail to get balance.", e);
            e.printStackTrace();
            throw new GetBalanceException(e);
        } catch (ExecutionException e) {
            LOGGER.info("fail to get balance.", e);
            e.printStackTrace();
            throw new GetBalanceException(e);
        }

        try {
            MGetBalanceRsp balanceRspPlatformB = balanceFuturePlatformB.get();
            Map<String, MCurrencyBalance> currencyBalanceMapB = balanceRspPlatformB.getmBalance()
                .getmCurrencyBalanceMap();
            accountBalanceBaseCoinPlatformB = currencyBalanceMapB.get(baseCoin).getTradeBalance();
            accountBalanceQuoteCoinPlatformB = currencyBalanceMapB.get(quoteCoin).getTradeBalance();

        } catch (InterruptedException e) {
            LOGGER.info("fail to get balance.", e);
            e.printStackTrace();
            throw new GetBalanceException(e);
        } catch (ExecutionException e) {
            LOGGER.info("fail to get balance.", e);
            e.printStackTrace();
            throw new GetBalanceException(e);
        }


    }

    public void dataCollect(){
        //initAccountBalance();
        Thread.currentThread().setName("primarythread:" + baseCoin);
        while (true) {
            //LOGGER.info("{} is alive", Thread.currentThread().getName());
            try {
                long startNs = System.nanoTime();
                calcDepthProfit();

                long sleepMs = calcSleepMillis(startNs);
                //LOGGER.info("sleepMs:{}", sleepMs);
                if (sleepMs > 0) {
                    try {
                        //System.out.println(sleepTime);
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                LOGGER.error("dataCollect Exception", e);
            } catch (Throwable t) {
                LOGGER.error("throwable ", t);
            }
        }
    }

    private long calcSleepMillis(long startNs) {
        long durationNs = System.nanoTime() - startNs;
        long durationMs = durationNs / 1000000;
        if (durationMs > reqIntervalMillis) {
            //already exceed,no sleep
            return 0;
        } else {
            return reqIntervalMillis - durationMs;
        }
    }


    public void calcDepthProfit() {
        FutureTask<MDepth> huobiDepthFuture = mAsyncRestClientPlatformA.depth(baseCoin, quoteCoin);
        FutureTask<MDepth> binanceDepthFuture = mAsyncRestClientPlatformB.depth(baseCoin, quoteCoin);

        MDepth huobiDepth = null;
        MDepth binanceDepth = null;
        try {
            huobiDepth = huobiDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error("huobi depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.error("huobi depth ExecutionException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        }

        try {
            binanceDepth = binanceDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.error("binance depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.error("binance depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        }

        if (huobiDepth == null || binanceDepth == null) {
            LOGGER.info("huobiDepth || binanceDepth is null");
            //continue next round
            return;
        }

        List<PriceQtyPair> huobiAskList = huobiDepth.getAsks();
        List<PriceQtyPair> huobiBidList = huobiDepth.getBids();

        List<PriceQtyPair> binanceAskList = binanceDepth.getAsks();
        List<PriceQtyPair> binanceBidList = binanceDepth.getBids();

        PriceQtyPair huobiBid1 = huobiBidList.get(0);
        PriceQtyPair binanceAsk1 = binanceAskList.get(0);

        PriceQtyPair huobiAsk1 = huobiAskList.get(0);
        PriceQtyPair binanceBid1 = binanceBidList.get(0);

        double priceDiff = huobiBid1.getPrice() - binanceAsk1.getPrice();
        double profit1 = priceDiff - (huobiBid1.getPrice() * mAsyncRestClientPlatformA.getTradeFee()
            + binanceAsk1.getPrice() * mAsyncRestClientPlatformB.getTradeFee());

        double priceDiff2 = binanceBid1.getPrice() - huobiAsk1.getPrice();
        double profit2 = priceDiff2 - (binanceBid1.getPrice() * mAsyncRestClientPlatformB.getTradeFee()
            + huobiAsk1.getPrice() * mAsyncRestClientPlatformA.getTradeFee());

        if (profit1 > profitThreshold) {
            LOGGER.info("INFO-profit:{},huobiBid:{},binanceAsk:{}", profit1, huobiBid1, binanceAsk1);
        }

        if (profit2 > profitThreshold) {
            LOGGER.info("INFO-profit2:{},binanceBid:{},huobiAsk:{}", profit2, binanceBid1, huobiAsk1);
        }

        businessCount++;
        if (businessCount % 1000 == 0) {
            if (profit1 < 0) {
                LOGGER.info("DEBUG:profit:{},huobiBid:{},binanceAsk:{}", profit1, huobiBid1, binanceAsk1);
            }
            if (profit2 < 0) {
                LOGGER.info("DEBUG:profit2:{},binanceBid:{},huobiAsk:{}", profit2, binanceBid1, huobiAsk1);
            }
        }

        if (businessCount % 100 == 0) {
            LOGGER.info("businessCount:{}", businessCount);
        }
    }


    public void firstOrderStrategy() throws GetBalanceException {

        initAccountBalance();

        while (!stop) {
            try {
                long startNs = System.nanoTime();
                List<ProfitableTradePairVO> profitableTradePairVOList = calcProfit();

                if (inProgressPlacedOrderPairVOList.size() < maxInprogressOrderPairNum) {
                    List<PlacedOrderPairVO> placedOrderPairVOList = placeProfitableTradePairOrderList(
                        profitableTradePairVOList);
                    inProgressPlacedOrderPairVOList.addAll(placedOrderPairVOList);
                }

                if (!inProgressPlacedOrderPairVOList.isEmpty()) {
                    checkInprogressPlacedOrderPairList();
                }

                ++firstOrderRoundCount;

                if (firstOrderRoundCount % 100 == 0) {
                    LOGGER.info("inprogressPlacedOrderSize:{},inprogressPlacedOrderList:{}",
                        inProgressPlacedOrderPairVOList.size(), inProgressPlacedOrderPairVOList);
                }

                long sleepMs = calcSleepMillis(startNs);
                if (sleepMs > 0) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        LOGGER.info("sleep interrupted.", e);
                    }
                }
            } catch (GetDepthException e1) {
                LOGGER.info("fail to get depth, continue next round");
            } catch (PlaceOrderException e) {
                LOGGER.error("fail to place order, stop application", e);
                stop = true;
            } catch (QueryOrderException e) {
                LOGGER.error("fail to query order", e);
            } catch (Exception e) {
                LOGGER.info("fail firstOrderRound", e);
                System.out.println("exception: exit system.");
                stop = true;
            }
        }

        LOGGER.info("exit system.");
        System.out.println("exit system.");

    }


    public void checkInprogressPlacedOrderPairList() throws QueryOrderException {
        if (inProgressPlacedOrderPairVOList.isEmpty()) {
            ORDERLOGGER.info("inProgressPlacedOrderPairVOList is empty.");
            return;
        }

        Iterator<PlacedOrderPairVO> iterator = inProgressPlacedOrderPairVOList.iterator();

        for (; iterator.hasNext(); ) {
            PlacedOrderPairVO placedOrderPairVO = iterator.next();
            PlacedOrderVO placedOrderVO1 = placedOrderPairVO.getPlacedOrderVO1();
            PlacedOrderVO placedOrderVO2 = placedOrderPairVO.getPlacedOrderVO2();

            updateOrderStatusAndAccountBalance(placedOrderVO1);
            updateOrderStatusAndAccountBalance(placedOrderVO2);

            /*
            if (!placedOrderVO1.isFinished()) {
                MQueryOrderRsp mQueryOrderRsp1 = placedOrderVO1.asyncQueryOrder();


                if (isOrderFinished(mQueryOrderRsp1.getState())) {
                    placedOrderVO1.setFinished(true);
                }
            }

            PlacedOrderVO placedOrderVO2 = placedOrderPairVO.getPlacedOrderVO2();
            if (!placedOrderVO2.isFinished()) {
                MQueryOrderRsp mQueryOrderRsp2 = placedOrderVO2.asyncQueryOrder();
                if (isOrderFinished(mQueryOrderRsp2.getState())) {
                    placedOrderVO2.setFinished(true);
                }
            }
            */

            if (placedOrderVO1 != null && placedOrderVO1.isFinished() && placedOrderVO2 != null && placedOrderVO2.isFinished()) {
                historyPlacedOrderPairVOList.add(placedOrderPairVO);
                iterator.remove();
            }
        }

    }

    private void updateOrderStatusAndAccountBalance(PlacedOrderVO placedOrderVO) throws QueryOrderException {
        if (placedOrderVO != null && !placedOrderVO.isFinished()) {
            MQueryOrderRsp mQueryOrderRsp1 = placedOrderVO.asyncQueryOrder();

            updateAccountBalance(placedOrderVO);

            if (isOrderFinished(mQueryOrderRsp1.getState())) {
                placedOrderVO.setFinished(true);
            }
        }
    }

    private void updateAccountBalance(PlacedOrderVO placedOrderVO) {
        if (placedOrderVO == null || placedOrderVO.getmQueryOrderRsp() == null) {
            LOGGER.error("Cannot update account balance.NO queryOrderRsp");
            return;
        }

        MQueryOrderRsp mQueryOrderRsp = placedOrderVO.getmQueryOrderRsp();
        double newTradeQuantity =
            Double.parseDouble(mQueryOrderRsp.getFieldQuantity()) - placedOrderVO.getLastFieldQuantity();
        if (newTradeQuantity > 0) {
            //there is new field quantity
            double orderPrice = Double.parseDouble(mQueryOrderRsp.getPrice());
            if (MOrderSide.buy.equals(mQueryOrderRsp.getSide())) {
                //buy add more balance of baseCoin, decrease quoteCoin
                if (placedOrderVO.getmAsyncRestClient() == this.mAsyncRestClientPlatformA) {
                    //platformA
                    accountBalanceBaseCoinPlatformA += newTradeQuantity;
                    accountBalanceQuoteCoinPlatformA -= newTradeQuantity * orderPrice;
                } else {
                    accountBalanceBaseCoinPlatformB += newTradeQuantity;
                    accountBalanceQuoteCoinPlatformB -= newTradeQuantity * orderPrice;
                }
            } else {
                //sell , add quoteCoin, decrease baseCoin
                if (placedOrderVO.getmAsyncRestClient() == this.mAsyncRestClientPlatformA) {
                    //platformA
                    accountBalanceQuoteCoinPlatformA += newTradeQuantity * orderPrice;
                    accountBalanceBaseCoinPlatformA -= newTradeQuantity;
                } else {
                    accountBalanceQuoteCoinPlatformB += newTradeQuantity * orderPrice;
                    accountBalanceBaseCoinPlatformB -= newTradeQuantity;
                }

            }
        }

    }


    /**
     * @return list of PlacedOrderPair, placedOrderVO will be null when place order encountering exception
     */
    public List<PlacedOrderPairVO> placeProfitableTradePairOrderList(
        List<ProfitableTradePairVO> profitableTradePairVOS) throws PlaceOrderException {

        List<PlacedOrderPairVO> placedOrderPairVOList = new ArrayList<>();

        if (profitableTradePairVOS == null || profitableTradePairVOS.isEmpty()) {
            LOGGER.debug("no profitableTradePairVOS.");
            return placedOrderPairVOList;
        }

        for (ProfitableTradePairVO profitableTradePairVO : profitableTradePairVOS) {
            ProfitableTradeVO profitableTradeVO1 = profitableTradePairVO.getProfitableTradeVO1();
            ProfitableTradeVO profitableTradeVO2 = profitableTradePairVO.getProfitableTradeVO2();

            //may not have enough balance of
            FutureTask<MPlaceOrderRsp> profitableTradeRspFuture1 = placeTradeOrder(profitableTradeVO1);
            //profitableTradeVO1.asyncPlaceOrder();
            FutureTask<MPlaceOrderRsp> profitableTradeRspFuture2 = placeTradeOrder(profitableTradeVO2);
            //profitableTradeVO2.asyncPlaceOrder();

            PlacedOrderVO placedOrderVO1 = null;
            MPlaceOrderRsp mPlaceOrderRsp1 = null;

            if (profitableTradeRspFuture1 != null) {
                try {
                    mPlaceOrderRsp1 = profitableTradeRspFuture1.get();
                    placedOrderVO1 = new PlacedOrderVO(profitableTradeVO1.getmAsyncRestClient(), profitableTradeVO1,
                        mPlaceOrderRsp1);
                    ORDERLOGGER.info("placed order {}", placedOrderVO1);
                } catch (InterruptedException e) {
                    LOGGER.error("fail place order.profitbaleTradeVO1:{},mPlaceOrderRsp1:{}", profitableTradeVO1,
                        mPlaceOrderRsp1, e);
                    throw new PlaceOrderException(e);
                } catch (ExecutionException e) {
                    LOGGER.error("fail place order.profitbaleTradeVO1:{},mPlaceOrderRsp1:{}", profitableTradeVO1,
                        mPlaceOrderRsp1, e);
                    throw new PlaceOrderException(e);
                }
            }

            PlacedOrderVO placedOrderVO2 = null;
            MPlaceOrderRsp mPlaceOrderRsp2 = null;

            if (profitableTradeRspFuture2 != null) {
                try {
                    mPlaceOrderRsp2 = profitableTradeRspFuture2.get();
                    placedOrderVO2 = new PlacedOrderVO(profitableTradeVO2.getmAsyncRestClient(), profitableTradeVO2,
                        mPlaceOrderRsp2);
                    ORDERLOGGER.info("place order:{}", placedOrderVO2);
                } catch (InterruptedException e) {
                    LOGGER.error("fail place order.profitbaleTradeVO2:{},mPlaceOrderRsp2:{}", profitableTradeVO2,
                        mPlaceOrderRsp2, e);
                    throw new PlaceOrderException(e);
                } catch (ExecutionException e) {
                    LOGGER.error("fail place order.profitbaleTradeVO2:{},mPlaceOrderRsp2:{}", profitableTradeVO2,
                        mPlaceOrderRsp2, e);
                    throw new PlaceOrderException(e);
                }
            }

            //placeOrderVO1, placeOrderVO2 may be null
            if (placedOrderVO1 != null || placedOrderVO2 != null) {
                PlacedOrderPairVO placedOrderPairVO = new PlacedOrderPairVO(placedOrderVO1, placedOrderVO2);
                placedOrderPairVOList.add(placedOrderPairVO);
            }

        }

        return placedOrderPairVOList;
    }


    /**
     * place order if has enough balance
     */
    private FutureTask<MPlaceOrderRsp> placeTradeOrder(ProfitableTradeVO profitableTradeVO) {

        FutureTask<MPlaceOrderRsp> profitableTradeRspFuture = null;

        MAsyncRestClient mAsyncRestClient = profitableTradeVO.getmAsyncRestClient();
        double price = profitableTradeVO.getPrice();
        double quantity = profitableTradeVO.getQuantity();
        if (mAsyncRestClient == mAsyncRestClientPlatformA) {
            //platformA
            if (MOrderSide.buy.equals(profitableTradeVO.getmOrderSide())) {
                //buy base coin,
                if (accountBalanceQuoteCoinPlatformA < price * quantity) {
                    ORDERLOGGER.error(
                        "not enough quoteCoin balance to place a buy trade order.platformA QuoteCoinBalance:{}, profitableTradeVO:{}",
                        accountBalanceQuoteCoinPlatformA, profitableTradeVO);
                    notEnoughQuoteCoinBalancePlatformA = true;
                } else {
                    profitableTradeRspFuture = profitableTradeVO.asyncPlaceOrder();
                }
            } else {
                //sell base coin,
                if (accountBalanceBaseCoinPlatformA < quantity) {
                    ORDERLOGGER.error(
                        "not enough baseCoin balance to place a sell trade order.platformA BaseCoinBalance:{}, profitableTradeVO:{}",
                        accountBalanceBaseCoinPlatformA, profitableTradeVO);
                    notEnoughBaseCoinBalancePlatformA = true;
                } else {
                    profitableTradeRspFuture = profitableTradeVO.asyncPlaceOrder();
                }
            }
        } else {
            //platformB
            if (MOrderSide.buy.equals(profitableTradeVO.getmOrderSide())) {
                //buy base coin
                if (accountBalanceQuoteCoinPlatformB < price * quantity) {
                    ORDERLOGGER.error(
                        "not enough quoteCoin balance to place a buy trade order.platformB QuoteCoinBalance:{},profitableTradeVO:{}",
                        accountBalanceQuoteCoinPlatformB, profitableTradeVO);
                    notEnoughQuoteCoinBalancePlatformB = true;
                } else {
                    profitableTradeRspFuture = profitableTradeVO.asyncPlaceOrder();
                }
            } else {
                //sell base coin
                if (accountBalanceBaseCoinPlatformB < quantity) {
                    ORDERLOGGER.error(
                        "not enough baseCoin balance to place a sell trade order.platformB BaseCoinBalance:{}, profitableTradeVO:{}",
                        accountBalanceBaseCoinPlatformB, profitableTradeVO);
                    notEnoughtBaseCoinBalancePlatformB = true;
                } else {
                    profitableTradeRspFuture = profitableTradeVO.asyncPlaceOrder();
                }
            }
        }

        return profitableTradeRspFuture;

    }

    public List<ProfitableTradePairVO> calcProfit() throws GetDepthException {
        List<ProfitableTradePairVO> profitableTradePairVOS = new ArrayList<>();

        FutureTask<MDepth> huobiDepthFuture = mAsyncRestClientPlatformA.depth(baseCoin, quoteCoin);
        FutureTask<MDepth> binanceDepthFuture = mAsyncRestClientPlatformB.depth(baseCoin, quoteCoin);

        MDepth huobiDepth = null;
        MDepth binanceDepth = null;
        try {
            huobiDepth = huobiDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER
                .error("platformA depth InterruptedException.baseCoin:{},quoteCoin:{},platformA:{}", baseCoin,
                    quoteCoin,
                    mAsyncRestClientPlatformA, e);
            throw new GetDepthException(e);
        } catch (ExecutionException e) {
            LOGGER
                .error("platformA depth ExecutionException.baseCoin:{},quoteCoin:{},platformA:{}", baseCoin, quoteCoin,
                    mAsyncRestClientPlatformA, e);
            throw new GetDepthException(e);
        }

        try {
            binanceDepth = binanceDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER
                .error("platformB depth InterruptedException.baseCoin:{},quoteCoin:{},platformB:{}", baseCoin,
                    quoteCoin,
                    mAsyncRestClientPlatformB, e);
            throw new GetDepthException(e);
        } catch (ExecutionException e) {
            LOGGER
                .error("platformB depth InterruptedException.baseCoin:{},quoteCoin:{},platformB:{}", baseCoin,
                    quoteCoin,
                    mAsyncRestClientPlatformB, e);
            throw new GetDepthException(e);
        }

        if (huobiDepth == null || binanceDepth == null) {
            //continue next round
            return profitableTradePairVOS;
        }

        List<PriceQtyPair> huobiAskList = huobiDepth.getAsks();
        List<PriceQtyPair> huobiBidList = huobiDepth.getBids();

        List<PriceQtyPair> binanceAskList = binanceDepth.getAsks();
        List<PriceQtyPair> binanceBidList = binanceDepth.getBids();

        PriceQtyPair huobiBid1 = huobiBidList.get(0);
        PriceQtyPair binanceAsk1 = binanceAskList.get(0);

        PriceQtyPair huobiAsk1 = huobiAskList.get(0);
        PriceQtyPair binanceBid1 = binanceBidList.get(0);

        double priceDiff = huobiBid1.getPrice() - binanceAsk1.getPrice();
        double profit1 = priceDiff - (huobiBid1.getPrice() * mAsyncRestClientPlatformA.getTradeFee()
            + binanceAsk1.getPrice() * mAsyncRestClientPlatformB.getTradeFee());

        if (profit1 > profitThreshold) {

            double tradeAskPrice = huobiBid1.getPrice();
            double tradeQuantity = calcMinQuantity(huobiBid1, binanceAsk1, maxTradeQtyQuoteCoin);
            ProfitableTradeVO profitableTradeVO = new ProfitableTradeVO(mAsyncRestClientPlatformA, baseCoin, quoteCoin,
                tradeAskPrice, tradeQuantity, MOrderSide.sell, MOrderType.limit);

            double tradeBidPrice = binanceAsk1.getPrice();
            ProfitableTradeVO profitableTradeVO1 = new ProfitableTradeVO(mAsyncRestClientPlatformB, baseCoin, quoteCoin,
                tradeBidPrice, tradeQuantity, MOrderSide.buy, MOrderType.limit);

            ProfitableTradePairVO profitableTradePairVO = new ProfitableTradePairVO(profitableTradeVO,
                profitableTradeVO1);

            profitableTradePairVOS.add(profitableTradePairVO);

            LOGGER
                .info("INFO-profit:{},platformA-Bid:{},platformB-Ask:{},platformA:{},platformB:{}", profit1, huobiBid1,
                    binanceAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        double priceDiff2 = binanceBid1.getPrice() - huobiAsk1.getPrice();
        double profit2 = priceDiff2 - (binanceBid1.getPrice() * mAsyncRestClientPlatformB.getTradeFee()
            + huobiAsk1.getPrice() * mAsyncRestClientPlatformA.getTradeFee());

        if (profit2 > profitThreshold) {

            double tradeAskPrice = binanceBid1.getPrice();
            double tradeQuantity = calcMinQuantity(binanceBid1, huobiAsk1, maxTradeQtyQuoteCoin);
            ProfitableTradeVO profitableTradeVO = new ProfitableTradeVO(mAsyncRestClientPlatformB, baseCoin, quoteCoin,
                tradeAskPrice, tradeQuantity, MOrderSide.sell, MOrderType.limit);

            double tradeBidPrice = huobiAsk1.getPrice();
            ProfitableTradeVO profitableTradeVO1 = new ProfitableTradeVO(mAsyncRestClientPlatformA, baseCoin, quoteCoin,
                tradeBidPrice, tradeQuantity, MOrderSide.buy, MOrderType.limit);

            ProfitableTradePairVO profitableTradePairVO = new ProfitableTradePairVO(profitableTradeVO,
                profitableTradeVO1);
            profitableTradePairVOS.add(profitableTradePairVO);

            LOGGER.info("INFO-profit2:{},platformB-Bid:{},platformA-Ask:{},platformA:{},platformB:{}", profit2,
                binanceBid1, huobiAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        businessCount++;
        if (businessCount % 100 == 0) {
            LOGGER.info("businessCount:{}", businessCount);
        }

        if (businessCount % 1000 == 0) {
            LOGGER
                .info("DEBUG:profit:{},platformA-Bid:{},platformB-Ask:{},platformA:{},platformB:{}", profit1, huobiBid1,
                    binanceAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
            LOGGER.info("DEBUG:profit2:{},platformB-Bid:{},platformA-Ask:{},platformA:{},platformB:{}", profit2,
                binanceBid1, huobiAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        return profitableTradePairVOS;
    }

    private double calcMinQuantity(PriceQtyPair pair1, PriceQtyPair pair2, double maxTradeQtyQuoteCoin) {


        double minPairQty = Math.min(pair1.getQty(), pair2.getQty());
        double maxTradeQtyBaseCoin = maxTradeQtyQuoteCoin / ((pair1.getPrice() + pair2.getPrice()) / 2.0);
        double minQty = Math.min(minPairQty, maxTradeQtyBaseCoin);

        ExchangeInfo exchangeInfoPlatformA = mAsyncRestClientPlatformA.getExchangeInfo(baseCoin, quoteCoin);
        ExchangeInfo exchangeInfoPlatformB = mAsyncRestClientPlatformB.getExchangeInfo(baseCoin, quoteCoin);

        int tradeQuantityPrecision = Math
            .min(exchangeInfoPlatformA.getQuantityPrecision(), exchangeInfoPlatformB.getQuantityPrecision());

        BigDecimal bigDecimal = new BigDecimal(minQty);
        double returnQty = bigDecimal.setScale(tradeQuantityPrecision, RoundingMode.UP).doubleValue();

        return returnQty;
    }

    public boolean isOrderFinished(MOrderStatus mOrderStatus) {
        if (mOrderStatus == null || MOrderStatus.NEW.equals(mOrderStatus) || MOrderStatus.PARTIALLY_FILLED
            .equals(mOrderStatus)) {
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("businessCount", businessCount)
            .add("inProgressPlacedOrderPairVOList", inProgressPlacedOrderPairVOList)
            .add("historyPlacedOrderPairVOList", historyPlacedOrderPairVOList)
            .add("mAsyncRestClientPlatformA", mAsyncRestClientPlatformA)
            .add("mAsyncRestClientPlatformB", mAsyncRestClientPlatformB)
            .add("baseCoin", baseCoin)
            .add("quoteCoin", quoteCoin)
            .add("maxTradeQtyQuoteCoin", maxTradeQtyQuoteCoin)
            .add("maxInprogressOrderPairNum", maxInprogressOrderPairNum)
            .add("stop", stop)
            .add("firstOrderRoundCount", firstOrderRoundCount)
            .add("profitThreshold", profitThreshold)
            .add("reqIntervalMillis", reqIntervalMillis)
            .add("accountBalanceQuoteCoinPlatformA", accountBalanceQuoteCoinPlatformA)
            .add("accountBalanceBaseCoinPlatformA", accountBalanceBaseCoinPlatformA)
            .add("accountBalanceQuoteCoinPlatformB", accountBalanceQuoteCoinPlatformB)
            .add("accountBalanceBaseCoinPlatformB", accountBalanceBaseCoinPlatformB)
            .toString();
    }
}
