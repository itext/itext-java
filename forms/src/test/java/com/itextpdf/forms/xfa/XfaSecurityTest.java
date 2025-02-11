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
package com.itextpdf.forms.xfa;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class XfaSecurityTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/xfa/XfaSecurityTest/";

    private static final String DTD_EXCEPTION_MESSAGE = ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage();

    private static final String XFA_WITH_DTD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE foo>\n"
            + "\n"
            + "<xdp:xdp xmlns:xdp=\"http://ns.adobe.com/xdp/\" timeStamp=\"2018-03-08T12:50:19Z\"\n"
            + "         uuid=\"36ac5111-55c5-4172-b0c1-0cbd783e2fcf\">\n"
            + "</xdp:xdp>\n";

    @BeforeEach
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(null);
    }

    @Test
    public void xfaExternalFileTest() throws IOException {
        xfaSecurityExceptionTest(SOURCE_FOLDER + "xfaExternalFile.pdf");
    }

    @Test
    public void xfaExternalConnectionTest() throws IOException {
        xfaSecurityExceptionTest(SOURCE_FOLDER + "xfaExternalConnection.pdf");
    }

    @Test
    public void xfaInternalEntityTest() throws IOException {
        xfaSecurityExceptionTest(SOURCE_FOLDER + "xfaInternalEntity.pdf");
    }

    @Test
    public void xfaExternalFileCustomFactoryTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "xfaExternalFile.pdf";
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFileName),
                new PdfWriter(new ByteArrayOutputStream()))) {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> PdfFormCreator.getAcroForm(pdfDoc, true)
            );
            Assertions.assertEquals(ExceptionTestUtil.getXxeTestMessage(), e.getMessage());
        }
    }

    @Test
    public void xfaExternalFileXfaFormTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "xfaExternalFile.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFileName))) {
            Exception e = Assertions.assertThrows(PdfException.class, () -> new XfaForm(pdfDoc));
            Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void xfaWithDtdXfaFormTest() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XFA_WITH_DTD_XML.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(PdfException.class, () -> new XfaForm(inputStream));
            Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void fillXfaFormTest() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XFA_WITH_DTD_XML.getBytes(StandardCharsets.UTF_8))) {
            XfaForm form = new XfaForm();
            Exception e = Assertions.assertThrows(PdfException.class, () -> form.fillXfaForm(inputStream, true));
            Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    private void xfaSecurityExceptionTest(String inputFileName) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFileName),
                new PdfWriter(new ByteArrayOutputStream()))) {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> PdfFormCreator.getAcroForm(pdfDoc, true)
            );
            Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }
}
