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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for performing LZW decoding.
 */
public class LZWDecoder {

    byte stringTable[][];
    byte[] data = null;
    OutputStream uncompData;
    int tableIndex, bitsToGet = 9;
    int bytePointer, bitPointer;
    int nextData = 0;
    int nextBits = 0;

    int[] andTable = {
            511,
            1023,
            2047,
            4095
    };

    /**
     * Creates an LZWDecoder instance.
     */
    public LZWDecoder() {
        // Empty body
    }

    /**
     * Method to decode LZW compressed data.
     *
     * @param data       The compressed data.
     * @param uncompData Array to return the uncompressed data in.
     */
    public void decode(byte[] data, OutputStream uncompData) {

        if (data[0] == (byte) 0x00 && data[1] == (byte) 0x01) {
            throw new PdfException(KernelExceptionMessageConstant.LZW_FLAVOUR_NOT_SUPPORTED);
        }

        initializeStringTable();

        this.data = data;
        this.uncompData = uncompData;

        // Initialize pointers
        bytePointer = 0;
        bitPointer = 0;

        nextData = 0;
        nextBits = 0;

        int code, oldCode = 0;
        byte[] string;

        while ((code = getNextCode()) != 257) {

            if (code == 256) {

                initializeStringTable();
                code = getNextCode();

                if (code == 257) {
                    break;
                }

                writeString(stringTable[code]);
                oldCode = code;

            } else {

                if (code < tableIndex) {

                    string = stringTable[code];

                    writeString(string);
                    addStringToTable(stringTable[oldCode], string[0]);
                    oldCode = code;

                } else {

                    string = stringTable[oldCode];
                    string = composeString(string, string[0]);
                    writeString(string);
                    addStringToTable(string);
                    oldCode = code;
                }
            }
        }
    }


    /**
     * Initialize the string table.
     */
    public void initializeStringTable() {

        stringTable = new byte[8192][];

        for (int i = 0; i < 256; i++) {
            stringTable[i] = new byte[1];
            stringTable[i][0] = (byte) i;
        }

        tableIndex = 258;
        bitsToGet = 9;
    }

    /**
     * Write out the string just uncompressed.
     *
     * @param string content to write to the uncompressed data
     */
    public void writeString(byte[] string) {
        try {
            uncompData.write(string);
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.LZW_DECODER_EXCEPTION, e);
        }
    }

    /**
     * Add a new string to the string table.
     *
     * @param oldString stored string
     * @param newString string to be appended to the stored string
     */
    public void addStringToTable(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;

        // Add this new String to the table
        stringTable[tableIndex++] = string;

        if (tableIndex == 511) {
            bitsToGet = 10;
        } else if (tableIndex == 1023) {
            bitsToGet = 11;
        } else if (tableIndex == 2047) {
            bitsToGet = 12;
        }
    }

    /**
     * Add a new string to the string table.
     *
     * @param string byte[] to store in the string table
     */
    public void addStringToTable(byte[] string) {

        // Add this new String to the table
        stringTable[tableIndex++] = string;

        if (tableIndex == 511) {
            bitsToGet = 10;
        } else if (tableIndex == 1023) {
            bitsToGet = 11;
        } else if (tableIndex == 2047) {
            bitsToGet = 12;
        }
    }

    /**
     * Append <code>newString</code> to the end of <code>oldString</code>.
     *
     * @param oldString string be appended to
     * @param newString string that is to be appended to oldString
     * @return combined string
     */
    public byte[] composeString(byte[] oldString, byte newString) {
        int length = oldString.length;
        byte[] string = new byte[length + 1];
        System.arraycopy(oldString, 0, string, 0, length);
        string[length] = newString;

        return string;
    }

    // Returns the next 9, 10, 11 or 12 bits

    /**
     * Attempt to get the next code. Exceptions are caught to make
     * this robust to cases wherein the EndOfInformation code has been
     * omitted from a strip. Examples of such cases have been observed
     * in practice.
     *
     * @return next code
     */
    public int getNextCode() {
        //
        try {
            nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
            nextBits += 8;

            if (nextBits < bitsToGet) {
                nextData = (nextData << 8) | (data[bytePointer++] & 0xff);
                nextBits += 8;
            }

            int code =
                    (nextData >> (nextBits - bitsToGet)) & andTable[bitsToGet - 9];
            nextBits -= bitsToGet;

            return code;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Strip not terminated as expected: return EndOfInformation code.
            return 257;
        }
    }
}
