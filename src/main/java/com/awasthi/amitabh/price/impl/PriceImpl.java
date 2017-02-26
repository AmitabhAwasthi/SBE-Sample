package com.awasthi.amitabh.price.impl;

import com.awasthi.amitabh.domain.Side;
import com.awasthi.amitabh.price.Price;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class PriceImpl implements Price {

    private final Side side;
    private final double amount;
    private final double price;

    public PriceImpl(Side side, double amount, double price) {
        this.side = side;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PriceImpl{" +
                "side=" + side +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }
}
