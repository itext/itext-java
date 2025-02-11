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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfCollectionFieldTest extends ExtendedITextTest {

    private static final PdfName[] ALLOWED_PDF_NAMES = {
            PdfName.D,
            PdfName.N,
            PdfName.F,
            PdfName.Desc,
            PdfName.ModDate,
            PdfName.CreationDate,
            PdfName.Size
    };
    private static final int[] EXPECTED_SUB_TYPES = {
            PdfCollectionField.DATE,
            PdfCollectionField.NUMBER,
            PdfCollectionField.FILENAME,
            PdfCollectionField.DESC,
            PdfCollectionField.MODDATE,
            PdfCollectionField.CREATIONDATE,
            PdfCollectionField.SIZE
    };

    @Test
    public void subTypeInConstructorTest() {
        for(int i = 0; i < ALLOWED_PDF_NAMES.length; i++) {
            PdfDictionary pdfObject = new PdfDictionary();
            pdfObject.put(PdfName.Subtype, ALLOWED_PDF_NAMES[i]);

            PdfCollectionField field = new PdfCollectionField(pdfObject);

            Assertions.assertEquals(field.subType, EXPECTED_SUB_TYPES[i]);
        }
    }

    @Test
    public void defaultSubTypeInConstructorTest() {
        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);

        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertEquals(field.subType, PdfCollectionField.TEXT);
    }

    @Test
    public void fieldCreationWithNameAndSubTypeTest() {
        final String fieldName = "fieldName";

        for(int i = 0; i < ALLOWED_PDF_NAMES.length; i++) {
            PdfCollectionField field = new PdfCollectionField(fieldName, EXPECTED_SUB_TYPES[i]);

            Assertions.assertEquals(new PdfString(fieldName), field.getPdfObject().get(PdfName.N));
            Assertions.assertEquals(ALLOWED_PDF_NAMES[i], field.getPdfObject().get(PdfName.Subtype));
        }
    }

    @Test
    public void fieldCreationWithDefaultSubTypeTest() {
        final String fieldName = "fieldName";
        final int unexpectedSubType = -1;
        final PdfName defaultSubType = PdfName.S;

        PdfCollectionField field = new PdfCollectionField(fieldName, unexpectedSubType);

        Assertions.assertEquals(defaultSubType, field.getPdfObject().get(PdfName.Subtype));
    }

    @Test
    public void orderPropertyTest() {
        final int testOrder = 5;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertNull(field.getOrder());

        field.setOrder(testOrder);

        Assertions.assertEquals(testOrder, field.getOrder().intValue());
    }

    @Test
    public void visibilityPropertyTest() {
        final boolean testVisibility = true;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertNull(field.getVisibility());

        field.setVisibility(testVisibility);

        Assertions.assertEquals(testVisibility, field.getVisibility().getValue());
    }

    @Test
    public void editablePropertyTest() {
        final boolean testEditable = true;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertNull(field.getEditable());

        field.setEditable(testEditable);

        Assertions.assertEquals(testEditable, field.getEditable().getValue());
    }

    @Test
    public void getTextValueTest() {
        final String textValue = "some text";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertEquals(new PdfString(textValue), field.getValue(textValue));
    }

    @Test
    public void getNumberValueTest() {
        final double numberValue = 125;
        final String numberValueAsString = String.valueOf(numberValue);

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.N);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertEquals(numberValue, ((PdfNumber)field.getValue(numberValueAsString)).getValue(), 0.0001);
    }

    @Test
    public void getDateValueTest() {
        final String timeValueAsString = "D:19860426012347+04'00'";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.D);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertTrue(((PdfString)field.getValue(timeValueAsString)).getValue().startsWith("D:1986"));
    }

    @Test
    public void getUnsupportedTypeValueTest() {
        final String stringValue = "string value";
        final String fieldName = "fieldName";

        PdfCollectionField field = new PdfCollectionField(fieldName, PdfCollectionField.FILENAME);

        // this line will throw an exception as getValue() method is not
        // supported for subType which differs from S, N and D.
        Exception e = Assertions.assertThrows(PdfException.class, () -> field.getValue(stringValue));
        Assertions.assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.UNACCEPTABLE_FIELD_VALUE,
                stringValue, fieldName), e.getMessage());
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assertions.assertFalse(field.isWrappedObjectMustBeIndirect());
    }

}
