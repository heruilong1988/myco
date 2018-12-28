/*
 * Copyright (c) 2018, TP-Link Co.,Ltd.
 * Author: heruilong <heruilong@tp-link.com.cn>
 * Created: 2018/12/12
 */

package org.hrl.api.binance.task;

import org.hrl.api.binance.BinanceApiRestClient;
import org.hrl.api.binance.domain.account.Account;
import org.hrl.api.binance.domain.account.AssetBalance;
import org.hrl.api.rsp.MBalance;
import org.hrl.api.rsp.MCurrencyBalance;
import org.hrl.api.rsp.MGetBalanceRsp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BinanceGetBalanceTask implements Callable<MGetBalanceRsp> {

    private BinanceApiRestClient binanceApiRestClient;


    public BinanceGetBalanceTask(BinanceApiRestClient binanceApiRestClient) {
        this.binanceApiRestClient = binanceApiRestClient;
    }

    @Override
    public MGetBalanceRsp call() throws Exception {

        Account account = binanceApiRestClient.getAccount();

        List<AssetBalance> balances = account.getBalances();

        MGetBalanceRsp mGetBalanceRsp = new MGetBalanceRsp();
        MBalance mBalance = new MBalance();
        Map<String, MCurrencyBalance> mCurrencyBalanceMap = new HashMap<>();
        for(AssetBalance balance : balances) {
            String asset = balance.getAsset();
            String free = balance.getFree();
            String locked = balance.getLocked();

            MCurrencyBalance mCurrencyBalance = new MCurrencyBalance();
            mCurrencyBalance.setCurrency(asset.toLowerCase());
            mCurrencyBalance.setFrozenBalance(Double.parseDouble(locked));
            mCurrencyBalance.setTradeBalance(Double.parseDouble(free));

            mCurrencyBalanceMap.put(asset.toLowerCase(), mCurrencyBalance);
        }

        mBalance.setmCurrencyBalanceMap(mCurrencyBalanceMap);
        mGetBalanceRsp.setmBalance(mBalance);
        return mGetBalanceRsp;
    }
}
