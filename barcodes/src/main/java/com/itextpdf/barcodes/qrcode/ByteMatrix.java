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
 * A class which wraps a 2D array of bytes. The default usage is signed. If you want to use it as a
 * unsigned container, it's up to you to do byteValue &amp; 0xff at each location.
 *
 * JAVAPORT: The original code was a 2D array of ints, but since it only ever gets assigned
 * -1, 0, and 1, I'm going to use less memory and go with bytes.
 */
public final class ByteMatrix {

    private final byte[][] bytes;
    private final int width;
    private final int height;

    /**
     * Create a ByteMatix of given width and height, with the values initialized to 0
     * @param width width of the matrix
     * @param height height of the matrix
     */
    public ByteMatrix(int width, int height) {
        bytes = new byte[height][];
        for(int i = 0; i < height; i++) {
            bytes[i] = new byte[width];
        }
        this.width = width;
        this.height = height;
    }

    /**
     * @return height of the matrix
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return width of the matrix
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the value of the byte at (x,y)
     * @param x the width coordinate
     * @param y the height coordinate
     * @return the byte value at position (x,y)
     */
    public byte get(int x, int y) {
        return bytes[y][x];
    }

    /**
     * @return matrix as byte[][]
     */
    public byte[][] getArray() {
        return bytes;
    }

    /**
     * Set the value of the byte at (x,y)
     * @param x the width coordinate
     * @param y the height coordinate
     * @param value the new byte value
     */
    public void set(int x, int y, byte value) {
        bytes[y][x] = value;
    }

    /**
     * Set the value of the byte at (x,y)
     * @param x the width coordinate
     * @param y the height coordinate
     * @param value the new byte value
     */
    public void set(int x, int y, int value) {
        bytes[y][x] = (byte) value;
    }

    /**
     * Resets the contents of the entire matrix to value
     * @param value new value of every element
     */
    public void clear(byte value) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                bytes[y][x] = value;
            }
        }
    }

    /**
     * @return String representation
     */
    public String toString() {
        StringBuilder result = new StringBuilder(2 * width * height + 2);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                switch (bytes[y][x]) {
                    case 0:
                        result.append(" 0");
                        break;
                    case 1:
                        result.append(" 1");
                        break;
                    default:
                        result.append("  ");
                        break;
                }
            }
            result.append('\n');
        }
        return result.toString();
    }

}
