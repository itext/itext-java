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

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class TaggedPdfReaderToolTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/utils/TaggedPdfReaderToolTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/utils/TaggedPdfReaderToolTest/";

    @Before
    public void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void taggedPdfReaderToolTest01() throws IOException, ParserConfigurationException, SAXException {
        String filename = "iphone_user_guide.pdf";

        String outXmlPath = DESTINATION_FOLDER + "outXml01.xml";
        String cmpXmlPath = SOURCE_FOLDER + "cmpXml01.xml";

        PdfReader reader = new PdfReader(SOURCE_FOLDER + filename);

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

    @Test
    public void noStructTreeRootInDocTest() {
        String outXmlPath = DESTINATION_FOLDER + "noStructTreeRootInDoc.xml";

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
            TaggedPdfReaderTool tool = new TaggedPdfReaderTool(pdfDocument);
            try (FileOutputStream outXml = new FileOutputStream(outXmlPath)) {
                Exception exception = Assert.assertThrows(PdfException.class,
                        () -> tool.convertToXml(outXml, "UTF-8"));
                Assert.assertEquals(KernelExceptionMessageConstant.DOCUMENT_DOES_NOT_CONTAIN_STRUCT_TREE_ROOT,
                        exception.getMessage());
            }
        } catch (IOException e) {
            Assert.fail("IOException is not expected to be triggered");
        }
    }
}
