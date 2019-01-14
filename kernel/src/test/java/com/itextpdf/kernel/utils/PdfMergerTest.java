/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class PdfMergerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/PdfMergerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/PdfMergerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void mergeDocumentTest01() throws IOException, InterruptedException {
        String filename = sourceFolder + "courierTest.pdf";
        String filename1 = sourceFolder + "helveticaTest.pdf";
        String filename2 = sourceFolder + "timesRomanTest.pdf";
        String resultFile = destinationFolder + "mergedResult01.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        merger.merge(pdfDoc, 1, 1);
        merger.merge(pdfDoc1, 1, 1);

        merger.merge(pdfDoc2, 1, 1);

        pdfDoc3.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentOutlinesWithNullDestinationTest01() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "mergeDocumentOutlinesWithNullDestinationTest01.pdf";
        String filename = sourceFolder + "null_dest_outline.pdf";
        PdfDocument sourceDocument = new PdfDocument(new PdfReader(filename));

        PdfMerger resultDocument = new PdfMerger(new PdfDocument(new PdfWriter(resultFile)));
        resultDocument.merge(sourceDocument, 1, 1);
        resultDocument.close();
        sourceDocument.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergeDocumentOutlinesWithNullDestinationTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void mergeDocumentTest02() throws IOException, InterruptedException {
        String filename = sourceFolder + "doc1.pdf";
        String filename1 = sourceFolder + "doc2.pdf";
        String filename2 = sourceFolder + "doc3.pdf";
        String resultFile = destinationFolder + "mergedResult02.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);
        PdfReader reader2 = new PdfReader(filename2);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc2 = new PdfDocument(reader2);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);

        merger.merge(pdfDoc, 1, 1).merge(pdfDoc1, 1, 1).merge(pdfDoc2, 1, 1).close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)
    })
    public void mergeDocumentTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult03.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        new PdfMerger(pdfDoc3)
                .merge(pdfDoc, 2, 2)
                .merge(pdfDoc1, 7, 8)
                .close();

        pdfDoc.close();
        pdfDoc1.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult03.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult03.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY),
            @LogMessage(messageTemplate = LogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING, count = 2)
    })
    public void mergeDocumentTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "pdf_open_parameters.pdf";
        String filename1 = sourceFolder + "iphone_user_guide.pdf";
        String resultFile = destinationFolder + "mergedResult04.pdf";

        PdfReader reader = new PdfReader(filename);
        PdfReader reader1 = new PdfReader(filename1);

        FileOutputStream fos1 = new FileOutputStream(resultFile);
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfDocument pdfDoc1 = new PdfDocument(reader1);
        PdfDocument pdfDoc3 = new PdfDocument(writer1);
        pdfDoc3.setTagged();

        PdfMerger merger = new PdfMerger(pdfDoc3).setCloseSourceDocuments(true);
        List<Integer> pages = new ArrayList<>();
        pages.add(3);
        pages.add(2);
        pages.add(1);
        merger.merge(pdfDoc, pages);

        List<Integer> pages1 = new ArrayList<>();
        pages1.add(5);
        pages1.add(9);
        pages1.add(4);
        pages1.add(3);
        merger.merge(pdfDoc1, pages1);

        merger.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = "";
        String contentErrorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergedResult04.pdf", destinationFolder, "diff_");
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_mergedResult04.pdf");

        errorMessage += tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        errorMessage += contentErrorMessage == null ? "" : contentErrorMessage;
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void mergeTableWithEmptyTdTest() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String filename = sourceFolder + "tableWithEmptyTd.pdf";
        String resultFile = destinationFolder + "tableWithEmptyTdResult.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(new PdfWriter(resultFile));
        output.setTagged();
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(true);
        merger.merge(sourceDoc, 1, sourceDoc.getNumberOfPages());
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        CompareTool compareTool = new CompareTool();
        String tagStructErrorMessage = compareTool.compareTagStructures(resultFile, sourceFolder + "cmp_tableWithEmptyTd.pdf");

        String errorMessage = tagStructErrorMessage == null ? "" : tagStructErrorMessage + "\n";
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 2)})
    public void mergeOutlinesNamedDestinations() throws IOException, InterruptedException {
        String filename = sourceFolder + "outlinesNamedDestinations.pdf";
        String resultFile = destinationFolder + "mergeOutlinesNamedDestinations.pdf";

        PdfReader reader = new PdfReader(filename);

        PdfDocument sourceDoc = new PdfDocument(reader);
        PdfDocument output = new PdfDocument(new PdfWriter(resultFile));
        PdfMerger merger = new PdfMerger(output).setCloseSourceDocuments(false);
        merger.merge(sourceDoc, 2, 3);
        merger.merge(sourceDoc, 2, 3);
        sourceDoc.close();
        reader.close();
        merger.close();
        output.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(resultFile, sourceFolder + "cmp_mergeOutlinesNamedDestinations.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
