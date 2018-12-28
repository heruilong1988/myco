package org.hrl.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.hrl.api.MAsyncRestClient;
import org.hrl.api.req.MPlaceOrderRequest;
import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderType;
import org.hrl.api.rsp.MPlaceOrderRsp;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ProfitableTradeVO {

    private MAsyncRestClient mAsyncRestClient;

    private String baseCoin;
    private String quoteCoin;
    private double price;
    private double quantity;

    private MOrderSide mOrderSide;
    private MOrderType mOrderType;


    public ProfitableTradeVO(MAsyncRestClient mAsyncRestClient, String baseCoin, String quoteCoin, double price, double quantity, MOrderSide mOrderSide, MOrderType mOrderType) {
        this.mAsyncRestClient = mAsyncRestClient;
        this.baseCoin = baseCoin;
        this.quoteCoin = quoteCoin;
        this.price = price;
        this.quantity = quantity;
        this.mOrderSide = mOrderSide;
        this.mOrderType = mOrderType;
    }

    public MAsyncRestClient getmAsyncRestClient() {
        return mAsyncRestClient;
    }

    public void setmAsyncRestClient(MAsyncRestClient mAsyncRestClient) {
        this.mAsyncRestClient = mAsyncRestClient;
    }

    public String getBaseCoin() {
        return baseCoin;
    }

    public void setBaseCoin(String baseCoin) {
        this.baseCoin = baseCoin;
    }

    public String getQuoteCoin() {
        return quoteCoin;
    }

    public void setQuoteCoin(String quoteCoin) {
        this.quoteCoin = quoteCoin;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public MOrderSide getmOrderSide() {
        return mOrderSide;
    }

    public void setmOrderSide(MOrderSide mOrderSide) {
        this.mOrderSide = mOrderSide;
    }

    public MOrderType getmOrderType() {
        return mOrderType;
    }

    public void setmOrderType(MOrderType mOrderType) {
        this.mOrderType = mOrderType;
    }

    public FutureTask<MPlaceOrderRsp> asyncPlaceOrder() {
        MPlaceOrderRequest mPlaceOrderRequest = new MPlaceOrderRequest();
        mPlaceOrderRequest.setBaseCoin(baseCoin);
        mPlaceOrderRequest.setQuoteCoin(quoteCoin);
        mPlaceOrderRequest.setPrice(price);
        mPlaceOrderRequest.setQuantity(quantity);
        mPlaceOrderRequest.setSide(mOrderSide);
        mPlaceOrderRequest.setType(mOrderType);

        FutureTask<MPlaceOrderRsp> mPlaceOrderRspFuture = mAsyncRestClient.placeOrder(mPlaceOrderRequest);

        return mPlaceOrderRspFuture;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mAsyncRestClient", mAsyncRestClient)
                .add("baseCoin", baseCoin)
                .add("quoteCoin", quoteCoin)
                .add("price", price)
                .add("quantity", quantity)
                .add("mOrderSide", mOrderSide)
                .add("mOrderType", mOrderType)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        ProfitableTradeVO o =  (ProfitableTradeVO) obj;
        return Objects.equal(this.getBaseCoin(), o.getBaseCoin()) &&
            Objects.equal(this.getQuoteCoin(), o.getQuoteCoin()) &&
            Objects.equal(this.getmAsyncRestClient(), o.getmAsyncRestClient()) &&
            Objects.equal(this.getmOrderSide(), o.getmOrderSide()) &&
            Objects.equal(this.getmOrderType(), o.getmOrderType()) &&
            Objects.equal(this.getPrice(), o.getPrice()) &&
            Objects.equal(this.getQuantity(), o.getQuantity());
    }
}
