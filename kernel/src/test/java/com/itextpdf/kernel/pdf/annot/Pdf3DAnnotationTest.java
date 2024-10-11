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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class Pdf3DAnnotationTest extends ExtendedITextTest {

    @Test
    public void setAndGetDefaultInitialViewTest() {
        Pdf3DAnnotation pdf3DAnnotation = new Pdf3DAnnotation(new PdfDictionary());
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Type, new PdfName("3DView"));

        pdf3DAnnotation.setDefaultInitialView(dict);

        Assertions.assertEquals(dict, pdf3DAnnotation.getDefaultInitialView());
    }

    @Test
    public void setAndGetActivationDictionaryTest() {
        Pdf3DAnnotation pdf3DAnnotation = new Pdf3DAnnotation(new PdfDictionary());
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Type, new PdfName("3DView"));

        pdf3DAnnotation.setActivationDictionary(dict);

        Assertions.assertEquals(dict, pdf3DAnnotation.getActivationDictionary());
    }

    @Test
    public void setAndIsInteractiveTest() {
        Pdf3DAnnotation pdf3DAnnotation = new Pdf3DAnnotation(new PdfDictionary());
        boolean flag = true;

        pdf3DAnnotation.setInteractive(flag);

        Assertions.assertEquals(flag, pdf3DAnnotation.isInteractive().getValue());
    }

    @Test
    public void setAndGetViewBoxTest() {
        Pdf3DAnnotation pdf3DAnnotation = new Pdf3DAnnotation(new PdfDictionary());
        Rectangle rect = new Rectangle(10, 10);

        pdf3DAnnotation.setViewBox(rect);

        boolean result = rect.equalsWithEpsilon(pdf3DAnnotation.getViewBox());
        Assertions.assertTrue(result, "Rectangles are different");
    }
}
