package org.hrl.domain;

import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("profitableTradeVO1", profitableTradeVO1)
                .add("profitableTradeVO2", profitableTradeVO2)
                .toString();
    }
}
