package com.awasthi.amitabh.price;

import com.awasthi.amitabh.domain.Side;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public interface Price {

    Side getSide();

    double getPrice();

    double getAmount();
}
