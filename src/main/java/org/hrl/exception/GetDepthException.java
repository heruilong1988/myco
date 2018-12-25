/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/25
 */

package org.hrl.exception;

public class GetDepthException extends Exception{

    public GetDepthException() {
        super();
    }

    public GetDepthException(String message) {
        super(message);
    }

    public GetDepthException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetDepthException(Throwable cause) {
        super(cause);
    }

    protected GetDepthException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
