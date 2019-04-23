/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
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
     *
     * @param n
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
