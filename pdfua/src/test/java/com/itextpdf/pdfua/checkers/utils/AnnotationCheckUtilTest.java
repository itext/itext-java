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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
@Deprecated
public class AnnotationCheckUtilTest extends ExtendedITextTest {

    @Test
    public void testIsAnnotationVisible() {
        assertTrue(AnnotationCheckUtil.isAnnotationVisible(new PdfDictionary()));
    }

    @Test
    public void annotationHandler(){
        AnnotationCheckUtil.AnnotationHandler handler = new AnnotationCheckUtil.AnnotationHandler(new PdfUAValidationContext(null));
        assertNotNull(handler);
        assertFalse(handler.accept(null));
        assertTrue(handler.accept(new PdfMcrNumber(new PdfNumber(2), null)));
        AssertUtil.doesNotThrow(() -> handler.processElement(null));
    }
}