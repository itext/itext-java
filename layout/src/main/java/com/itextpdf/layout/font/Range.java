package com.itextpdf.layout.font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Ordered range for {@link FontInfo#getFontUnicodeRange()}.
 * To create a custom Range instance {@link RangeBuilder} shall be used.
 */
public class Range {

    //ordered sub-ranges
    private SubRange[] ranges;

    private Range() {
    }

    Range(List<SubRange> ranges) {
        if (ranges.size() == 0) {
            throw new IllegalArgumentException("Ranges shall not be empty");
        }
        this.ranges = normalizeSubRanges(ranges);
    }

    /**
     * Binary search over ordered segments.
     */
    public boolean contains(int n) {
        int low = 0;
        int high = ranges.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (ranges[mid].compareTo(n) < 0)
                low = mid + 1;
            else if (ranges[mid].compareTo(n) > 0)
                high = mid - 1;
            else
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Arrays.equals(ranges, range.ranges);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ranges);
    }

    @Override
    public String toString() {
        return Arrays.toString(ranges);
    }

    /**
     * Order ranges. Replace with a union of ranges in case of overlap.
     *
     * @param ranges Unsorted list of sub-ranges.
     * @return ordered and normalized sub-ranges.
     */
    private static SubRange[] normalizeSubRanges(List<SubRange> ranges) {
        //Ranges will not be modified, let's create a union of sub-ranges.
        //1. Sort ranges by start point.
        Collections.sort(ranges);
        List<SubRange> union = new ArrayList<>(ranges.size());

        assert ranges.size() > 0;
        SubRange curr = ranges.get(0);
        union.add(curr);
        for (int i = 1; i < ranges.size(); i++) {
            SubRange next = ranges.get(i);
            //assume that curr.low <= next.low
            if (next.low <= curr.high) {
                //union, update curr
                if (next.high > curr.high)
                    curr.high = next.high;
            } else {
                //add a new sub-range.
                curr = next;
                union.add(curr);
            }
        }

        return union.toArray(new SubRange[0]);
    }

    static class SubRange implements Comparable<SubRange> {
        int low;
        int high;

        SubRange(int low, int high) {
            this.low = low;
            this.high = high;
        }

        @Override
        public int compareTo(SubRange o) {
            return low - o.low;
        }

        public int compareTo(int n) {
            if (n < low) return 1;
            if (n > high) return -1;
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubRange subRange = (SubRange) o;
            return low == subRange.low &&
                    high == subRange.high;
        }

        @Override
        public int hashCode() {
            return 31 * low + high;
        }

        @Override
        public String toString() {
            return "(" + low + "; " + high +')';
        }
    }

    static class FullRange extends Range {
        FullRange() {
            super();
        }

        @Override
        public boolean contains(int uni) {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "[FullRange]";
        }
    }
}
