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
 * <p>This class implements a simple char vector with access to the
 * underlying array.</p>
 *
 * <p>This work was authored by Carlos Villegas (cav@uniscope.co.jp).</p>
 */
public class CharVector implements Serializable {

    private static final long serialVersionUID = 4263472982169004048L;

    /**
     * Capacity increment size
     */
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;

    /**
     * The encapsulated array
     */
    private char[] array;

    /**
     * Points to next free item
     */
    private int n;

    /**
     * Construct char vector instance with default block size.
     */
    public CharVector() {
        this(DEFAULT_BLOCK_SIZE);
    }

    /**
     * Construct char vector instance.
     * @param capacity initial block size
     */
    public CharVector(int capacity) {
        if (capacity > 0) {
            blockSize = capacity;
        } else {
            blockSize = DEFAULT_BLOCK_SIZE;
        }
        array = new char[blockSize];
        n = 0;
    }

    /**
     * Construct char vector instance.
     * @param a char array to use
     */
    public CharVector(char[] a) {
        blockSize = DEFAULT_BLOCK_SIZE;
        array = a;
        n = a.length;
    }

    /**
     * Construct char vector instance.
     * @param a char array to use
     * @param capacity initial block size
     */
    public CharVector(char[] a, int capacity) {
        if (capacity > 0) {
            blockSize = capacity;
        } else {
            blockSize = DEFAULT_BLOCK_SIZE;
        }
        array = a;
        n = a.length;
    }

    /**
     * Copy constructor
     * @param cv the CharVector that should be cloned
     */
    public CharVector(CharVector cv) {
        this.array = (char[])cv.array.clone();
        this.blockSize = cv.blockSize;
        this.n = cv.n;
    }

    /**
     * Reset length of vector, but don't clear contents.
     */
    public void clear() {
        n = 0;
    }

    /**
     * Obtain char vector array.
     * @return char array
     */
    public char[] getArray() {
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
     * Pet char at index.
     * @param index the index
     * @param val a char
     */
    public void put(int index, char val) {
        array[index] = val;
    }

    /**
     * Get char at index.
     * @param index the index
     * @return a char
     */
    public char get(int index) {
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
            char[] aux = new char[len + blockSize];
            System.arraycopy(array, 0, aux, 0, len);
            array = aux;
        }
        n += size;
        return index;
    }

    /**
     * Trim char vector to current length.
     */
    public void trimToSize() {
        if (n < array.length) {
            char[] aux = new char[n];
            System.arraycopy(array, 0, aux, 0, n);
            array = aux;
        }
    }
}
