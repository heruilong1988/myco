/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/29
 */

package org.hrl.api.req;

import org.hrl.api.rsp.MOrderSide;

public class MQueryOpenOrderRequest {

    private String accountId;
    private String baseCoin;
    private String quoteCoin;
    private String symbol;
    private MOrderSide mOrderSide;
    private int size = 500;


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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public MOrderSide getmOrderSide() {
        return mOrderSide;
    }

    public void setmOrderSide(MOrderSide mOrderSide) {
        this.mOrderSide = mOrderSide;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
