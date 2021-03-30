/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.barcodes;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BarcodePDF417UnitTest extends ExtendedITextTest {

    @Test
    public void barcode417CodeRowsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeRows(150);

        Assert.assertEquals(150, barcode.getCodeRows());
    }

    @Test
    public void barcode417CodeColumnsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCodeColumns(150);

        Assert.assertEquals(150, barcode.getCodeColumns());
    }

    @Test
    public void barcode417CodeWordsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setLenCodewords(150);

        Assert.assertEquals(150, barcode.getLenCodewords());
    }

    @Test
    public void barcode417ErrorLevelTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setErrorLevel(3);

        Assert.assertEquals(3, barcode.getErrorLevel());
    }

    @Test
    public void barcode417GetCodeWordsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();

        Assert.assertEquals(928, barcode.getCodewords().length);
    }

    @Test
    public void barcode417OptionsTest() {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setOptions(100);

        Assert.assertEquals(100, barcode.getOptions());
    }

    @Test
    public void barcode417MaxSquareTest() {
        String text = "Call me Ishmael. Some years ago--never mind how long "
                + "precisely --having little or no money in my purse, and nothing "
                + "particular to interest me on shore, I thought I would sail about "
                + "a little and see the watery part of the world.";
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode(text);

        Assert.assertEquals(928, barcode.getMaxSquare());
    }
}
