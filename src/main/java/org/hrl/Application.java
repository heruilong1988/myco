package org.hrl;

import org.hrl.api.MAsyncRestClient;
import org.hrl.api.binance.BinanceApiClientFactory;
import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.impl.MBinanceAsyncRestClientImpl;
import org.hrl.api.huobi.client.MHuobiAsyncRestClient;
import org.hrl.business.MultipleThreadStrategy1;
import org.hrl.business.MultipleThreadStrategy1DataCollect;
import org.hrl.business.Strategy1DataCollectTask;
import org.hrl.business.Strategy1;
import org.hrl.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    private static String PRO_CONF_DIR_PATH =
            System.getProperty("conf.dir", "src" + File.separator + "main" + File.separator + "resources");

    public static void main(String[] args) throws MalformedURLException {

        ClassLoader loader = new URLClassLoader(new URL[] { new File(PRO_CONF_DIR_PATH).toURI().toURL() });
        Thread.currentThread().setContextClassLoader(loader);

        ConfigurableApplicationContext applicationContext =  SpringApplication.run(Application.class, args);

        AppConfig appConfig = applicationContext.getBean(AppConfig.class);

        if(appConfig.isProxyEnabled()) {
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "1080");
        }

        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sys.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String API_KEY = properties.getProperty("huobi-accesskey");
        String API_SECRET = properties.getProperty("huobi-secretkey");
        MAsyncRestClient huoBiAsyncRestClient  = new MHuobiAsyncRestClient(API_KEY,API_SECRET);

        String BINANCE_API_KEY = properties.getProperty("binance-accesskey");
        String BINANCE_API_SECRET = properties.getProperty("binance-secretkey");

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(BINANCE_API_KEY, BINANCE_API_SECRET);
        BinanceApiRestClient binanceApiRestClient  = factory.newRestClient();
        MAsyncRestClient mBinanceAsyncRestClient = new MBinanceAsyncRestClientImpl(binanceApiRestClient);


        if(appConfig.isDataCollectEnabled()) {
            MultipleThreadStrategy1DataCollect multipleThreadStrategy1DataCollect = new MultipleThreadStrategy1DataCollect(huoBiAsyncRestClient,mBinanceAsyncRestClient, appConfig.getBaseCoinArr().split(","));
            multipleThreadStrategy1DataCollect.start();
        } else {

            MultipleThreadStrategy1 multipleThreadStrategy1 = new MultipleThreadStrategy1(huoBiAsyncRestClient, mBinanceAsyncRestClient);
            multipleThreadStrategy1.start();
        }

        String serviceName ="twoplatformcoin";
        System.out.println(String.format("The %s service started", serviceName));

    }
}
