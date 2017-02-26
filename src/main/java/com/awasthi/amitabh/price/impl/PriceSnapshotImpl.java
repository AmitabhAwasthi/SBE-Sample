package com.awasthi.amitabh.price.impl;

import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.State;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;

import java.util.Arrays;

public class PriceSnapshotImpl implements PriceSnapshot {

    private final Instrument instrument;
    private final Market market;
    private final State state;
    private final Price[] prices;

    public PriceSnapshotImpl(Instrument instrument,
                             Market market,
                             State state,
                             Price[] prices) {
        this.instrument = instrument;
        this.market = market;
        this.state = state;
        this.prices = prices;
    }


    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public Market getMarket() {
        return market;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public Price[] getPrices() {
        return prices;
    }

    @Override
    public String toString() {
        return "PriceSnapshotImpl{" +
                "instrument=" + instrument +
                ", market=" + market +
                ", state=" + state +
                ", prices=" + Arrays.toString(prices) +
                '}';
    }
}
