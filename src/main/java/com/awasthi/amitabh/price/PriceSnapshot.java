package com.awasthi.amitabh.price;

import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.State;


public interface PriceSnapshot {

    Instrument getInstrument();

    Market getMarket();

    State getState();

    Price[] getPrices();
}
