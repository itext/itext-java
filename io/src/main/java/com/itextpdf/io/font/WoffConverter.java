/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.io.font;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

class WoffConverter {

    private static final long woffSignature = 0x774F4646L;

    public static boolean isWoffFont(byte[] woffBytes) {
        return bytesToUInt(woffBytes, 0) == woffSignature;
    }

    public static byte[] convert(byte[] woffBytes) throws java.io.IOException {
        int srcPos = 0;
        int destPos = 0;

        // signature
        if (bytesToUInt(woffBytes, srcPos) != woffSignature) {
            throw new IllegalArgumentException();
        }
        srcPos += 4;

        byte[] flavor = new byte[4];
        System.arraycopy(woffBytes, srcPos, flavor, 0, 4);
        srcPos += 4;

        // length
        if (bytesToUInt(woffBytes, srcPos) != woffBytes.length) {
            throw new IllegalArgumentException();
        }
        srcPos += 4;

        byte[] numTables = new byte[2];
        System.arraycopy(woffBytes, srcPos, numTables, 0, 2);
        srcPos += 2;

        // reserved
        if (bytesToUShort(woffBytes, srcPos) != 0) {
            throw new IllegalArgumentException();
        }
        srcPos += 2;

        long totalSfntSize = bytesToUInt(woffBytes, srcPos);
        srcPos += 4;

        srcPos += 2; // majorVersion
        srcPos += 2; // minorVersion
        srcPos += 4; // metaOffset
        srcPos += 4; // metaLength
        srcPos += 4; // metaOrigLength
        srcPos += 4; // privOffset
        srcPos += 4; // privLength


        byte[] otfBytes = new byte[(int) totalSfntSize]; // assuming font won't be larger than 2GB
        System.arraycopy(flavor, 0, otfBytes, destPos, 4);
        destPos += 4;
        System.arraycopy(numTables, 0, otfBytes, destPos, 2);
        destPos += 2;

        int entrySelector = -1;
        int searchRange = -1;
        int numTablesVal = bytesToUShort(numTables, 0);
        for (int i = 0; i < 17; ++i) {
            int powOfTwo = (int) Math.pow(2, i);
            if (powOfTwo > numTablesVal) {
                entrySelector = i;
                searchRange = powOfTwo * 16;
                break;
            }
        }
        if (entrySelector < 0) {
            throw new IllegalArgumentException();
        }
        otfBytes[destPos] = (byte) (searchRange >> 8);
        otfBytes[destPos + 1] = (byte) (searchRange);
        destPos += 2;
        otfBytes[destPos] = (byte) (entrySelector >> 8);
        otfBytes[destPos + 1] = (byte) (entrySelector);
        destPos += 2;
        int rangeShift = numTablesVal * 16 - searchRange;
        otfBytes[destPos] = (byte) (rangeShift >> 8);
        otfBytes[destPos + 1] = (byte) (rangeShift);
        destPos += 2;

        int outTableOffset = destPos;
        List<TableDirectory> tdList = new ArrayList<>(numTablesVal);
        for (int i = 0; i < numTablesVal; ++i) {
            TableDirectory td = new TableDirectory();
            System.arraycopy(woffBytes, srcPos, td.tag, 0, 4);
            srcPos += 4;
            td.offset = bytesToUInt(woffBytes, srcPos);
            srcPos += 4;

            if (td.offset % 4 != 0) {
                throw new IllegalArgumentException();
            }

            td.compLength = bytesToUInt(woffBytes, srcPos);
            srcPos += 4;
            System.arraycopy(woffBytes, srcPos, td.origLength, 0, 4);
            td.origLengthVal = bytesToUInt(td.origLength, 0);
            srcPos += 4;
            System.arraycopy(woffBytes, srcPos, td.origChecksum, 0, 4);
            srcPos += 4;

            tdList.add(td);
            outTableOffset += 4*4;
        }

        for (TableDirectory td : tdList) {
            System.arraycopy(td.tag, 0, otfBytes, destPos, 4);
            destPos += 4;

            System.arraycopy(td.origChecksum, 0, otfBytes, destPos, 4);
            destPos += 4;

            otfBytes[destPos] = (byte) (outTableOffset >> 24);
            otfBytes[destPos + 1] = (byte) (outTableOffset >> 16);
            otfBytes[destPos + 2] = (byte) (outTableOffset >> 8);
            otfBytes[destPos + 3] = (byte) (outTableOffset);
            destPos += 4;

            System.arraycopy(td.origLength, 0, otfBytes, destPos, 4);
            destPos += 4;

            td.outOffset = outTableOffset;

            outTableOffset += (int)td.origLengthVal;
            if (outTableOffset % 4 != 0) {
                outTableOffset += 4 - outTableOffset % 4;
            }
        }

        if (outTableOffset != totalSfntSize) {
            throw new IllegalArgumentException();
        }

        for (TableDirectory td : tdList) {
            byte[] compressedData = new byte[(int) td.compLength];
            byte[] uncompressedData;
            System.arraycopy(woffBytes, (int) td.offset, compressedData, 0, (int) td.compLength);
            int expectedUncompressedLen = (int) td.origLengthVal;
            if (td.compLength > td.origLengthVal) {
                throw new IllegalArgumentException();
            }
            if (td.compLength != td.origLengthVal) {
                ByteArrayInputStream stream = new ByteArrayInputStream(compressedData);
                InflaterInputStream zip = new InflaterInputStream(stream);
                uncompressedData = new byte[expectedUncompressedLen];
                int bytesRead = 0;
                while (expectedUncompressedLen - bytesRead > 0) {
                    int readRes = zip.read(uncompressedData, bytesRead, expectedUncompressedLen - bytesRead);
                    if (readRes < 0) {
                        throw new IllegalArgumentException();
                    }
                    bytesRead += readRes;
                }
                if (zip.read() >= 0) {
                    throw new IllegalArgumentException();
                }
            } else {
                uncompressedData = compressedData;
            }

            System.arraycopy(uncompressedData, 0, otfBytes, td.outOffset, expectedUncompressedLen);
        }

        return otfBytes;
    }

    private static long bytesToUInt(byte[] b, int start) {
        return (b[start] & 0xFFL) << 24
                | (b[start + 1] & 0xFFL) << 16
                | (b[start + 2] & 0xFFL) << 8
                | (b[start + 3] & 0xFFL);
    }

    private static int bytesToUShort(byte[] b, int start) {
        return (b[start] & 0xFF) << 8
                | (b[start + 1] & 0xFF);
    }

    private static class TableDirectory {
        byte[] tag = new byte[4];
        long offset;
        long compLength;
        byte[] origLength = new byte[4];
        long origLengthVal;
        byte[] origChecksum = new byte[4];
        int outOffset;
    }
}
