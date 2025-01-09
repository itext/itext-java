/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.barcodes.qrcode;


/**
 * A simple, fast array of bits, represented compactly by an array of ints internally.
 */
final class BitArray {

    private int[] bits;
    private final int size;

    public BitArray(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("size must be at least 1");
        }
        this.size = size;
        this.bits = makeArray(size);
    }

    public int getSize() {
        return size;
    }

    /**
     * @param i bit to get.
     * @return true iff bit i is set
     */
    public boolean get(int i) {
        return (bits[i >> 5] & (1 << (i & 0x1F))) != 0;
    }

    /**
     * Sets bit i.
     *
     * @param i bit to set
     */
    public void set(int i) {
        bits[i >> 5] |= 1 << (i & 0x1F);
    }

    /**
     * Flips bit i.
     *
     * @param i bit to set
     */
    public void flip(int i) {
        bits[i >> 5] ^= 1 << (i & 0x1F);
    }

    /**
     * Sets a block of 32 bits, starting at bit i.
     *
     * @param i first bit to set
     * @param newBits the new value of the next 32 bits. Note again that the least-significant bit
     * corresponds to bit i, the next-least-significant to i+1, and so on.
     */
    public void setBulk(int i, int newBits) {
        bits[i >> 5] = newBits;
    }

    /**
     * Clears all bits (sets to false).
     */
    public void clear() {
        int max = bits.length;
        for (int i = 0; i < max; i++) {
            bits[i] = 0;
        }
    }

    /**
     * Efficient method to check if a range of bits is set, or not set.
     *
     * @param start start of range, inclusive.
     * @param end end of range, exclusive
     * @param value if true, checks that bits in range are set, otherwise checks that they are not set
     * @return true iff all bits are set or not set in range, according to value argument
     * @throws IllegalArgumentException if end is less than or equal to start
     */
    public boolean isRange(int start, int end, boolean value) {
        if (end < start) {
            throw new IllegalArgumentException();
        }
        if (end == start) {

            // empty range matches
            return true;
        }

        // will be easier to treat this as the last actually set bit -- inclusive
        end--;
        int firstInt = start >> 5;
        int lastInt = end >> 5;
        for (int i = firstInt; i <= lastInt; i++) {
            int firstBit = i > firstInt ? 0 : start & 0x1F;
            int lastBit = i < lastInt ? 31 : end & 0x1F;
            int mask;
            if (firstBit == 0 && lastBit == 31) {
                mask = -1;
            } else {
                mask = 0;
                for (int j = firstBit; j <= lastBit; j++) {
                    mask |= 1 << j;
                }
            }

            // Return false if we're looking for 1s and the masked bits[i] isn't all 1s (that is,
            // equals the mask, or we're looking for 0s and the masked portion is not all 0s
            if ((bits[i] & mask) != (value ? mask : 0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return underlying array of ints. The first element holds the first 32 bits, and the least
     *         significant bit is bit 0.
     */
    public int[] getBitArray() {
        return bits;
    }

    /**
     * Reverses all bits in the array.
     */
    public void reverse() {
        int[] newBits = new int[bits.length];
        int size = this.size;
        for (int i = 0; i < size; i++) {
            if (get(size - i - 1)) {
                newBits[i >> 5] |= 1 << (i & 0x1F);
            }
        }
        bits = newBits;
    }

    private static int[] makeArray(int size) {
        int arraySize = size >> 5;
        if ((size & 0x1F) != 0) {
            arraySize++;
        }
        return new int[arraySize];
    }

    public String toString() {
        StringBuffer result = new StringBuffer(size);
        for (int i = 0; i < size; i++) {
            if ((i & 0x07) == 0) {
                result.append(' ');
            }
            result.append(get(i) ? 'X' : '.');
        }
        return result.toString();
    }

}
