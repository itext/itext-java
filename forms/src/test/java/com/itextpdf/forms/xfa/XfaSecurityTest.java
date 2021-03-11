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
package com.itextpdf.forms.xfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class XfaSecurityTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/xfa/XfaSecurityTest/";

    private static final String DTD_EXCEPTION_MESSAGE = ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage();

    private static final String XFA_WITH_DTD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE foo>\n"
            + "\n"
            + "<xdp:xdp xmlns:xdp=\"http://ns.adobe.com/xdp/\" timeStamp=\"2018-03-08T12:50:19Z\"\n"
            + "         uuid=\"36ac5111-55c5-4172-b0c1-0cbd783e2fcf\">\n"
            + "</xdp:xdp>\n";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(new DefaultSafeXmlParserFactory());
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
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(ExceptionTestUtil.getXxeTestMessage());
            PdfAcroForm.getAcroForm(pdfDoc, true);
        }
    }

    @Test
    public void xfaExternalFileXfaFormTest() throws IOException {
        String inFileName = SOURCE_FOLDER + "xfaExternalFile.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inFileName))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
            new XfaForm(pdfDoc);
        }
    }

    @Test
    public void xfaWithDtdXfaFormTest() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XFA_WITH_DTD_XML.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
            new XfaForm(inputStream);
        }
    }

    @Test
    public void fillXfaFormTest() throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XFA_WITH_DTD_XML.getBytes(StandardCharsets.UTF_8))) {
            XfaForm form = new XfaForm();
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
            form.fillXfaForm(inputStream, true);
        }
    }

    private void xfaSecurityExceptionTest(String inputFileName) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputFileName),
                new PdfWriter(new ByteArrayOutputStream()))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
            PdfAcroForm.getAcroForm(pdfDoc, true);
        }
    }
}
