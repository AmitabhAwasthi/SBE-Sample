package com.awasthi.amitabh.domain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Description:- We can assume that there are more markets here.
 * We don't need to include all for this example
 */
public enum Market {
    EBSAI,
    RTM,
    CNX,
    HSF,
    UBS,
    GS,
    CMZ,
    HSBC,
    BARX,
    DBK;

    /**
     * Do NOT call Enum#values() as it creates a new array-copy each time
     */
    public static final Market[] VALUES = Market.values();

}