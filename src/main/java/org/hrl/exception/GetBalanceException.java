/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/25
 */

package org.hrl.exception;

public class GetBalanceException extends Exception{

    public GetBalanceException() {
        super();
    }

    public GetBalanceException(String message) {
        super(message);
    }

    public GetBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetBalanceException(Throwable cause) {
        super(cause);
    }

    protected GetBalanceException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
