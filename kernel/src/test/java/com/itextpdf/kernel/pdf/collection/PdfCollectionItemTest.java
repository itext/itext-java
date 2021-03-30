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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCollectionItemTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void addItemTest() {
        final String fieldName = "fieldName";
        final String fieldValue = "fieldValue";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, fieldValue);
        Assert.assertEquals(fieldValue, item.getPdfObject().getAsString(new PdfName(fieldName)).getValue());
    }

    @Test
    public void addDateItemTest() {
        final String fieldName = "fieldName";
        final String timeValueAsString = "D:19860426012347+04'00'";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.D);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, new PdfDate(PdfDate.decode(timeValueAsString)));
        Assert.assertTrue(((PdfString)field.getValue(timeValueAsString)).getValue().startsWith("D:1986"));
    }

    @Test
    public void dontAddDateItemToAnotherSubTypeFieldTest() {
        final String fieldName = "fieldName";
        final String timeValueAsString = "D:19860426012347+04'00'";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.F);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, new PdfDate(PdfDate.decode(timeValueAsString)));
        Assert.assertNull(item.getPdfObject().getAsString(new PdfName(fieldName)));
    }

    @Test
    public void addNumberItemTest() {
        final String fieldName = "fieldName";

        final double numberValue = 0.1234;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.N);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, new PdfNumber(numberValue));
        Assert.assertEquals(numberValue, item.getPdfObject().getAsNumber(new PdfName(fieldName)).getValue(), 0.0001);
    }

    @Test
    public void dontAddNumberItemToAnotherSubTypeFieldTest() {
        final String fieldName = "fieldName";

        final double numberValue = 0.1234;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.F);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, new PdfNumber(numberValue));
        Assert.assertNull(item.getPdfObject().getAsString(new PdfName(fieldName)));
    }

    @Test
    public void addPrefixTest() {
        final String fieldName = "fieldName";
        final String fieldValue = "fieldValue";
        final String fieldPrefix = "fieldPrefix";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        item.addItem(fieldName, fieldValue);
        item.setPrefix(fieldName, fieldPrefix);

        Assert.assertEquals(fieldValue, item.getPdfObject().getAsDictionary(new PdfName(fieldName))
                .getAsString(PdfName.D).getValue());
        Assert.assertEquals(fieldPrefix, item.getPdfObject().getAsDictionary(new PdfName(fieldName))
                .getAsString(PdfName.P).getValue());
    }

    @Test
    public void addPrefixToEmptyFieldTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.YouMustSetAValueBeforeAddingAPrefix);
        final String fieldName = "fieldName";
        final String fieldPrefix = "fieldPrefix";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        PdfCollectionSchema schema = new PdfCollectionSchema();

        schema.addField(fieldName, field);

        PdfCollectionItem item = new PdfCollectionItem(schema);

        // this line will throw an exception as setPrefix() method may be called
        // only if value was set previously
        item.setPrefix(fieldName, fieldPrefix);
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        PdfCollectionItem item = new PdfCollectionItem(new PdfCollectionSchema());

        Assert.assertFalse(item.isWrappedObjectMustBeIndirect());
    }
}
