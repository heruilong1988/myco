package org.hrl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrecisionConfig {

    @Value("${huobi-precision}")
    private String huobiPrecisionStr;

    @Value(("${binance-precision}"))
    private String binancePrecisionStr;

    public String getHuobiPrecisionStr() {
        return huobiPrecisionStr;
    }

    public void setHuobiPrecisionStr(String huobiPrecisionStr) {
        this.huobiPrecisionStr = huobiPrecisionStr;
    }

    public String getBinancePrecisionStr() {
        return binancePrecisionStr;
    }

    public void setBinancePrecisionStr(String binancePrecisionStr) {
        this.binancePrecisionStr = binancePrecisionStr;
    }
}
