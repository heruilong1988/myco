package org.hrl.business;

public class Strategy1Task implements Runnable{

    private  Strategy1 strategy1;

    public Strategy1Task(Strategy1 strategy1) {
        this.strategy1 = strategy1;
    }


    @Override
    public void run() {
        strategy1.firstOrderStrategy();
    }
}
