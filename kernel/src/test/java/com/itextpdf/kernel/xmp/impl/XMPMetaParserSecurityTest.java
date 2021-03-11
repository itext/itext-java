/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.xmp.impl;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class XMPMetaParserSecurityTest extends ExtendedITextTest {

    private static final String XMP_WITH_XXE = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n"
            + "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"xxe-data.txt\" > ]>\n"
            + "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n"
            + "    <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
            + "        <rdf:Description rdf:about=\"\" xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\">\n"
            + "            <pdfaid:part>&xxe;1</pdfaid:part>\n"
            + "            <pdfaid:conformance>B</pdfaid:conformance>\n"
            + "        </rdf:Description>\n"
            + "    </rdf:RDF>\n"
            + "</x:xmpmeta>\n"
            + "<?xpacket end=\"r\"?>";

    private static final String DTD_EXCEPTION_MESSAGE = ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage();

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Before
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(new DefaultSafeXmlParserFactory());
    }

    @Test
    public void xxeTestFromString() throws XMPException {
        junitExpectedException.expect(XMPException.class);
        junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
        XMPMetaParser.parse(XMP_WITH_XXE, null);
    }

    @Test
    public void xxeTestFromByteBuffer() throws XMPException {
        junitExpectedException.expect(XMPException.class);
        junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
        XMPMetaParser.parse(XMP_WITH_XXE.getBytes(StandardCharsets.UTF_8), null);
    }

    @Test
    public void xxeTestFromInputStream() throws XMPException, IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XMP_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            junitExpectedException.expect(XMPException.class);
            junitExpectedException.expectMessage(DTD_EXCEPTION_MESSAGE);
            XMPMetaParser.parse(inputStream, null);
        }
    }

    @Test
    public void xxeTestFromStringCustomXmlParser() throws XMPException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage("Test message");
        XMPMetaParser.parse(XMP_WITH_XXE, null);
    }
}
