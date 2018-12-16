package org.hrl.api.huobi.util;


import org.hrl.api.rsp.MOrderSide;
import org.hrl.api.rsp.MOrderStatus;
import org.hrl.api.rsp.MOrderType;

public class Utils {

    public static String toSymbol(String baseCoin, String quoteCoin) {
        return baseCoin+quoteCoin;
    }

    public static String toOrderType(MOrderSide side, MOrderType type) {
        return side.toString() + "-" + type.toString();
    }

    public static MOrderStatus toStatus(String state) {

        switch (state) {
            case "submitting":
            case "submitted":
                return MOrderStatus.NEW;
            case "partial-filled":
                return MOrderStatus.PARTIALLY_FILLED;
            case "partial-cancelled":
                return MOrderStatus.PARTIAL_CANCELED;
            case "filled":
                return MOrderStatus.FILLED;
            case "canceled":
                return MOrderStatus.CANCELED;
            default:
                return MOrderStatus.UNDEFINED;
        }
    }
}
