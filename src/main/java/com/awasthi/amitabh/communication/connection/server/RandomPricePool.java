package com.awasthi.amitabh.communication.connection.server;

import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.Side;
import com.awasthi.amitabh.domain.State;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;
import com.awasthi.amitabh.price.impl.PriceImpl;
import com.awasthi.amitabh.price.impl.PriceSnapshotImpl;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class RandomPricePool {

    private static final Random RANDOM = new Random(47);

    private static final List<Price[]> quoteBookPool = Lists.newArrayList();

    static {
        populatePricesFor(0.75655);
        populatePricesFor(1.02345);
        populatePricesFor(0.87655);
        populatePricesFor(1.37456);
        populatePricesFor(1.03455);
        populatePricesFor(0.68755);
    }

    private static void populatePricesFor(double midRate) {
        final Price[] prices = new Price[10];
        for (int i = 1; i <= 5; i++) {
            prices[i - 1] = new PriceImpl(Side.BID, 1000000 * i, midRate - 0.0001 * i);
            prices[5 + i - 1] = new PriceImpl(Side.OFFER, 1000000 * i, midRate + 0.0001 * i);
        }
        quoteBookPool.add(prices);
    }

    private RandomPricePool() {

    }

    public static PriceSnapshot getRandomPriceFromPool() {
        final int marketOrd = RANDOM.nextInt(Market.VALUES.length);
        final int instrumentOrd = RANDOM.nextInt(Instrument.VALUES.length);
        final int stateOrd = RANDOM.nextInt(State.VALUES.length);

        final int priceIndex = RANDOM.nextInt(quoteBookPool.size());

        return new PriceSnapshotImpl(Instrument.VALUES[instrumentOrd],
                Market.VALUES[marketOrd],
                State.VALUES[stateOrd],
                quoteBookPool.get(priceIndex));
    }
}
