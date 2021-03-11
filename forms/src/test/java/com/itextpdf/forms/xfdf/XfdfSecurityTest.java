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
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class XfdfSecurityTest extends ExtendedITextTest {

    private static final String XFDF_WITH_XXE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
            + "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"xxe-data.txt\" > ]>\n"
            + "<xfdf xmlns=\"http://ns.adobe.com/xfdf/\" xml:space=\"preserve\">\n"
            + "<f href=\"something.pdf\"/>\n"
            + "<fields\n"
            + "><field name=\"Input field\"\n"
            + "><value\n"
            + ">ABCDEFGH&xxe;</value\n"
            + "></field\n"
            + "></fields\n"
            + ">\n"
            + "<ids/>\n"
            + "</xfdf>";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void xxeVulnerabilityXfdfTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(new DefaultSafeXmlParserFactory());
        try (InputStream inputStream = new ByteArrayInputStream(XFDF_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage(ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage());
            XfdfFileUtils.createXfdfDocumentFromStream(inputStream);
        }
    }

    @Test
    public void xxeVulnerabilityXfdfCustomXmlParserTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        try (InputStream inputStream = new ByteArrayInputStream(XFDF_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(PdfException.class);
            junitExpectedException.expectMessage("Test message");
            XfdfFileUtils.createXfdfDocumentFromStream(inputStream);
        }
    }

    @Test
    public void customXmlParserCreateNewXfdfDocumentExceptionTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(new ExceptionTestXmlParserFactory());
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(ExceptionTestUtil.getXxeTestMessage());
        XfdfFileUtils.createNewXfdfDocument();
    }
}
