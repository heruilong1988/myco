/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/25
 */

package org.hrl.exception;

public class PlaceOrderException extends Exception{

    public PlaceOrderException() {
        super();
    }

    public PlaceOrderException(String message) {
        super(message);
    }

    public PlaceOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaceOrderException(Throwable cause) {
        super(cause);
    }

    protected PlaceOrderException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
