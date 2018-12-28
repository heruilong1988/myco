/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/12
 */

package org.hrl.api.client;

import org.hrl.api.MAsyncRestClient;
import org.hrl.api.binance.impl.MBinanceAsyncRestClientImpl;
import org.hrl.api.huobi.client.MHuobiAsyncRestClient;
import java.io.IOException;
import java.util.Properties;

public class MApiRestClientFactory {

    private static MApiRestClientFactory instance = new MApiRestClientFactory();

    Properties properties = new Properties();

    private MAsyncRestClient huobiAsyncRestClient;
    private MAsyncRestClient binanceAsyncRestClient;

    private MApiRestClientFactory(){
        init();
    }

    private void init(){

        try {

            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String huobiAccessKey = properties.getProperty("huobi-accesskey");
        String huobiSecretKey = properties.getProperty("huobi-secretkey");

        String binanceAccessKey = properties.getProperty("binance-accesskey");
        String binanceSecretKey = properties.getProperty("binance-secretkey");

        huobiAsyncRestClient = new MHuobiAsyncRestClient(huobiAccessKey, huobiSecretKey,null);

        binanceAsyncRestClient = new MBinanceAsyncRestClientImpl(binanceAccessKey, binanceSecretKey, null);

    }

    public MAsyncRestClient getAsyncRestClient(String platform) {
        if("huobi".equals(platform)) {
            return huobiAsyncRestClient;
        } else if("binance".equals(platform)) {
            return binanceAsyncRestClient;
        }

        return huobiAsyncRestClient;
    }
}
