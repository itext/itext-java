/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.forms.form.element;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SelectFieldItemTest extends ITextTest {

    @Test
    public void newSelectFieldItem2ParamConstructorTest() {
        SelectFieldItem item = new SelectFieldItem("exportValue", "displayValue");
        Assert.assertEquals("exportValue", item.getExportValue());
        Assert.assertEquals("displayValue", item.getDisplayValue());
    }

    @Test
    public void newSelectFieldItem1ParamConstructorTest() {
        SelectFieldItem item = new SelectFieldItem("exportValue");
        Assert.assertEquals("exportValue", item.getExportValue());
        Assert.assertEquals("exportValue", item.getDisplayValue());
    }

    @Test
    public void newSelectFieldItem3ParamConstructorTest1() {
        SelectFieldItem item = new SelectFieldItem("exportValue", "displayValue", new Paragraph("displayValue"));
        Assert.assertEquals("exportValue", item.getExportValue());
        Assert.assertEquals("displayValue", item.getDisplayValue());
        Assert.assertTrue(item.getElement() instanceof Paragraph);
    }


    @Test
    public void newSelectFieldItem3ParamConstructorTest2() {
        Assert.assertThrows(PdfException.class, () -> {
            new SelectFieldItem("exportValue", "displayValue", null);
        });
    }

    @Test
    public void newSelectFieldItem3ParamConstructorTest3() {
        Assert.assertThrows(PdfException.class, () -> {
            new SelectFieldItem(null, "displayValue", new Paragraph("displayValue"));
        });
    }

    @Test
    public void hasExportAndDisplayValuesTest01() {
        SelectFieldItem item = new SelectFieldItem("exportValue", "displayValue");
        Assert.assertTrue(item.hasExportAndDisplayValues());
    }

    @Test
    public void hasExportAndDisplayValuesTest02() {
        SelectFieldItem item = new SelectFieldItem("exportValue");
        Assert.assertFalse(item.hasExportAndDisplayValues());
    }

    @Test
    public void hasExportAndDisplayValuesTest03() {
        SelectFieldItem item = new SelectFieldItem("exportValue", new Paragraph("displayValue"));
        Assert.assertFalse(item.hasExportAndDisplayValues());
    }

    @Test
    public void hasExportAndDisplayValuesTest04() {
        SelectFieldItem item = new SelectFieldItem("exportValue", null, new Paragraph("displayValue"));
        Assert.assertFalse(item.hasExportAndDisplayValues());
    }

    @Test
    public void hasExportAndDisplayValuesTest05() {
        SelectFieldItem item = new SelectFieldItem("exportValue", "displayValue", new Paragraph("displayValue"));
        Assert.assertTrue(item.hasExportAndDisplayValues());
    }
}
