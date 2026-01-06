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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashSet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class DocumentRevisionPdfObjectComparatorTest extends ExtendedITextTest {


    @Test
    public void testUnsupportedDecodingTypeDoesNotThrowException() {
        PdfStream stream1 = new PdfStream();
        stream1.setData(new byte[] {0x01, 0x02, 0x03});
        stream1.put(PdfName.Filter, PdfName.JBIG2Decode);

        PdfStream stream2 = new PdfStream();
        stream2.setData(new byte[] {0x01, 0x02, 0x03});
        stream2.put(PdfName.Filter, PdfName.JBIG2Decode);

        AssertUtil.doesNotThrow(() -> {
            DocumentRevisionPdfObjectComparator.comparePdfObjects(stream1, stream2,
                    new Tuple2<>(new HashSet<>(), new HashSet<>()));
        });
    }

}
