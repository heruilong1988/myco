package org.hrl.domain;

public class ExchangeInfo {


    private int pricePrecision;
    private int quantityPrecision;
    private double minPrice;
    private double minQuantity;

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

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(double minQuantity) {
        this.minQuantity = minQuantity;
    }
}
