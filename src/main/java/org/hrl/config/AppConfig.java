package org.hrl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${proxy-enabled}")
    private boolean proxyEnabled;

    @Value("${datacollect-enabled}")
    private boolean dataCollectEnabled;

    @Value("${huobi-accesskey}")
    private String huobiAccessKey;

    @Value(("${huobi-secretkey}"))
    private String huobiSecretKey;

    @Value("${binance-accesskey}")
    private String binanceAccessKey;

    @Value("${binance-secretkey}")
    private String binanceSecretKey;

    @Value("${baseCoinArr}")
    private String baseCoinArr;

    @Value("${profit-threshold}")
    private double profitThreshold;

    @Value("${req-interval-millis}")
    private long reqIntervalMillis;

    @Value("${maxTradeQtyQuoteCoin}")
    private int maxTradeQtyQuoteCoin;

    @Value("${maxInprogressOrderPairNum}")
    private int maxInprogressOrderPairNum;

    public int getMaxTradeQtyQuoteCoin() {
        return maxTradeQtyQuoteCoin;
    }

    public void setMaxTradeQtyQuoteCoin(int maxTradeQtyQuoteCoin) {
        this.maxTradeQtyQuoteCoin = maxTradeQtyQuoteCoin;
    }

    public int getMaxInprogressOrderPairNum() {
        return maxInprogressOrderPairNum;
    }

    public void setMaxInprogressOrderPairNum(int maxInprogressOrderPairNum) {
        this.maxInprogressOrderPairNum = maxInprogressOrderPairNum;
    }

    public long getReqIntervalMillis() {
        return reqIntervalMillis;
    }

    public void setReqIntervalMillis(long reqIntervalMillis) {
        this.reqIntervalMillis = reqIntervalMillis;
    }

    public double getProfitThreshold() {
        return profitThreshold;
    }

    public void setProfitThreshold(double profitThreshold) {
        this.profitThreshold = profitThreshold;
    }

    public boolean isDataCollectEnabled() {
        return dataCollectEnabled;
    }

    public void setDataCollectEnabled(boolean dataCollectEnabled) {
        this.dataCollectEnabled = dataCollectEnabled;
    }

    public String getBaseCoinArr() {
        return baseCoinArr;
    }

    public void setBaseCoinArr(String baseCoinArr) {
        this.baseCoinArr = baseCoinArr;
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public String getHuobiAccessKey() {
        return huobiAccessKey;
    }

    public void setHuobiAccessKey(String huobiAccessKey) {
        this.huobiAccessKey = huobiAccessKey;
    }

    public String getHuobiSecretKey() {
        return huobiSecretKey;
    }

    public void setHuobiSecretKey(String huobiSecretKey) {
        this.huobiSecretKey = huobiSecretKey;
    }

    public String getBinanceAccessKey() {
        return binanceAccessKey;
    }

    public void setBinanceAccessKey(String binanceAccessKey) {
        this.binanceAccessKey = binanceAccessKey;
    }

    public String getBinanceSecretKey() {
        return binanceSecretKey;
    }

    public void setBinanceSecretKey(String binanceSecretKey) {
        this.binanceSecretKey = binanceSecretKey;
    }
}
