package com.itextpdf.kernel.counter.data;

public final class WaitTime {

    private final long time;
    private final long initial;
    private final long maximum;

    public WaitTime(long initial, long maximum) {
        this(initial, maximum, initial);
    }

    public WaitTime(long initial, long maximum, long time) {
        this.initial = initial;
        this.maximum = maximum;
        this.time = time;
    }

    public long getInitial() {
        return initial;
    }

    public long getMaximum() {
        return maximum;
    }

    public long getTime() {
        return time;
    }
}