package org.hrl.domain;

import com.google.common.base.MoreObjects;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.huobi.task.QueryOrderTask;
import org.hrl.api.req.MQueryOrderRequest;
import org.hrl.api.rsp.MPlaceOrderRsp;
import org.hrl.api.rsp.MQueryOrderRsp;
import org.hrl.exception.QueryOrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class PlacedOrderVO {

    private Logger ORDERLOG = null;

    private MAsyncRestClient mAsyncRestClient;

    private ProfitableTradeVO profitableTradeVO;
    private MPlaceOrderRsp mPlaceOrderRsp;

    private MQueryOrderRsp mQueryOrderRsp;

    //first time set to false
    private boolean finished = false;

    //first time set to zero
    private double lastFieldQuantity = 0.0;



    public PlacedOrderVO(MAsyncRestClient mAsyncRestClient, ProfitableTradeVO profitableTradeVO, MPlaceOrderRsp mPlaceOrderRsp) {
        this.profitableTradeVO = profitableTradeVO;
        this.mAsyncRestClient = mAsyncRestClient;
        this.mPlaceOrderRsp = mPlaceOrderRsp;
        ORDERLOG = LoggerFactory.getLogger(profitableTradeVO.getBaseCoin() + ".Order");
    }


    public double getLastFieldQuantity() {
        return lastFieldQuantity;
    }

    public void setLastFieldQuantity(double lastFieldQuantity) {
        this.lastFieldQuantity = lastFieldQuantity;
    }

    public ProfitableTradeVO getProfitableTradeVO() {
        return profitableTradeVO;
    }

    public void setProfitableTradeVO(ProfitableTradeVO profitableTradeVO) {
        this.profitableTradeVO = profitableTradeVO;
    }

    public MAsyncRestClient getmAsyncRestClient() {
        return mAsyncRestClient;
    }

    public void setmAsyncRestClient(MAsyncRestClient mAsyncRestClient) {
        this.mAsyncRestClient = mAsyncRestClient;
    }

    public MPlaceOrderRsp getmPlaceOrderRsp() {
        return mPlaceOrderRsp;
    }

    public void setmPlaceOrderRsp(MPlaceOrderRsp mPlaceOrderRsp) {
        this.mPlaceOrderRsp = mPlaceOrderRsp;
    }

    public MQueryOrderRsp getmQueryOrderRsp() {
        return mQueryOrderRsp;
    }

    public void setmQueryOrderRsp(MQueryOrderRsp mQueryOrderRsp) {
        this.mQueryOrderRsp = mQueryOrderRsp;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public MQueryOrderRsp asyncQueryOrder() throws QueryOrderException {
        if (mPlaceOrderRsp != null) {
            MQueryOrderRequest mQueryOrderRequest = new MQueryOrderRequest();
            mQueryOrderRequest.setOrderId(mPlaceOrderRsp.getOrderId());
            mQueryOrderRequest.setBaseCoin(profitableTradeVO.getBaseCoin());
            mQueryOrderRequest.setQuoteCoin(profitableTradeVO.getQuoteCoin());

            FutureTask<MQueryOrderRsp> mQueryOrderRspFutureTask = mAsyncRestClient.queryOrder(mQueryOrderRequest);
            try {
                MQueryOrderRsp mQueryOrderRsp = mQueryOrderRspFutureTask.get();
                this.mQueryOrderRsp = mQueryOrderRsp;
                return mQueryOrderRsp;
            } catch (InterruptedException e) {
                ORDERLOG.info("fail query placedOrder. placedOrderVO:{}", this);
                throw new QueryOrderException(e);
            } catch (ExecutionException e) {
                ORDERLOG.info("fail query placedOrder. placedOrderVO:{}", this);
                throw new QueryOrderException(e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("mAsyncRestClient", mAsyncRestClient)
            .add("profitableTradeVO", profitableTradeVO)
            .add("mPlaceOrderRsp", mPlaceOrderRsp)
            .add("mQueryOrderRsp", mQueryOrderRsp)
            .add("finished", finished)
            .add("lastFieldQuantity", lastFieldQuantity)
            .toString();
    }
}

