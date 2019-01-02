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
package com.itextpdf.io.codec;


import java.io.PrintStream;

/**
 * General purpose LZW String Table.
 * Extracted from GIFEncoder by Adam Doppelt
 * Comments added by Robin Luiten
 * <code>expandCode</code> added by Robin Luiten
 * The strLen_ table to give quick access to the lenght of an expanded
 * code for use by the <code>expandCode</code> method added by Robin.
 */
public class LZWStringTable {
    /**
     * codesize + Reserved Codes
     */
    private static final int RES_CODES = 2;

    //0xFFFF
    private static final short HASH_FREE = -1;
    //0xFFFF
    private static final short NEXT_FIRST = -1;

    private static final int MAXBITS = 12;
    private static final int MAXSTR = (1 << MAXBITS);

    private static final short HASHSIZE = 9973;
    private static final short HASHSTEP = 2039;

    byte[] strChr_;        // after predecessor character
    short[] strNxt_;        // predecessor string
    short[] strHsh_;        // hash table to find  predecessor + char pairs
    short numStrings_;        // next code if adding new prestring + char

    /**
     * each entry corresponds to a code and contains the length of data
     * that the code expands to when decoded.
     **/
    int[] strLen_;

    /**
     * Constructor allocate memory for string store data
     **/
    public LZWStringTable() {
        strChr_ = new byte[MAXSTR];
        strNxt_ = new short[MAXSTR];
        strLen_ = new int[MAXSTR];
        strHsh_ = new short[HASHSIZE];
    }

    /**
     * @param index value of -1 indicates no predecessor [used in initialization]
     * @param b     the byte [character] to add to the string store which follows
     *              the predecessor string specified the index.
     * @return 0xFFFF if no space in table left for addition of predecessor
     * index and byte b. Else return the code allocated for combination index + b.
     **/
    public int AddCharString(short index, byte b) {
        int hshidx;

        if (numStrings_ >= MAXSTR)    // if used up all codes
        {
            return 0xFFFF;
        }

        hshidx = Hash(index, b);
        while (strHsh_[hshidx] != HASH_FREE)
            hshidx = (hshidx + HASHSTEP) % HASHSIZE;

        strHsh_[hshidx] = numStrings_;
        strChr_[numStrings_] = b;
        if (index == HASH_FREE) {
            strNxt_[numStrings_] = NEXT_FIRST;
            strLen_[numStrings_] = 1;
        } else {
            strNxt_[numStrings_] = index;
            strLen_[numStrings_] = strLen_[index] + 1;
        }

        return numStrings_++;    // return the code and inc for next code
    }

    /**
     * @param index index to prefix string
     * @param b     the character that follws the index prefix
     * @return b if param index is HASH_FREE. Else return the code
     * for this prefix and byte successor
     **/
    public short FindCharString(short index, byte b) {
        int hshidx, nxtidx;

        if (index == HASH_FREE)
            return (short) (b & 0xFF);    // Rob fixed used to sign extend

        hshidx = Hash(index, b);
        while ((nxtidx = strHsh_[hshidx]) != HASH_FREE)    // search
        {
            if (strNxt_[nxtidx] == index && strChr_[nxtidx] == b)
                return (short) nxtidx;
            hshidx = (hshidx + HASHSTEP) % HASHSIZE;
        }

        //return (short) 0xFFFF;
        return -1;
    }

    /**
     * @param codesize the size of code to be preallocated for the
     *                 string store.
     **/
    public void ClearTable(int codesize) {
        numStrings_ = 0;

        for (int q = 0; q < HASHSIZE; q++)
            strHsh_[q] = HASH_FREE;

        int w = (1 << codesize) + RES_CODES;
        for (int q = 0; q < w; q++) {
            //AddCharString((short) 0xFFFF, (byte) q);    // init with no prefix
            AddCharString((short)-1, (byte) q);    // init with no prefix
        }
    }

    public static int Hash(short index, byte lastbyte) {
        return (((short) (lastbyte << 8) ^ index) & 0xFFFF) % HASHSIZE;
    }

    /**
     * If expanded data doesn't fit into array only what will fit is written
     * to buf and the return value indicates how much of the expanded code has
     * been written to the buf. The next call to expandCode() should be with
     * the same code and have the skip parameter set the negated value of the
     * previous return. Successive negative return values should be negated and
     * added together for next skip parameter value with same code.
     *
     * @param buf      buffer to place expanded data into
     * @param offset   offset to place expanded data
     * @param code     the code to expand to the byte array it represents.
     *                 PRECONDITION This code must already be in the LZSS
     * @param skipHead is the number of bytes at the start of the expanded code to
     *                 be skipped before data is written to buf. It is possible that skipHead is
     *                 equal to codeLen.
     * @return the length of data expanded into buf. If the expanded code is longer
     * than space left in buf then the value returned is a negative number which when
     * negated is equal to the number of bytes that were used of the code being expanded.
     * This negative value also indicates the buffer is full.
     **/
    public int expandCode(byte[] buf, int offset, short code, int skipHead) {
        if (offset == -2) {
            if (skipHead == 1) skipHead = 0;
        }
        //-1 ~ 0xFFFF
        if (code == -1 ||                // just in case
                skipHead == strLen_[code])                // DONE no more unpacked
            return 0;

        int expandLen;                            // how much data we are actually expanding
        int codeLen = strLen_[code] - skipHead;    // length of expanded code left
        int bufSpace = buf.length - offset;        // how much space left
        if (bufSpace > codeLen)
            expandLen = codeLen;                // only got this many to unpack
        else
            expandLen = bufSpace;

        int skipTail = codeLen - expandLen;        // only > 0 if codeLen > bufSpace [left overs]

        int idx = offset + expandLen;            // initialise to exclusive end address of buffer area

        // NOTE: data unpacks in reverse direction and we are placing the
        // unpacked data directly into the array in the correct location.
        while ((idx > offset) && (code != -1)) {
            if (--skipTail < 0)                    // skip required of expanded data
            {
                buf[--idx] = strChr_[code];
            }
            code = strNxt_[code];                // to predecessor code
        }

        if (codeLen > expandLen)
            return -expandLen;                    // indicate what part of codeLen used
        else
            return expandLen;                    // indicate length of dat unpacked
    }

    public void dump(PrintStream output) {
        int i;
        for (i = 258; i < numStrings_; ++i)
            output.println(" strNxt_[" + i + "] = " + strNxt_[i]
                    + " strChr_ " + Integer.toHexString(strChr_[i] & 0xFF)
                    + " strLen_ " + Integer.toHexString(strLen_[i]));
    }
}
