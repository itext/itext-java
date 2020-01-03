/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;


@Category(UnitTest.class)
public class PdfTextArrayTest extends ExtendedITextTest {

    @Test
    public void addNZeroTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assert.assertFalse(textArray.add(0.0f));
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void additiveInverseTest() {
        PdfTextArray textArray = new PdfTextArray();
        float number = 10;
        textArray.add(number);
        textArray.add(number * -1);
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addEmptyStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        Assert.assertFalse(textArray.add(""));
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNewStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String content = "content";
        Assert.assertTrue(textArray.add(content));
        Assert.assertEquals(new PdfString(content), textArray.get(0));
    }

    @Test
    public void appendStringTest() {
        PdfTextArray textArray = new PdfTextArray();
        String[] stringArray = new String[] {"one", "element"};
        for (String string : stringArray)
            textArray.add(string);
        PdfString expected = new PdfString(stringArray[0] + stringArray[1]);
        Assert.assertEquals(expected, textArray.get(0));
    }

    @Test
    public void addStringWithFontTest() throws IOException {
        PdfTextArray textArray = new PdfTextArray();
        String string = "font";
        textArray.add(string, PdfFontFactory.createFont());
        Assert.assertEquals(new PdfString(string), textArray.get(0));
    }

    @Test
    public void addAllNullTest() {
        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll((PdfArray) null);
        Assert.assertTrue(textArray.isEmpty());
    }

    @Test
    public void addNumbersTest() {
        PdfTextArray textArray = new PdfTextArray();
        float a = 5f;
        float b = 10f;
        textArray.add(new PdfNumber(a));
        textArray.add(new PdfNumber(b));
        Assert.assertEquals(a + b, textArray.getAsNumber(0).floatValue(), 0.0001);
    }

    @Test
    public void addCollectionTest() {
        PdfArray collection = new PdfArray();
        collection.add(new PdfString("str"));
        collection.add(new PdfNumber(11));

        PdfTextArray textArray = new PdfTextArray();
        textArray.addAll(collection);
        Assert.assertEquals(collection.list, textArray.list);
    }
}
