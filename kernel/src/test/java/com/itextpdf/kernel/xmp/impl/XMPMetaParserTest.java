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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class XMPMetaParserTest extends ExtendedITextTest {

    private static final String XXE_FILE_PATH = "./src/test/resources/com/itextpdf/kernel/xmp/impl/xxe-data.txt";

    private static final String XMP_WITH_XXE = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n"
            + "<!DOCTYPE foo [ <!ENTITY xxe SYSTEM \"{0}\" > ]>\n"
            + "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n"
            + "    <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
            + "        <rdf:Description rdf:about=\"\" xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\">\n"
            + "            <pdfaid:part>&xxe;1</pdfaid:part>\n"
            + "            <pdfaid:conformance>B</pdfaid:conformance>\n"
            + "        </rdf:Description>\n"
            + "    </rdf:RDF>\n"
            + "</x:xmpmeta>\n"
            + "<?xpacket end=\"r\"?>";

    private static final String EXPECTED_SERIALIZED_XMP = "<?xpacket begin=\"\uFEFF\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n"
            + "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"Adobe XMP Core 5.1.0-jc003\">\n"
            + "  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
            + "    <rdf:Description rdf:about=\"\"\n"
            + "        xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\"\n"
            + "      pdfaid:part=\"1\"\n"
            + "      pdfaid:conformance=\"B\"/>\n"
            + "  </rdf:RDF>\n"
            + "</x:xmpmeta>\n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "                                                                                                    \n"
            + "             \n"
            + "<?xpacket end=\"w\"?>";

    @Test
    public void xxeTestFromString() throws XMPException {
        String metadataToParse = MessageFormatUtil.format(XMP_WITH_XXE, XXE_FILE_PATH);
        XMPMeta xmpMeta = XMPMetaParser.parse(metadataToParse, null);
        String serializedResult = XMPMetaFactory.serializeToString(xmpMeta, null);
        Assert.assertEquals(EXPECTED_SERIALIZED_XMP, serializedResult);
    }

    @Test
    public void xxeTestFromByteBuffer() throws XMPException {
        String metadataToParse = MessageFormatUtil.format(XMP_WITH_XXE, XXE_FILE_PATH);
        XMPMeta xmpMeta = XMPMetaParser.parse(metadataToParse.getBytes(StandardCharsets.UTF_8), null);
        String serializedResult = XMPMetaFactory.serializeToString(xmpMeta, null);
        Assert.assertEquals(EXPECTED_SERIALIZED_XMP, serializedResult);
    }

    @Test
    public void xxeTestFromInputStream() throws XMPException, IOException {
        String metadataToParse = MessageFormatUtil.format(XMP_WITH_XXE, XXE_FILE_PATH);
        try (InputStream inputStream = new ByteArrayInputStream(metadataToParse.getBytes(StandardCharsets.UTF_8))) {
            XMPMeta xmpMeta = XMPMetaParser.parse(inputStream, null);
            String serializedResult = XMPMetaFactory.serializeToString(xmpMeta, null);
            Assert.assertEquals(EXPECTED_SERIALIZED_XMP, serializedResult);
        }
    }
}
