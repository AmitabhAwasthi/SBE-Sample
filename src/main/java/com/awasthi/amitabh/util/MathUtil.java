package com.awasthi.amitabh.util;

import com.google.common.math.DoubleMath;

/**
 * Description:- Static util class to help with comparisons
 */
public class MathUtil {

    public static final double PRICE_TOLERANCE = 0.000000001d;
    public static final double QTY_TOLERANCE = 0.0001d;
    public static final double ZERO = 0.0d;

    private MathUtil() {

    }

    public static boolean isPriceUndefined(double price1) {
        return Double.isNaN(price1) || isPriceEqual(price1, ZERO);
    }

    public static boolean isQtyUndefined(double qty1) {
        return Double.isNaN(qty1) || isQtyEqual(qty1, ZERO);
    }

    public static boolean isPriceEqual(double price1, double price2) {
        return DoubleMath.fuzzyEquals(price1, price2, PRICE_TOLERANCE);
    }

    public static int comparePrice(double price1, double price2) {
        return DoubleMath.fuzzyCompare(price1, price2, PRICE_TOLERANCE);
    }

    public static boolean isQtyEqual(double qty1, double qty2) {
        return DoubleMath.fuzzyEquals(qty1, qty2, QTY_TOLERANCE);
    }

    public static int compareQty(double qty1, double qty2) {
        return DoubleMath.fuzzyCompare(qty1, qty2, QTY_TOLERANCE);
    }
}
