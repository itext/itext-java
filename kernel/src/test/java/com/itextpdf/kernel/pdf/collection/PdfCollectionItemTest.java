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
package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfCollectionItemTest extends ExtendedITextTest {

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
        Assertions.assertEquals(fieldValue, item.getPdfObject().getAsString(new PdfName(fieldName)).getValue());
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
        Assertions.assertTrue(((PdfString)field.getValue(timeValueAsString)).getValue().startsWith("D:1986"));
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
        Assertions.assertNull(item.getPdfObject().getAsString(new PdfName(fieldName)));
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
        Assertions.assertEquals(numberValue, item.getPdfObject().getAsNumber(new PdfName(fieldName)).getValue(), 0.0001);
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
        Assertions.assertNull(item.getPdfObject().getAsString(new PdfName(fieldName)));
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

        Assertions.assertEquals(fieldValue, item.getPdfObject().getAsDictionary(new PdfName(fieldName))
                .getAsString(PdfName.D).getValue());
        Assertions.assertEquals(fieldPrefix, item.getPdfObject().getAsDictionary(new PdfName(fieldName))
                .getAsString(PdfName.P).getValue());
    }

    @Test
    public void addPrefixToEmptyFieldTest() {
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
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> item.setPrefix(fieldName, fieldPrefix)
        );
        Assertions.assertEquals(KernelExceptionMessageConstant.YOU_MUST_SET_A_VALUE_BEFORE_ADDING_A_PREFIX, e.getMessage());
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        PdfCollectionItem item = new PdfCollectionItem(new PdfCollectionSchema());

        Assertions.assertFalse(item.isWrappedObjectMustBeIndirect());
    }
}
