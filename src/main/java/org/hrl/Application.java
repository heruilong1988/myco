package org.hrl;

import org.hrl.api.MAsyncRestClient;
import org.hrl.api.binance.impl.MBinanceAsyncRestClientImpl;
import org.hrl.api.huobi.client.MHuobiAsyncRestClient;
import org.hrl.business.MultipleThreadStrategy1;
import org.hrl.business.MultipleThreadStrategy1DataCollect;
import org.hrl.config.AppConfig;
import org.hrl.config.PrecisionConfig;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@SpringBootApplication
public class Application {

    private static String PRO_CONF_DIR_PATH =
            System.getProperty("conf.dir", "src" + File.separator + "main" + File.separator + "resources");

    public static void main(String[] args) throws MalformedURLException, JSONException {

        ClassLoader loader = new URLClassLoader(new URL[]{new File(PRO_CONF_DIR_PATH).toURI().toURL()});
        Thread.currentThread().setContextClassLoader(loader);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

        AppConfig appConfig = applicationContext.getBean(AppConfig.class);

        if (appConfig.isProxyEnabled()) {
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "1080");
            //System.setProperty("socksProxyHost", "172.31.1.162");
            //System.setProperty("socksProxyPort", "1080");
        }

        /*
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        String API_KEY = appConfig.getHuobiAccessKey();
        String API_SECRET = appConfig.getHuobiSecretKey();

        /*String API_KEY = properties.getProperty("huobi-accesskey");
        String API_SECRET = properties.getProperty("huobi-secretkey");*/

        MAsyncRestClient huoBiAsyncRestClient = applicationContext.getBean(MHuobiAsyncRestClient.class);
        //MAsyncRestClient huoBiAsyncRestClient = new MHuobiAsyncRestClient(API_KEY, API_SECRET);

        //String BINANCE_API_KEY = appConfig.getBinanceAccessKey();
        //String BINANCE_API_SECRET = appConfig.getBinanceSecretKey();

        // String BINANCE_API_KEY = properties.getProperty("binance-accesskey");
        //String BINANCE_API_SECRET = properties.getProperty("binance-secretkey");

        //BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(BINANCE_API_KEY, BINANCE_API_SECRET);
        //BinanceApiRestClient binanceApiRestClient = factory.newRestClient();
        //MAsyncRestClient mBinanceAsyncRestClient = new MBinanceAsyncRestClientImpl(BINANCE_API_KEY, BINANCE_API_SECRET);
        MAsyncRestClient mBinanceAsyncRestClient = applicationContext.getBean(MBinanceAsyncRestClientImpl.class);

        double profitThreshold = appConfig.getProfitThreshold();
        long reqIntervalMillis = appConfig.getReqIntervalMillis();
        int maxTradeQtyQuoteCoin = appConfig.getMaxTradeQtyQuoteCoin();
        int maxInprogressOrderPairNum = appConfig.getMaxInprogressOrderPairNum();


        if (appConfig.isDataCollectEnabled()) {
            MultipleThreadStrategy1DataCollect multipleThreadStrategy1DataCollect = new MultipleThreadStrategy1DataCollect(
                    huoBiAsyncRestClient, mBinanceAsyncRestClient, appConfig.getBaseCoinArr().split(","), profitThreshold,
                    reqIntervalMillis, maxTradeQtyQuoteCoin, maxInprogressOrderPairNum);
            multipleThreadStrategy1DataCollect.start();
            System.out.println("multipleTreadStrategy1DataCollect started.===============================================");
        } else {

            MultipleThreadStrategy1 multipleThreadStrategy1 = new MultipleThreadStrategy1(huoBiAsyncRestClient,
                    mBinanceAsyncRestClient, appConfig.getBaseCoinArr().split(","), profitThreshold, reqIntervalMillis, maxTradeQtyQuoteCoin, maxInprogressOrderPairNum);
            multipleThreadStrategy1.start();
            System.out.println("multipleTreadStrategy1 started.===============================================");
        }

        String serviceName = "twoplatformcoin";
        System.out.println(String.format("The %s service started", serviceName));

    }
}
