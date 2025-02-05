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

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;


@Tag("UnitTest")
public class PdfTextArrayTest extends ExtendedITextTest {

    @Test
    public void addNZeroTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assertions.assertFalse(textArray.add(0.0f));
        Assertions.assertTrue(textArray.isEmpty());
    }

    @Test
    public void additiveInverseTest() {
        PdfTextArray textArray = new PdfTextArray();
        float number = 10;
        textArray.add(number);
        textArray.add(number * -1);
        Assertions.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addEmptyStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assertions.assertFalse(textArray.add(""));
        Assertions.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNewStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String content = "content";
        Assertions.assertTrue(textArray.add(content));
        Assertions.assertEquals(new PdfString(content), textArray.get(0));
    }

    @Test
    public void appendStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String[] stringArray = new String[] {"one", "element"};
        for (String string : stringArray)
            textArray.add(string);
        PdfString expected = new PdfString(stringArray[0] + stringArray[1]);
        Assertions.assertEquals(expected, textArray.get(0));
    }

    @Test
    public void addStringWithFontTest() throws IOException {
        PdfTextArray textArray = new PdfTextArray();
        String string = "font";
        textArray.add(string, PdfFontFactory.createFont());
        Assertions.assertEquals(new PdfString(string), textArray.get(0));
    }

    @Test
    public void addAllNullTest() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll((PdfArray) null);
        Assertions.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNumbersTest() {
        PdfTextArray textArray = new PdfTextArray();
        float a = 5f;
        float b = 10f;
        textArray.add(new PdfNumber(a));
        textArray.add(new PdfNumber(b));
        Assertions.assertEquals(a + b, textArray.getAsNumber(0).floatValue(), 0.0001);
    }

    @Test
    public void addCollectionTest() {
        PdfArray collection = new PdfArray();
        collection.add(new PdfString("str"));
        collection.add(new PdfNumber(11));

        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll(collection);
        Assertions.assertEquals(collection.list, textArray.list);
    }

    @Test
    public void addZeroSumTest() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(new PdfString("test"));
        textArray.add(new PdfNumber(11));
        textArray.add(new PdfNumber(12));
        textArray.add(new PdfNumber(-13));
        textArray.add(new PdfNumber(8));
        textArray.add(new PdfNumber(-18));
        textArray.add(new PdfString("test"));

        PdfArray expected = new PdfArray();
        expected.add(new PdfString("test"));
        expected.add(new PdfString("test"));
        Assertions.assertEquals(expected.list, textArray.list);
    }

    @Test
    public void addZeroSumAtTheBeginningTest() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.add(new PdfNumber(11));
        textArray.add(new PdfNumber(-11));
        textArray.add(new PdfNumber(13));
        textArray.add(new PdfString("test"));

        PdfArray expected = new PdfArray();
        expected.add(new PdfNumber(13));
        expected.add(new PdfString("test"));
        Assertions.assertEquals(expected.list, textArray.list);
    }


}
