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
package com.itextpdf.forms.xfdf;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.DefaultSafeXmlParserFactory;
import com.itextpdf.kernel.utils.XmlProcessorCreator;
import com.itextpdf.test.ExceptionTestUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
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

    @Test
    public void xxeVulnerabilityXfdfTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(null);
        try (InputStream inputStream = new ByteArrayInputStream(XFDF_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> XfdfFileUtils.createXfdfDocumentFromStream(inputStream)
            );
            Assertions.assertEquals(ExceptionTestUtil.getDoctypeIsDisallowedExceptionMessage(), e.getMessage());
        }
    }

    @Test
    public void xxeVulnerabilityXfdfCustomXmlParserTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(new SecurityTestXmlParserFactory());
        try (InputStream inputStream = new ByteArrayInputStream(XFDF_WITH_XXE.getBytes(StandardCharsets.UTF_8))) {
            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> XfdfFileUtils.createXfdfDocumentFromStream(inputStream)
            );
            Assertions.assertEquals("Test message", e.getMessage());
        }
    }

    @Test
    public void customXmlParserCreateNewXfdfDocumentExceptionTest()
            throws IOException {
        XmlProcessorCreator.setXmlParserFactory(new ExceptionTestXmlParserFactory());
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> XfdfFileUtils.createNewXfdfDocument()
        );
        Assertions.assertEquals(ExceptionTestUtil.getXxeTestMessage(), e.getMessage());
    }
}
