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
package com.itextpdf.kernel.pdf.layer;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("UnitTest")
public class PdfVisibilityExpressionTest extends ExtendedITextTest {

    @Test
    public void expressionByArrayTest() {
        PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        PdfArray array = new PdfArray();

        // add the AND operator as the first parameter of the expression
        array.add(PdfName.And);

        // add two empty dictionaries as the other parameters
        array.add(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)).getPdfObject());
        array.add(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)).getPdfObject());

        // create visibility expression
        PdfVisibilityExpression expression = new PdfVisibilityExpression(array);

        PdfObject expressionObject = expression.getPdfObject();
        Assertions.assertTrue(expressionObject instanceof PdfArray);
        Assertions.assertEquals(3, ((PdfArray) expressionObject).size());
        Assertions.assertEquals(PdfName.And, ((PdfArray) expressionObject).getAsName(0));
    }

    @Test
    public void andExpressionTest() {
        PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        // create expression with the AND operator as the first parameter
        PdfVisibilityExpression expression = new PdfVisibilityExpression(PdfName.And);

        // add two empty dictionaries as the other parameters
        expression.addOperand(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)));
        expression.addOperand(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)));

        PdfObject expressionObject = expression.getPdfObject();
        Assertions.assertTrue(expressionObject instanceof PdfArray);
        Assertions.assertEquals(3, ((PdfArray) expressionObject).size());
        Assertions.assertEquals(PdfName.And, ((PdfArray) expressionObject).getAsName(0));
    }

    @Test
    public void nestedExpressionTest() {
        PdfDocument tempDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

        // create expression with the OR operator as the first parameter
        PdfVisibilityExpression expression = new PdfVisibilityExpression(PdfName.Or);

        // add an empty dictionary as the second parameter
        expression.addOperand(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)));

        // create a nested expression with the AND operator and two empty dictionaries as parameters
        PdfVisibilityExpression nestedExpression = new PdfVisibilityExpression(PdfName.And);
        nestedExpression.addOperand(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)));
        nestedExpression.addOperand(new PdfLayer((PdfDictionary) new PdfDictionary().makeIndirect(tempDoc)));

        // add another expression as the third parameter
        expression.addOperand(nestedExpression);

        PdfObject expressionObject = expression.getPdfObject();
        Assertions.assertTrue(expressionObject instanceof PdfArray);
        Assertions.assertEquals(3, ((PdfArray) expressionObject).size());
        Assertions.assertEquals(PdfName.Or, ((PdfArray) expressionObject).getAsName(0));

        PdfObject child = ((PdfArray) expressionObject).get(2);
        Assertions.assertTrue(child instanceof PdfArray);
        Assertions.assertEquals(3, ((PdfArray) child).size());
        Assertions.assertEquals(PdfName.And, ((PdfArray) child).get(0));
    }

}
