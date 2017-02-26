package com.awasthi.amitabh.domain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public enum Side {
    BID(true),
    OFFER(false);

    private boolean isBid;

    Side(final boolean isBid) {
        this.isBid = isBid;
    }

    public boolean isBid() {
        return this.isBid;
    }
    /**
     * Do NOT call Enum#values() as it creates a new array-copy each time
     */
    public static final Side[] VALUES = Side.values();
}
