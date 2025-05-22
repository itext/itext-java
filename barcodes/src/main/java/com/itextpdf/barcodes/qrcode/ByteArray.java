/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.itextpdf.barcodes.qrcode;

/**
* This class implements an array of unsigned bytes.
*/
final class ByteArray {

    private static final int INITIAL_SIZE = 32;

    private byte[] bytes;
    private int size;

    /**
     * Creates a new ByteArray instance with size 0.
     */
    public ByteArray() {
        bytes = null;
        size = 0;
    }

    /**
     * Creates a new ByteArray instance of the specified size.
     *
     * @param size size of the array
     */
    public ByteArray(int size) {
        bytes = new byte[size];
        this.size = size;
    }

    /**
     * Creates a new ByteArray instance based on an existing byte[].
     *
     * @param byteArray the byte[]
     */
    public ByteArray(byte[] byteArray) {
        bytes = byteArray;
        size = bytes.length;
    }

    /**
     * Access an unsigned byte at location index.
     * @param index The index in the array to access.
     * @return The unsigned value of the byte as an int.
     */
    public int at(int index) {
        return bytes[index] & 0xff;
    }

    /**
     * Set the value at "index" to "value"
     * @param index position in the byte-array
     * @param value new value
     */
    public void set(int index, int value) {
        bytes[index] = (byte) value;
    }

    /**
     * @return size of the array
     */
    public int size() {
        return size;
    }

    /**
     * @return true if size is equal to 0, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Append a byte to the end of the array. If the array is too small, it's capacity is doubled.
     * @param value byte to append.
     */
    public void appendByte(int value) {
        if (size == 0 || size >= bytes.length) {
            int newSize = Math.max(INITIAL_SIZE, size << 1);
            reserve(newSize);
        }
        bytes[size] = (byte) value;
        size++;
    }

    /**
     * Increase the capacity of the array to "capacity" if the current capacity is smaller
     * @param capacity the new capacity
     */
    public void reserve(int capacity) {
        if (bytes == null || bytes.length < capacity) {
            byte[] newArray = new byte[capacity];
            if (bytes != null) {
                System.arraycopy(bytes, 0, newArray, 0, bytes.length);
            }
            bytes = newArray;
        }
    }

    /**
     * Copy count bytes from array source starting at offset.
     * @param source source of the copied bytes
     * @param offset offset to start at
     * @param count number of bytes to copy
     */
    public void set(byte[] source, int offset, int count) {
        bytes = new byte[count];
        size = count;
        for (int x = 0; x < count; x++) {
            bytes[x] = source[offset + x];
        }
    }

}
