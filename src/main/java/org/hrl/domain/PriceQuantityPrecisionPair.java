/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/26
 */

package org.hrl.domain;

public class PriceQuantityPrecisionPair {

    private int pricePrecision;
    private int quantityPrecision;

    public PriceQuantityPrecisionPair(int pricePrecision, int quantityPrecision) {
        this.pricePrecision = pricePrecision;
        this.quantityPrecision = quantityPrecision;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public int getQuantityPrecision() {
        return quantityPrecision;
    }

    public void setQuantityPrecision(int quantityPrecision) {
        this.quantityPrecision = quantityPrecision;
    }
}
