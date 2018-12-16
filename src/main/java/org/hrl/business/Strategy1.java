package org.hrl.business;

import com.google.common.base.MoreObjects;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.rsp.*;
import org.hrl.domain.PlacedOrderPairVO;
import org.hrl.domain.PlacedOrderVO;
import org.hrl.domain.ProfitableTradePairVO;
import org.hrl.domain.ProfitableTradeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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

    private double maxTradeQty = 150;
    private int maxInprogressOrderPairNum = 10;

    private boolean stop = false;
    private long firstOrderRoundCount = 0;

    public Strategy1(MAsyncRestClient mAsyncRestClientPlatformA, MAsyncRestClient mAsyncRestClientPlatformB, String baseCoin, String quoteCoin) {
        this.mAsyncRestClientPlatformA = mAsyncRestClientPlatformA;
        this.mAsyncRestClientPlatformB = mAsyncRestClientPlatformB;
        this.baseCoin = baseCoin;
        this.quoteCoin = quoteCoin;

        LOGGER = LoggerFactory.getLogger(baseCoin);
        ORDERLOGGER = LoggerFactory.getLogger(baseCoin + ".Order");
        LOGGER.info(this.toString());

    }

    public void dataCollect() {
        while (true) {
            try {
                long startNs = System.nanoTime();
                calcDepthProfit();
                long duration = System.nanoTime() - startNs;

                //1秒2次
                if (duration < TimeUnit.SECONDS.toNanos(1) / 2) {
                    try {
                        long sleepTime = (TimeUnit.SECONDS.toNanos(1) / 2 - duration) / 1000000;

                        //System.out.println(sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                LOGGER.info("businessone Exception", e);
            }
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
            LOGGER.info("huobi depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.info("huobi depth ExecutionException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        }

        try {
            binanceDepth = binanceDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("binance depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.info("binance depth InterruptedException.baseCoin:{},quoteCoin:{}", baseCoin, quoteCoin, e);
        }

        if (huobiDepth == null || binanceDepth == null) {
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
        double profit1 = priceDiff - (huobiBid1.getPrice() * mAsyncRestClientPlatformA.getTradeFee() + binanceAsk1.getPrice() * mAsyncRestClientPlatformB.getTradeFee());

        double priceDiff2 = binanceBid1.getPrice() - huobiAsk1.getPrice();
        double profit2 = priceDiff2 - (binanceBid1.getPrice() * mAsyncRestClientPlatformB.getTradeFee() + huobiAsk1.getPrice() * mAsyncRestClientPlatformA.getTradeFee());

        if (profit1 > 0) {
            LOGGER.info("profit:{},huobiBid:{},binanceAsk:{}", profit1, huobiBid1, binanceAsk1);
        }

        if (profit2 > 0) {
            LOGGER.info("profit2:{},binanceBid:{},huobiAsk:{}", profit2, binanceBid1, huobiAsk1);
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


    public void firstOrderStrategy() {
        while (!stop) {
            try {
                List<ProfitableTradePairVO> profitableTradePairVOList = calcProfit();

                if (inProgressPlacedOrderPairVOList.size() < maxInprogressOrderPairNum) {
                    List<PlacedOrderPairVO> placedOrderPairVOList = placeProfitableTradePairOrderList(profitableTradePairVOList);
                    inProgressPlacedOrderPairVOList.addAll(placedOrderPairVOList);
                }

                if (!inProgressPlacedOrderPairVOList.isEmpty()) {
                    checkInprogressPlacedOrderPairList();
                }

                ++firstOrderRoundCount;

                if (firstOrderRoundCount % 100 == 0) {
                    LOGGER.info("inprogressPlacedOrderSize:{},inprogressPlacedOrderList:{}", inProgressPlacedOrderPairVOList.size(), inProgressPlacedOrderPairVOList);
                }
            } catch (Exception e) {
                LOGGER.info("fail firstOrderRound", e);
            }
        }

    }


    public void checkInprogressPlacedOrderPairList() {
        if (inProgressPlacedOrderPairVOList.isEmpty()) {
            ORDERLOGGER.info("inProgressPlacedOrderPairVOList is empty.");
            return;
        }

        Iterator<PlacedOrderPairVO> iterator = inProgressPlacedOrderPairVOList.iterator();

        for (; iterator.hasNext(); ) {
            PlacedOrderPairVO placedOrderPairVO = iterator.next();
            PlacedOrderVO placedOrderVO1 = placedOrderPairVO.getPlacedOrderVO1();
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

            if (placedOrderVO1.isFinished() && placedOrderVO2.isFinished()) {
                historyPlacedOrderPairVOList.add(placedOrderPairVO);
                iterator.remove();
            }
        }

    }


    /**
     * @param profitableTradePairVOS
     * @return list of PlacedOrderPair, placedOrderVO will be null when place order encountering exception
     */
    public List<PlacedOrderPairVO> placeProfitableTradePairOrderList(List<ProfitableTradePairVO> profitableTradePairVOS) {

        List<PlacedOrderPairVO> placedOrderPairVOList = new ArrayList<>();

        if (profitableTradePairVOS == null || profitableTradePairVOS.isEmpty()) {
            LOGGER.debug("no profitableTradePairVOS.");
            return placedOrderPairVOList;
        }

        for (ProfitableTradePairVO profitableTradePairVO : profitableTradePairVOS) {
            ProfitableTradeVO profitableTradeVO1 = profitableTradePairVO.getProfitableTradeVO1();
            ProfitableTradeVO profitableTradeVO2 = profitableTradePairVO.getProfitableTradeVO2();

            FutureTask<MPlaceOrderRsp> profitableTradeRspFuture1 = profitableTradeVO1.asyncPlaceOrder();
            FutureTask<MPlaceOrderRsp> profitbaleTradeRspFuture2 = profitableTradeVO2.asyncPlaceOrder();

            PlacedOrderVO placedOrderVO1 = null;
            MPlaceOrderRsp mPlaceOrderRsp1 = null;
            try {
                mPlaceOrderRsp1 = profitableTradeRspFuture1.get();
                placedOrderVO1 = new PlacedOrderVO(profitableTradeVO1.getmAsyncRestClient(), profitableTradeVO1, mPlaceOrderRsp1);
            } catch (InterruptedException e) {
                LOGGER.info("fail place order.profitbaleTradeVO1:{},mPlaceOrderRsp1:{}", profitableTradeVO1, mPlaceOrderRsp1, e);
            } catch (ExecutionException e) {
                LOGGER.info("fail place order.profitbaleTradeVO1:{},mPlaceOrderRsp1:{}", profitableTradeVO1, mPlaceOrderRsp1, e);
            }

            PlacedOrderVO placedOrderVO2 = null;
            MPlaceOrderRsp mPlaceOrderRsp2 = null;
            try {
                mPlaceOrderRsp2 = profitbaleTradeRspFuture2.get();
                placedOrderVO2 = new PlacedOrderVO(profitableTradeVO2.getmAsyncRestClient(), profitableTradeVO2, mPlaceOrderRsp2);
            } catch (InterruptedException e) {
                LOGGER.info("fail place order.profitbaleTradeVO2:{},mPlaceOrderRsp2:{}", profitableTradeVO2, mPlaceOrderRsp2, e);
            } catch (ExecutionException e) {
                LOGGER.info("fail place order.profitbaleTradeVO2:{},mPlaceOrderRsp2:{}", profitableTradeVO2, mPlaceOrderRsp2, e);
            }

            PlacedOrderPairVO placedOrderPairVO = new PlacedOrderPairVO(placedOrderVO1, placedOrderVO2);
            placedOrderPairVOList.add(placedOrderPairVO);
        }

        return placedOrderPairVOList;

    }


    public List<ProfitableTradePairVO> calcProfit() {
        List<ProfitableTradePairVO> profitableTradePairVOS = new ArrayList<>();

        FutureTask<MDepth> huobiDepthFuture = mAsyncRestClientPlatformA.depth(baseCoin, quoteCoin);
        FutureTask<MDepth> binanceDepthFuture = mAsyncRestClientPlatformB.depth(baseCoin, quoteCoin);

        MDepth huobiDepth = null;
        MDepth binanceDepth = null;
        try {
            huobiDepth = huobiDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("platformA depth InterruptedException.baseCoin:{},quoteCoin:{},platformA:{}", baseCoin, quoteCoin, mAsyncRestClientPlatformA, e);
        } catch (ExecutionException e) {
            LOGGER.info("platformA depth ExecutionException.baseCoin:{},quoteCoin:{},platformA:{}", baseCoin, quoteCoin, mAsyncRestClientPlatformA, e);
        }

        try {
            binanceDepth = binanceDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("platformB depth InterruptedException.baseCoin:{},quoteCoin:{},platformB:{}", baseCoin, quoteCoin, mAsyncRestClientPlatformB, e);
        } catch (ExecutionException e) {
            LOGGER.info("platformB depth InterruptedException.baseCoin:{},quoteCoin:{},platformB:{}", baseCoin, quoteCoin, mAsyncRestClientPlatformB, e);
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
        double profit1 = priceDiff - (huobiBid1.getPrice() * mAsyncRestClientPlatformA.getTradeFee() + binanceAsk1.getPrice() * mAsyncRestClientPlatformB.getTradeFee());

        if (profit1 > 0) {

            double tradeAskPrice = huobiBid1.getPrice();
            double tradeQuantity = calcMinQuantity(huobiBid1, binanceAsk1, maxTradeQty);
            ProfitableTradeVO profitableTradeVO = new ProfitableTradeVO(mAsyncRestClientPlatformA, baseCoin, quoteCoin, tradeAskPrice, tradeQuantity, MOrderSide.sell, MOrderType.limit);

            double tradeBidPrice = binanceAsk1.getPrice();
            ProfitableTradeVO profitableTradeVO1 = new ProfitableTradeVO(mAsyncRestClientPlatformB, baseCoin, quoteCoin, tradeBidPrice, tradeQuantity, MOrderSide.buy, MOrderType.limit);

            ProfitableTradePairVO profitableTradePairVO = new ProfitableTradePairVO(profitableTradeVO, profitableTradeVO1);

            profitableTradePairVOS.add(profitableTradePairVO);

            LOGGER.info("profit:{},platformA-Bid:{},platformB-Ask:{},platformA:{},platformB:{}", profit1, huobiBid1, binanceAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        double priceDiff2 = binanceBid1.getPrice() - huobiAsk1.getPrice();
        double profit2 = priceDiff2 - (binanceBid1.getPrice() * mAsyncRestClientPlatformB.getTradeFee() + huobiAsk1.getPrice() * mAsyncRestClientPlatformA.getTradeFee());

        if (profit2 > 0) {

            double tradeAskPrice = binanceBid1.getPrice();
            double tradeQuantity = calcMinQuantity(binanceBid1, huobiAsk1, maxTradeQty);
            ProfitableTradeVO profitableTradeVO = new ProfitableTradeVO(mAsyncRestClientPlatformB, baseCoin, quoteCoin, tradeAskPrice, tradeQuantity, MOrderSide.sell, MOrderType.limit);

            double tradeBidPrice = huobiAsk1.getPrice();
            ProfitableTradeVO profitableTradeVO1 = new ProfitableTradeVO(mAsyncRestClientPlatformA, baseCoin, quoteCoin, tradeBidPrice, tradeQuantity, MOrderSide.buy, MOrderType.limit);

            ProfitableTradePairVO profitableTradePairVO = new ProfitableTradePairVO(profitableTradeVO, profitableTradeVO1);
            profitableTradePairVOS.add(profitableTradePairVO);

            LOGGER.info("profit2:{},platformB-Bid:{},platformA-Ask:{},platformA:{},platformB:{}", profit2, binanceBid1, huobiAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        businessCount++;
        if (businessCount % 100 == 0) {
            LOGGER.info("businessCount:{}", businessCount);
        }

        if (businessCount % 1000 == 0) {
            LOGGER.info("DEBUG:profit:{},platformA-Bid:{},platformB-Ask:{},platformA:{},platformB:{}", profit1, huobiBid1, binanceAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
            LOGGER.info("DEBUG:profit2:{},platformB-Bid:{},platformA-Ask:{},platformA:{},platformB:{}", profit2, binanceBid1, huobiAsk1, mAsyncRestClientPlatformA, mAsyncRestClientPlatformB);
        }

        return profitableTradePairVOS;
    }

    private double calcMinQuantity(PriceQtyPair pair1, PriceQtyPair pair2, double maxTradeQtyQuoteCoin) {
        double minPairQty = Math.min(pair1.getQty(), pair2.getQty());
        double maxTradeQtyBaseCoin = maxTradeQtyQuoteCoin / ((pair1.getPrice() + pair2.getPrice()) / 2.0);
        double minQty = Math.min(minPairQty, maxTradeQtyBaseCoin);
        return minQty;
    }

    public boolean isOrderFinished(MOrderStatus mOrderStatus) {
        if (mOrderStatus == null || MOrderStatus.NEW.equals(mOrderStatus) || MOrderStatus.PARTIALLY_FILLED.equals(mOrderStatus)) {
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mAsyncRestClientPlatformA", mAsyncRestClientPlatformA)
                .add("mAsyncRestClientPlatformB", mAsyncRestClientPlatformB)
                .add("baseCoin", baseCoin)
                .add("quoteCoin", quoteCoin)
                .toString();
    }


}
