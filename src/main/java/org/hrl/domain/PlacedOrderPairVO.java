package org.hrl.domain;

import com.google.common.base.MoreObjects;

public class PlacedOrderPairVO {

    private PlacedOrderVO placedOrderVO1;
    private PlacedOrderVO placedOrderVO2;

    public PlacedOrderPairVO(PlacedOrderVO placedOrderVO1, PlacedOrderVO placedOrderVO2) {
        this.placedOrderVO1 = placedOrderVO1;
        this.placedOrderVO2 = placedOrderVO2;
    }

    public PlacedOrderVO getPlacedOrderVO1() {
        return placedOrderVO1;
    }

    public void setPlacedOrderVO1(PlacedOrderVO placedOrderVO1) {
        this.placedOrderVO1 = placedOrderVO1;
    }

    public PlacedOrderVO getPlacedOrderVO2() {
        return placedOrderVO2;
    }

    public void setPlacedOrderVO2(PlacedOrderVO placedOrderVO2) {
        this.placedOrderVO2 = placedOrderVO2;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("placedOrderVO1", placedOrderVO1)
                .add("placedOrderVO2", placedOrderVO2)
                .toString();
    }
}
