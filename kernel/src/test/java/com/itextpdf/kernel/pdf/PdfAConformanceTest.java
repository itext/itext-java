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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.impl.XMPMetaImpl;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfAConformanceTest extends ExtendedITextTest {
    @Test
    public void getConformanceTest() {
        Assertions.assertEquals(PdfAConformance.PDF_A_4, PdfConformance.getAConformance("4", null));
        Assertions.assertEquals(PdfAConformance.PDF_A_4E, PdfConformance.getAConformance("4", "E"));
        Assertions.assertEquals(PdfAConformance.PDF_A_4F, PdfConformance.getAConformance("4", "F"));
    }

    @Test
    public void getXmpConformanceNullTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "4");
        PdfConformance level = PdfConformance.getConformance(meta);
        Assertions.assertEquals(PdfAConformance.PDF_A_4, level.getAConformance());
        Assertions.assertEquals(PdfConformance.PDF_A_4, level);
    }

    @Test
    public void getXmpConformanceBTest() throws XMPException {
        XMPMeta meta = new XMPMetaImpl();
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, "2");
        meta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, "B");
        PdfConformance level = PdfConformance.getConformance(meta);
        Assertions.assertEquals(PdfAConformance.PDF_A_2B, level.getAConformance());
        Assertions.assertEquals(PdfConformance.PDF_A_2B, level);
    }
}
