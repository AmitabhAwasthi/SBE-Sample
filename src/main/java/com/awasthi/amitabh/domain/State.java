package com.awasthi.amitabh.domain;

public enum State {
    FIRM(false),
    INDICATIVE(true);

    private final boolean isIndicative;

    State(final boolean isIndicative) {
        this.isIndicative = isIndicative;
    }

    public boolean isIndicative() {
        return this.isIndicative;
    }

    /**
     * Do NOT call Enum#values() as it creates a new array-copy each time
     */
    public static final State[] VALUES = State.values();
}
