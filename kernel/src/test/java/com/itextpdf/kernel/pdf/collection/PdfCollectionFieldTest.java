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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCollectionFieldTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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

            Assert.assertEquals(field.subType, EXPECTED_SUB_TYPES[i]);
        }
    }

    @Test
    public void defaultSubTypeInConstructorTest() {
        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);

        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertEquals(field.subType, PdfCollectionField.TEXT);
    }

    @Test
    public void fieldCreationWithNameAndSubTypeTest() {
        final String fieldName = "fieldName";

        for(int i = 0; i < ALLOWED_PDF_NAMES.length; i++) {
            PdfCollectionField field = new PdfCollectionField(fieldName, EXPECTED_SUB_TYPES[i]);

            Assert.assertEquals(new PdfString(fieldName), field.getPdfObject().get(PdfName.N));
            Assert.assertEquals(ALLOWED_PDF_NAMES[i], field.getPdfObject().get(PdfName.Subtype));
        }
    }

    @Test
    public void fieldCreationWithDefaultSubTypeTest() {
        final String fieldName = "fieldName";
        final int unexpectedSubType = -1;
        final PdfName defaultSubType = PdfName.S;

        PdfCollectionField field = new PdfCollectionField(fieldName, unexpectedSubType);

        Assert.assertEquals(defaultSubType, field.getPdfObject().get(PdfName.Subtype));
    }

    @Test
    public void orderPropertyTest() {
        final int testOrder = 5;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertNull(field.getOrder());

        field.setOrder(testOrder);

        Assert.assertEquals(testOrder, field.getOrder().intValue());
    }

    @Test
    public void visibilityPropertyTest() {
        final boolean testVisibility = true;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertNull(field.getVisibility());

        field.setVisibility(testVisibility);

        Assert.assertEquals(testVisibility, field.getVisibility().getValue());
    }

    @Test
    public void editablePropertyTest() {
        final boolean testEditable = true;

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertNull(field.getEditable());

        field.setEditable(testEditable);

        Assert.assertEquals(testEditable, field.getEditable().getValue());
    }

    @Test
    public void getTextValueTest() {
        final String textValue = "some text";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertEquals(new PdfString(textValue), field.getValue(textValue));
    }

    @Test
    public void getNumberValueTest() {
        final double numberValue = 125;
        final String numberValueAsString = String.valueOf(numberValue);

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.N);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertEquals(numberValue, ((PdfNumber)field.getValue(numberValueAsString)).getValue(), 0.0001);
    }

    @Test
    public void getDateValueTest() {
        final String timeValueAsString = "D:19860426012347+04'00'";

        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.D);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertTrue(((PdfString)field.getValue(timeValueAsString)).getValue().startsWith("D:1986"));
    }

    @Test
    public void getUnsupportedTypeValueTest() {
        final String stringValue = "string value";
        final String fieldName = "fieldName";

        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(PdfException._1IsNotAnAcceptableValueForTheField2,
                stringValue, fieldName));

        PdfCollectionField field = new PdfCollectionField(fieldName, PdfCollectionField.FILENAME);

        // this line will throw an exception as getValue() method is not
        // supported for subType which differs from S, N and D.
        field.getValue(stringValue);
    }

    @Test
    public void isWrappedObjectMustBeIndirectTest() {
        PdfDictionary pdfObject = new PdfDictionary();
        pdfObject.put(PdfName.Subtype, PdfName.S);
        PdfCollectionField field = new PdfCollectionField(pdfObject);

        Assert.assertFalse(field.isWrappedObjectMustBeIndirect());
    }

}
