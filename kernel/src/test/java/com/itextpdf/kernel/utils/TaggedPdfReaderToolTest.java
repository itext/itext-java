/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class TaggedPdfReaderToolTest extends ExtendedITextTest{
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/TaggedPdfReaderToolTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/TaggedPdfReaderToolTest/";

    @Before
    public void setUp() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void taggedPdfReaderToolTest01() throws IOException, ParserConfigurationException, SAXException {
        String filename = "iphone_user_guide.pdf";

        String outXmlPath = destinationFolder + "outXml01.xml";
        String cmpXmlPath = sourceFolder + "cmpXml01.xml";

        PdfReader reader = new PdfReader(sourceFolder + filename);

        try (FileOutputStream outXml = new FileOutputStream(outXmlPath);
             PdfDocument document = new PdfDocument(reader)) {

            TaggedPdfReaderTool tool = new TaggedPdfReaderTool(document);
            tool.setRootTag("root");
            tool.convertToXml(outXml);
        }

        CompareTool compareTool = new CompareTool();
        if (!compareTool.compareXmls(outXmlPath, cmpXmlPath)) {
            Assert.fail("Resultant xml is different.");
        }
    }
}
