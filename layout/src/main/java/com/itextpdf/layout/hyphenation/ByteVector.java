/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itextpdf.layout.hyphenation;

import java.io.Serializable;

/**
 * <p>This class implements a simple byte vector with access to the
 * underlying array.</p>
 *
 * <p>This work was authored by Carlos Villegas (cav@uniscope.co.jp).</p>
 */
public class ByteVector implements Serializable {

    private static final long serialVersionUID = 1554572867863466772L;

    /**
     * Capacity increment size
     */
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;

    /**
     * The encapsulated array
     */
    private byte[] array;

    /**
     * Points to next free item
     */
    private int n;

    /**
     * Construct byte vector instance with default block size.
     */
    public ByteVector() {
        this(DEFAULT_BLOCK_SIZE);
    }

    /**
     * Construct byte vector instance.
     * @param capacity initial block size
     */
    public ByteVector(int capacity) {
        if (capacity > 0) {
            blockSize = capacity;
        } else {
            blockSize = DEFAULT_BLOCK_SIZE;
        }
        array = new byte[blockSize];
        n = 0;
    }

    /**
     * Construct byte vector instance.
     * @param a byte array to use
     * TODO should n should be initialized to a.length to be consistent with
     * CharVector behavior? [GA]
     */
    public ByteVector(byte[] a) {
        blockSize = DEFAULT_BLOCK_SIZE;
        array = a;
        n = 0;
    }

    /**
     * Construct byte vector instance.
     * @param a byte array to use
     * @param capacity initial block size
     * TODO should n should be initialized to a.length to be consistent with
     * CharVector behavior? [GA]
     */
    public ByteVector(byte[] a, int capacity) {
        if (capacity > 0) {
            blockSize = capacity;
        } else {
            blockSize = DEFAULT_BLOCK_SIZE;
        }
        array = a;
        n = 0;
    }

    /**
     * Obtain byte vector array.
     * @return byte array
     */
    public byte[] getArray() {
        return array;
    }

    /**
     * Obtain number of items in array.
     * @return number of items
     */
    public int length() {
        return n;
    }

    /**
     * Obtain capacity of array.
     * @return current capacity of array
     */
    public int capacity() {
        return array.length;
    }

    /**
     * Pet byte at index.
     * @param index the index
     * @param val a byte
     */
    public void put(int index, byte val) {
        array[index] = val;
    }

    /**
     * Get byte at index.
     * @param index the index
     * @return a byte
     */
    public byte get(int index) {
        return array[index];
    }

    /**
     * This is to implement memory allocation in the array. Like malloc().
     * @param size to allocate
     * @return previous length
     */
    public int alloc(int size) {
        int index = n;
        int len = array.length;
        if (n + size >= len) {
            byte[] aux = new byte[len + blockSize];
            System.arraycopy(array, 0, aux, 0, len);
            array = aux;
        }
        n += size;
        return index;
    }

    /**
     * Trim byte vector to current length.
     */
    public void trimToSize() {
        if (n < array.length) {
            byte[] aux = new byte[n];
            System.arraycopy(array, 0, aux, 0, n);
            array = aux;
        }
    }

}
