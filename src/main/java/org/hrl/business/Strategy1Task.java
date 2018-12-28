package org.hrl.business;

import org.hrl.exception.GetBalanceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strategy1Task implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Strategy1Task.class);

    private Strategy1 strategy1;

    public Strategy1Task(Strategy1 strategy1) {
        this.strategy1 = strategy1;
    }


    @Override
    public void run() {
        try {
            strategy1.firstOrderStrategy();
        } catch (GetBalanceException e) {
            LOGGER.error("STRAGETY TASK FAIL.", e);
            e.printStackTrace();
        } catch (Throwable t) {
            LOGGER.error("STRATEGY TASK EXIT.", t);
        }
    }
}
