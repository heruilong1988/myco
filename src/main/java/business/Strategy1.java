package business;

import api.MAsyncRestClient;
import api.rsp.MDepth;
import api.rsp.PriceQtyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Strategy1 {

    Logger  LOGGER = LoggerFactory.getLogger(Strategy1.class);

    private MAsyncRestClient mAsyncRestClientHuobi;
    private MAsyncRestClient mAsyncRestClientBinance;

    private String baseCoin;
    private String quoteCoin;

    public  void businessone() {
        FutureTask<MDepth>  huobiDepthFuture = mAsyncRestClientHuobi.depth(baseCoin, quoteCoin);
        FutureTask<MDepth> binanceDepthFuture = mAsyncRestClientBinance.depth(baseCoin, quoteCoin);

        MDepth huobiDepth = null;
        MDepth binanceDepth = null;
        try {
            huobiDepth = huobiDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("huobi depth InterruptedException.baseCoin:{},quoteCoin:{}",baseCoin,quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.info("huobi depth ExecutionException.baseCoin:{},quoteCoin:{}",baseCoin,quoteCoin, e);
        }

        try {
            binanceDepth = binanceDepthFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("binance depth InterruptedException.baseCoin:{},quoteCoin:{}",baseCoin,quoteCoin, e);
        } catch (ExecutionException e) {
            LOGGER.info("binance depth InterruptedException.baseCoin:{},quoteCoin:{}",baseCoin,quoteCoin, e);
        }

        if(huobiDepth == null || binanceDepth == null) {
            //continue next round
        }

        List<PriceQtyPair> huobiAskList = huobiDepth.getAsks();
        List<PriceQtyPair> huobiBidList = huobiDepth.getBids();

        List<PriceQtyPair> binanceAskList = binanceDepth.getAsks();
        List<PriceQtyPair> binanceBidList = binanceDepth.getBids();


        PriceQtyPair huobiBid1 = huobiBidList.get(0);
        PriceQtyPair binanceAsk1 = binanceAskList.get(0);


        PriceQtyPair huobiAsk1 = huobiAskList.get(0);
        PriceQtyPair binanceBid1 = binanceBidList.get(0);


        double







    }

}
