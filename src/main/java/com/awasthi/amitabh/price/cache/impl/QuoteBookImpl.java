package com.awasthi.amitabh.price.cache.impl;

import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.Side;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;
import com.awasthi.amitabh.price.cache.QuoteBook;
import com.awasthi.amitabh.util.MathUtil;
import com.google.common.collect.Lists;

import java.util.*;

public class QuoteBookImpl implements QuoteBook {

    private final Comparator<Price> BID_SORTER = new BidPriceComparator();
    private final Comparator<Price> OFFERS_SORTER = new OfferPriceComparator();

    private final EnumMap<Instrument, EnumMap<Market, MutableQuoteBook>> priceCache =
            new EnumMap<>(Instrument.class);

    public QuoteBookImpl() {
        for (Instrument instrument : Instrument.VALUES) {
            final EnumMap<Market, MutableQuoteBook> instrumentQuoteCache = new EnumMap<>(Market.class);
            for (Market market : Market.VALUES) {
                instrumentQuoteCache.put(market, new MutableQuoteBook(Lists.newArrayList(), Lists.newArrayList()));
            }
            priceCache.put(instrument, instrumentQuoteCache);
        }
    }

    public void applyUpdate(PriceSnapshot priceSnapshot) {
        final Instrument instrument = priceSnapshot.getInstrument();
        final Market market = priceSnapshot.getMarket();
        final MutableQuoteBook mutableQuoteBook = priceCache.get(instrument).get(market);

        synchronized (mutableQuoteBook) {
            mutableQuoteBook.getBids().clear();
            mutableQuoteBook.getOffers().clear();

            for (Price price : priceSnapshot.getPrices()) {
                if (price.getSide().isBid()) {
                    mutableQuoteBook.bids.add(price);
                } else {
                    mutableQuoteBook.offers.add(price);
                }
            }
        }
    }

    @Override
    public List<Price> getSortedPrices(Instrument instrument, Side side) {
        final Map<Market, MutableQuoteBook> instrumentPriceCache = priceCache.get(instrument);
        final List<Price> prices = Lists.newArrayList();

        for (Market market : instrumentPriceCache.keySet()) {
            final MutableQuoteBook mutableQuoteBook = instrumentPriceCache.get(market);
            populateWithPrices(prices, mutableQuoteBook, side);
        }

        Collections.sort(prices, side.isBid() ? BID_SORTER : OFFERS_SORTER);

        return prices;
    }

    @Override
    public List<Price> getSortedPrices(Instrument instrument, Market market, Side side) {
        final MutableQuoteBook marketInstrumentPriceCache = priceCache.get(instrument).get(market);
        final List<Price> prices = Lists.newArrayList();

        populateWithPrices(prices, marketInstrumentPriceCache, side);

        Collections.sort(prices, side.isBid() ? BID_SORTER : OFFERS_SORTER);

        return prices;
    }

    private void populateWithPrices(List<Price> prices, MutableQuoteBook marketInstrumentPriceCache, Side side) {
        synchronized (marketInstrumentPriceCache) {
            if (side.isBid()) {
                prices.addAll(marketInstrumentPriceCache.bids);
            } else {
                prices.addAll(marketInstrumentPriceCache.offers);
            }
        }
    }

    private class MutableQuoteBook {
        private final List<Price> bids;
        private final List<Price> offers;

        public MutableQuoteBook(List<Price> bids, List<Price> offers) {
            this.bids = bids;
            this.offers = offers;
        }

        public List<Price> getBids() {
            return bids;
        }

        public List<Price> getOffers() {
            return offers;
        }
    }

    private class BidPriceComparator implements Comparator<Price> {

        @Override
        public int compare(Price o1, Price o2) {
            return MathUtil.comparePrice(o1.getPrice(), o2.getPrice());
        }
    }

    private class OfferPriceComparator implements Comparator<Price> {

        @Override
        public int compare(Price o1, Price o2) {
            return MathUtil.comparePrice(o2.getPrice(), o1.getPrice());
        }
    }
}
