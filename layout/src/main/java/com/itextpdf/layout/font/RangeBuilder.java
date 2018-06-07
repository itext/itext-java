package com.itextpdf.layout.font;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder of {@link Range}.
 */
public class RangeBuilder {

    private static final Range fullRangeSingleton = new Range.FullRange();

    private List<Range.SubRange> ranges = new ArrayList<>();

    /**
     * Default Range instance.
     *
     * @return Range that contains any integer.
     */
    static Range getFullRange() {
        return fullRangeSingleton;
    }


    /**
     * Default constructor with empty range.
     */
    public RangeBuilder() {
    }

    /**
     * Constructor with a single range.
     *
     * @param low  low boundary of the range.
     * @param high high boundary of the range.
     */
    public RangeBuilder(int low, int high) {
        this.addRange(low, high);
    }

    /**
     * Constructor with a single number.
     *
     * @param n a single number.
     */
    public RangeBuilder(int n) {
        this(n, n);
    }

    /**
     * Constructor with a single range.
     *
     * @param low  low boundary of the range.
     * @param high high boundary of the range.
     */
    public RangeBuilder(char low, char high) {
        this((int) low, (int) high);
    }

    /**
     * Constructor with a single char.
     *
     * @param ch a single char.
     */
    public RangeBuilder(char ch) {
        this((int) ch);
    }

    /**
     * Add one more range.
     *
     * @param low  low boundary of the range.
     * @param high high boundary of the range.
     */
    public RangeBuilder addRange(int low, int high) {
        if (high < low) {
            throw new IllegalArgumentException("'from' shall be less than 'to'");
        }
        ranges.add(new Range.SubRange(low, high));
        return this;
    }

    /**
     * Add one more range.
     *
     * @param low  low boundary of the range.
     * @param high high boundary of the range.
     */
    public RangeBuilder addRange(char low, char high) {
        return addRange((int) low, (int) high);
    }

    /**
     * Add range with a single number.
     *
     * @param n a single number.
     */
    public RangeBuilder addRange(int n) {
        return addRange(n, n);
    }

    /**
     * Add range with a single char.
     *
     * @param ch a single char.
     */
    public RangeBuilder addRange(char ch) {
        return addRange((int) ch);
    }


    public Range create() {
        return new Range(ranges);
    }
}
