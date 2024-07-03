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
package com.itextpdf.kernel.xmp.impl;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ExceptionTestUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
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

    @BeforeEach
    public void resetXmlParserFactoryToDefault() {
        XmlProcessorCreator.setXmlParserFactory(null);
    }

    @Test
    public void xxeTestFromString() throws XMPException {
        Exception e = Assertions.assertThrows(XMPException.class, () -> XMPMetaParser.parse(XMP_WITH_XXE, null));
        Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
    }

    @Test
    public void xxeTestFromByteBuffer() throws XMPException {
        Exception e = Assertions.assertThrows(XMPException.class,
                () -> XMPMetaParser.parse(XMP_WITH_XXE.getBytes(StandardCharsets.UTF_8), null)
        );
        Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
    }

    @Test
    public void xxeTestFromInputStream() throws XMPException, IOException {
        try (InputStream inputStream = new ByteArrayInputStream(XMP_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(XMPException.class,
                    () -> XMPMetaParser.parse(inputStream, null)
            );
            Assertions.assertEquals(DTD_EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void xxeTestFromStringCustomXmlParser() throws XMPException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> XMPMetaParser.parse(XMP_WITH_XXE, null)
        );
        Assertions.assertEquals("Test message", e.getMessage());
    }
}
