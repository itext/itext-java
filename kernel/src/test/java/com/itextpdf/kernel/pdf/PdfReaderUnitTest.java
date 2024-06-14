/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfReaderUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfReaderUnitTest/";

    @Test
    public void readStreamBytesRawNullStreamTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readStreamBytesRaw(null));
        Assert.assertEquals(KernelExceptionMessageConstant.UNABLE_TO_READ_STREAM_BYTES, e.getMessage());
    }

    @Test
    public void readObjectStreamNullStreamTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readObjectStream(null));
        Assert.assertEquals(KernelExceptionMessageConstant.UNABLE_TO_READ_OBJECT_STREAM, e.getMessage());
    }

    @Test
    public void readObjectInvalidObjectStreamNumberTest() throws java.io.IOException {
        PdfReader reader = new PdfReader(SOURCE_FOLDER + "testFile.pdf");
        PdfDocument doc = new PdfDocument(reader);

        PdfIndirectReference ref = new PdfIndirectReference(doc, 20);
        ref.setState(PdfObject.FREE);
        ref.setObjStreamNumber(5);
        ref.refersTo = null;

        PdfIndirectReference ref2 = new PdfIndirectReference(doc, 5);
        ref2.setState(PdfObject.FREE);
        ref2.refersTo = null;
        doc.getXref().add(ref2);

        doc.getCatalog().getPdfObject().put(PdfName.StructTreeRoot, ref);
        Exception e = Assert.assertThrows(PdfException.class, () -> reader.readObject(ref));
        Assert.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.INVALID_OBJECT_STREAM_NUMBER, 20, 5, 0), e.getMessage());
    }
}