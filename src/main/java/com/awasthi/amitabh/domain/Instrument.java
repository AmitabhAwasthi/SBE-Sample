package com.awasthi.amitabh.domain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Description:- We can assume that there are more instruments here.
 * We don't need to include all for this minimalist example
 */
public enum Instrument {
    EURUSD,
    USDJPY,
    AUDUSD,
    GBPUSD,
    EURCHF,
    USDCAD,
    NZDUSD,
    EURSEK,
    EURDKK,
    EURNOK;

    /**
     * Do NOT call Enum#values() as it creates a new array-copy each time
     */
    public static final Instrument[] VALUES = Instrument.values();
}
