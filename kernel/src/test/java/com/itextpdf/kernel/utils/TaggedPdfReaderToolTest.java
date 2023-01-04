/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
