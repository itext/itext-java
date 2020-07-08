/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.forms.xfdf;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Category(UnitTest.class)
public class XfdfWriterUnitTest extends ExtendedITextTest {

    @Test
    public void fieldEmptyValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");
        fieldObject.setValue("");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertNull(childOfFields.getFirstChild());
    }

    @Test
    public void fieldNullValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertNull(childOfFields.getFirstChild());
    }

    @Test
    public void fieldWithValueUnitTest() throws ParserConfigurationException {
        Document document = XfdfFileUtils.createNewXfdfDocument();

        Element fields = document.createElement("fields");
        document.appendChild(fields);

        FieldObject fieldObject = new FieldObject();
        fieldObject.setName("testname");
        fieldObject.setValue("testValue");

        List<FieldObject> fieldList = new ArrayList<>();

        XfdfWriter.addField(fieldObject, fields, document, fieldList);

        Node childOfFields = fields.getFirstChild();

        Assert.assertNotNull(childOfFields);
        Assert.assertEquals("value", childOfFields.getFirstChild().getNodeName());
        Assert.assertEquals("testValue", childOfFields.getFirstChild().getTextContent());
    }
}
