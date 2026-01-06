/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import java.util.Arrays;

/**
 * Class allows to process pixels of the bitmap image stored as byte array according to PDF
 * specification.
 */
public class BitmapImagePixels {
    private static final int BITS_IN_BYTE = 8;
    private static final int DEFAULT_BITS_PER_COMPONENT = 8;
    private static final int BYTE_WITH_LEADING_BIT = 0b10000000;

    // (x / 8) == (x >>> 3)
    private static final int BITS_IN_BYTE_LOG = 3;
    // (x % 8) == (x & 0b00000111)
    private static final int BIT_MASK = 0b00000111;

    private final int width;
    // Pdf spec: each row of sample data shall begin on a byte boundary. If the number of data bits
    // per row is not a multiple of 8, the end of the row is padded with extra bits to fill out the
    // last byte. A conforming reader shall ignore these padding bits.
    private final int bitsInRow;
    private final int height;
    private final int bitsPerComponent;
    private final int maxComponentValue;
    private final int numberOfComponents;
    private final byte[] data;

    /**
     * Creates a representation of empty image.
     *
     * @param width is a width of the image
     * @param height is a height of the image
     * @param bitsPerComponent is an amount of bits representing each color component of a pixel
     * @param numberOfComponents is a number of components representing a pixel
     */
    public BitmapImagePixels(int width, int height, int bitsPerComponent, int numberOfComponents) {
        this(width, height, bitsPerComponent, numberOfComponents, null);
    }

    /**
     * Creates a representation of an image presented as {@link PdfImageXObject}.
     *
     * @param image is an image as {@link PdfImageXObject}
     */
    public BitmapImagePixels(PdfImageXObject image) {
        this(
                (int) Math.round(image.getWidth()),
                (int) Math.round(image.getHeight()),
                obtainBitsPerComponent(image),
                obtainNumberOfComponents(image),
                image.getPdfObject().getBytes()
        );
    }

    /**
     * Creates a representation of an image presented as bytes array.
     *
     * @param width is a width of the image
     * @param height is a height of the image
     * @param bitsPerComponent is an amount of bits representing each color component of a pixel
     * @param numberOfComponents is a number of components representing a pixel
     * @param data is an image data
     */
    public BitmapImagePixels(int width, int height, int bitsPerComponent, int numberOfComponents, byte[] data) {
        this.width = width;
        this.height = height;
        this.bitsPerComponent = bitsPerComponent;
        this.maxComponentValue = (1 << this.bitsPerComponent) - 1;
        this.numberOfComponents = numberOfComponents;
        int rowLength = width * bitsPerComponent * numberOfComponents;
        if (rowLength % BITS_IN_BYTE != 0) {
            rowLength += BITS_IN_BYTE - (rowLength & BIT_MASK);
        }
        bitsInRow = rowLength;
        if (data == null) {
            this.data = new byte[(bitsInRow * height) >>> BITS_IN_BYTE_LOG];
        } else {
            final int expectedLength = bitsInRow * height;
            final int actualLength = data.length * BITS_IN_BYTE;
            if (expectedLength != actualLength) {
                throw new IllegalArgumentException(MessageFormatUtil.format(
                        KernelExceptionMessageConstant.INVALID_DATA_LENGTH, expectedLength, actualLength));
            }

            this.data = Arrays.copyOf(data, data.length);
        }
    }

    /**
     * Gets pixel of the image.
     *
     * @param x is an x-coordinate of a pixel to update
     * @param y is a y-coordinate of a pixel to update
     * @return an array representing pixel color according to used color space
     */
    public double[] getPixel(int x, int y) {
        final long[] longArray = getPixelAsLongs(x, y);
        double[] pixelArray = new double[longArray.length];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = (double) longArray[i] / maxComponentValue;
        }
        return pixelArray;
    }

    /**
     * Gets pixel of the image presented as long values.
     *
     * @param x is an x-coordinate of a pixel to update
     * @param y is a y-coordinate of a pixel to update
     * @return an array representing pixel color according to used color space
     */
    public long[] getPixelAsLongs(int x, int y) {
        checkCoordinates(x, y);
        final long[] pixelArray = new long[numberOfComponents];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = readNumber(
                    // skip y rows from 0 to y-1
                    y * bitsInRow +
                            // skip x pixels from 0 to (x-1)
                            x * bitsPerComponent * numberOfComponents +
                            // skip i components of the current pixel from 0 to (i-1)
                            i * bitsPerComponent);
        }
        return pixelArray;
    }

    /**
     * Updates a pixel of the image.
     *
     * @param x is an x-coordinate of a pixel to update
     * @param y is a y-coordinate of a pixel to update
     * @param value is a pixel color. Pixel should be presented as double array according to used
     *              color space. Each value should be in range [0., 1.] (otherwise negative value
     *              will be replaced with 0. and large numbers are replaced with 1.)
     */
    public void setPixel(int x, int y, double[] value) {
        final long[] longArray = new long[value.length];
        for (int i = 0; i < value.length; i++) {
            longArray[i] =(long) Math.round(value[i] * maxComponentValue);
        }
        setPixel(x, y, longArray);
    }

    /**
     * Updates a pixel of the image.
     *
     * @param x is an x-coordinate of a pixel to update
     * @param y is a y-coordinate of a pixel to update
     * @param value is a pixel color. Pixel should be presented as long array according to used
     *              color space. Each value should be in range
     *              [0, <code>2 ^ bitsPerComponent</code> - 1] (otherwise negative value
     *              will be replaced with 0. and large numbers are replaced with maximum allowed
     *              value.)
     */
    public void setPixel(int x, int y, long[] value) {
        checkCoordinates(x, y);
        checkPixel(value);
        for (int i = 0; i < value.length; i++) {
            writeNumber(value[i],
                    // skip y rows from 0 to y-1
                    y * bitsInRow +
                            // skip x pixels from 0 to (x-1)
                            x * bitsPerComponent * numberOfComponents +
                            // skip i components of the current pixel from 0 to (i-1)
                            i * bitsPerComponent);
        }
    }

    /**
     * Getter for a width of the image.
     *
     * @return width of the image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for a height of the image.
     *
     * @return height of the image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for bits per component parameter of the image.
     *
     * @return bits per component parameter of the image
     */
    public int getBitsPerComponent() {
        return bitsPerComponent;
    }

    /**
     * Getter for number of components parameter of the image.
     *
     * @return number of components of the image
     */
    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    /**
     * Getter for byte representation of the image.
     *
     * @return image data as byte array
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets the maximum value for the component.
     *
     * @return maximum value of the component
     */
    public int getMaxComponentValue() {
        return maxComponentValue;
    }

    private long readNumber(int index) {
        long result = 0;
        for (int i = 0; i < bitsPerComponent; i++) {
            result = (result << 1) + booleanToInt(getBit(index + i));
        }
        return result;
    }

    private void writeNumber(long number, int index) {
        for (int bitNumber = 0; bitNumber < bitsPerComponent; bitNumber ++) {
            final int actualBitMask = 1 << (bitsPerComponent - bitNumber - 1);
            setBit(index + bitNumber, (number & actualBitMask) != 0);
        }
    }

    private boolean getBit(int index) {
        return (data[index >>> BITS_IN_BYTE_LOG] & 0xff
                & (BYTE_WITH_LEADING_BIT >>> (index & BIT_MASK))) != 0;
    }

    private void setBit(int index, boolean value) {
        if (value) {
            data[index >>> BITS_IN_BYTE_LOG] |= (byte) (BYTE_WITH_LEADING_BIT >>> (index & BIT_MASK));
        } else {
            data[index >>> BITS_IN_BYTE_LOG] &= (byte) ~(BYTE_WITH_LEADING_BIT >>> (index & BIT_MASK));
        }
    }

    private void checkCoordinates(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y > height) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(
                            KernelExceptionMessageConstant.PIXEL_OUT_OF_BORDERS, x, y, width, height));
        }
    }
    private void checkPixel(long[] pixel) {
        if (pixel.length != numberOfComponents) {
            throw new IllegalArgumentException(
                    MessageFormatUtil.format(
                            KernelExceptionMessageConstant.LENGTH_OF_ARRAY_SHOULD_MATCH_NUMBER_OF_COMPONENTS,
                            pixel.length, numberOfComponents));
        }

        for (int i = 0; i < pixel.length; i++) {
            if (pixel[i] < 0) {
                pixel[i] = 0;
            }
            if (pixel[i] > maxComponentValue) {
                pixel[i] = maxComponentValue;
            }
        }
    }

    private static int obtainBitsPerComponent(PdfImageXObject objectToProcess) {
        final PdfStream imageStream = objectToProcess.getPdfObject();
        final PdfNumber bpc = imageStream.getAsNumber(PdfName.BitsPerComponent);
        if (bpc == null) {
            return DEFAULT_BITS_PER_COMPONENT;
        } else {
            return bpc.intValue();
        }
    }

    private static int obtainNumberOfComponents(PdfImageXObject objectToProcess) {
        return PdfColorSpace.makeColorSpace(
                objectToProcess.getPdfObject().get(PdfName.ColorSpace)).getNumberOfComponents();
    }

    private static int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
