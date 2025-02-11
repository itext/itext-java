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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("IntegrationTest")
public class ReorderPagesTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/ReorderPagesTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/ReorderPagesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void reorderTaggedHasCommonStructElem01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElem01.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElem.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), CompareTool.createTestPdfWriter(outPath));
        pdf.setTagged();

        pdf.movePage(2,1);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_01");
    }

    @Test
    public void reorderTaggedHasCommonStructElem02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElem02.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElem.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), CompareTool.createTestPdfWriter(outPath));
        pdf.movePage(1,3);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_02");
    }

    @Test
    public void reorderTaggedHasCommonStructElemBigger() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElemBigger.pdf";
        String outPath = destinationFolder + "reorderTaggedHasCommonStructElemBigger.pdf";
        String cmpPath = sourceFolder + "cmp_reorderTaggedHasCommonStructElemBigger.pdf";

        PdfDocument pdf = new PdfDocument(new PdfReader(inPath), CompareTool.createTestPdfWriter(outPath));
        pdf.movePage(2,5);
        pdf.close();

        compare(outPath, cmpPath, destinationFolder, "diff_03");
    }

    @Test
    public void copyReorderTaggedHasCommonStructElem() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String inPath = sourceFolder + "taggedHasCommonStructElem.pdf";
        String outPath = destinationFolder + "copyReorderTaggedHasCommonStructElem.pdf";
        String cmpPath = sourceFolder + "cmp_copyReorderTaggedHasCommonStructElem.pdf";

        PdfDocument sourceDoc = new PdfDocument(new PdfReader(inPath));
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPath));
        pdfDoc.setTagged();

        sourceDoc.copyPagesTo(Arrays.asList(2, 1, 3), pdfDoc);

        sourceDoc.close();
        pdfDoc.close();

        compare(outPath, cmpPath, destinationFolder, "diff_04");
    }

    private void compare(String outPath, String cmpPath, String destinationFolder, String diffPrefix) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String tagStructureErrors = compareTool.compareTagStructures(outPath, cmpPath);
        String contentErrors = compareTool.compareByContent(outPath, cmpPath, destinationFolder, diffPrefix);
        String resultMessage = "";
        if (tagStructureErrors != null) {
            resultMessage += tagStructureErrors + "\n";
        }
        if (contentErrors != null) {
            resultMessage += contentErrors + "\n";
        }
        assertTrue(tagStructureErrors == null && contentErrors == null, resultMessage);
    }

}
