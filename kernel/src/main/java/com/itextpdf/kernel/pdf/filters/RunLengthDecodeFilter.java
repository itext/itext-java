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

import com.itextpdf.kernel.pdf.MemoryLimitsAwareFilter;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;

import java.io.ByteArrayOutputStream;

/**
 * Handles RunLengthDecode filter.
 */
public class RunLengthDecodeFilter extends MemoryLimitsAwareFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] b, PdfName filterName, PdfObject decodeParams, PdfDictionary streamDictionary) {
        ByteArrayOutputStream outputStream = enableMemoryLimitsAwareHandler(streamDictionary);
        byte dupCount;
        for (int i = 0; i < b.length; i++) {
            dupCount = b[i];
            if (dupCount == (byte) 0x80) {
                // this is implicit end of data

                break;
            }
            if ((dupCount & 0x80) == 0) {
                int bytesToCopy = dupCount + 1;
                outputStream.write(b, i + 1, bytesToCopy);
                i += bytesToCopy;
            } else {
                // make dupcount copies of the next byte

                i++;
                for (int j = 0; j < 257 - (dupCount & 0xff); j++) {
                    outputStream.write(b[i]);
                }
            }
        }
        return outputStream.toByteArray();
    }
}
