/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/25
 */

package org.hrl.exception;

public class QueryOrderException extends Exception{

    public QueryOrderException() {
        super();
    }

    public QueryOrderException(String message) {
        super(message);
    }

    public QueryOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryOrderException(Throwable cause) {
        super(cause);
    }

    protected QueryOrderException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
