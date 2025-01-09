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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfFreeTextAnnotationTest extends ExtendedITextTest {

    @Test
    public void setAndGetRotationTest() {
        PdfFreeTextAnnotation freeTextAnnotation = new PdfFreeTextAnnotation(new Rectangle(0, 0, 100, 50),
                new PdfString("content"));
        freeTextAnnotation.setRotation(135);
        Assertions.assertEquals(135, freeTextAnnotation.getRotation().intValue());
    }

    @Test
    public void setAndGetJustificationTest() {
        PdfFreeTextAnnotation freeTextAnnotation = new PdfFreeTextAnnotation(new Rectangle(0, 0, 100, 50), null);
        Assertions.assertEquals(PdfFreeTextAnnotation.LEFT_JUSTIFIED, freeTextAnnotation.getJustification());
        freeTextAnnotation.setJustification(PdfFreeTextAnnotation.CENTERED);
        Assertions.assertEquals(PdfFreeTextAnnotation.CENTERED, freeTextAnnotation.getJustification());
    }
}
