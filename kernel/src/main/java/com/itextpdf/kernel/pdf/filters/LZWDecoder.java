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
package com.itextpdf.kernel.pdf.filters;

import com.itextpdf.kernel.PdfException;
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
     * @param data The compressed data.
     * @param uncompData Array to return the uncompressed data in.
     */
    public void decode(byte[] data, OutputStream uncompData) {

        if(data[0] == (byte)0x00 && data[1] == (byte)0x01) {
            throw new PdfException(PdfException.LzwFlavourNotSupported);
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

        for (int i=0; i<256; i++) {
            stringTable[i] = new byte[1];
            stringTable[i][0] = (byte)i;
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
            throw new PdfException(PdfException.LzwDecoderException, e);
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
                    (nextData >> (nextBits - bitsToGet)) & andTable[bitsToGet-9];
            nextBits -= bitsToGet;

            return code;
        } catch(ArrayIndexOutOfBoundsException e) {
            // Strip not terminated as expected: return EndOfInformation code.
            return 257;
        }
    }
}
