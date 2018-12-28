/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/26
 */

package org.hrl.domain;

import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pricePrecision", pricePrecision)
                .add("quantityPrecision", quantityPrecision)
                .toString();
    }
}
