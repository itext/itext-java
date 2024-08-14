/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.impl.XMPMetaImpl;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfAConformanceLevelTest extends ExtendedITextTest {
    @Test
    public void getConformanceTest() {
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_4, PdfAConformanceLevel.getConformanceLevel("4", null));
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_4E, PdfAConformanceLevel.getConformanceLevel("4", "E"));
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_4F, PdfAConformanceLevel.getConformanceLevel("4", "F"));
    }

    @Test
    public void getXmpConformanceNullTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "4");
        PdfAConformanceLevel level = PdfAConformanceLevel.getConformanceLevel(meta);
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_4, level);
    }

    @Test
    public void getXmpConformanceBTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, "B");
        PdfAConformanceLevel level = PdfAConformanceLevel.getConformanceLevel(meta);
        Assertions.assertEquals(PdfAConformanceLevel.PDF_A_2B, level);
    }
}
