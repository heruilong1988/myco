/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/29
 */

package org.hrl.api.rsp;

public class MOpenOrder {
    //huobi
    private String orderId; //订单号
    private String symbol;
    private String createdAt; //timestamp
    private MOrderType mOrderType;
    private MOrderSide mOrderSide;
    private String price;
    private String fieldQuantity;
    private String fieldCashAmount;
    private String source;
    private MOrderStatus mOrderStatus;


    //binance
    private String clientOrderId;





}
