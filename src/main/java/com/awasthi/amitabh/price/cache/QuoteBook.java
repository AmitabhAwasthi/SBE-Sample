package com.awasthi.amitabh.price.cache;

import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.Side;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;

import java.util.List;

/**
 * Description:- This can be further broken into a QuoteBook (to only query the current quotebook) and a MutableQuoteBook (to apply incoming updates) depending on whether we want to
 * lazily calculate computed price or on each tick
 */
public interface QuoteBook {

    void applyUpdate(PriceSnapshot priceSnapshot);

    List<Price> getSortedPrices(Instrument instrument, Side side);

    List<Price> getSortedPrices(Instrument instrument, Market market, Side side);
}
