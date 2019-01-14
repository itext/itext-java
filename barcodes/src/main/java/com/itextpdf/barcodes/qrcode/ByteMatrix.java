/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.barcodes.qrcode;


/**
 * A class which wraps a 2D array of bytes. The default usage is signed. If you want to use it as a
 * unsigned container, it's up to you to do byteValue &amp; 0xff at each location.
 *
 * JAVAPORT: The original code was a 2D array of ints, but since it only ever gets assigned
 * -1, 0, and 1, I'm going to use less memory and go with bytes.
 *
 * @author dswitkin@google.com (Daniel Switkin)
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
        StringBuffer result = new StringBuffer(2 * width * height + 2);
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
