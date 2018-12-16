package org.hrl.domain;

public class ProfitableTradePairVO {

    private ProfitableTradeVO profitableTradeVO1;
    private ProfitableTradeVO profitableTradeVO2;

    public ProfitableTradePairVO(ProfitableTradeVO profitableTradeVO1, ProfitableTradeVO profitableTradeVO2) {
        this.profitableTradeVO1 = profitableTradeVO1;
        this.profitableTradeVO2 = profitableTradeVO2;
    }

    public ProfitableTradeVO getProfitableTradeVO1() {
        return profitableTradeVO1;
    }

    public void setProfitableTradeVO1(ProfitableTradeVO profitableTradeVO1) {
        this.profitableTradeVO1 = profitableTradeVO1;
    }

    public ProfitableTradeVO getProfitableTradeVO2() {
        return profitableTradeVO2;
    }

    public void setProfitableTradeVO2(ProfitableTradeVO profitableTradeVO2) {
        this.profitableTradeVO2 = profitableTradeVO2;
    }
}
