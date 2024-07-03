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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfAAppendModeTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String testDirName = "PdfAAppendModeTest/";
    public static final String cmpFolder = sourceFolder + "cmp/" + testDirName;
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/" + testDirName;

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void addPageInAppendModeTest() throws IOException, InterruptedException {
        String inputFile = destinationFolder + "in_addPageInAppendModeTest.pdf";
        String outputFile = destinationFolder + "out_addPageInAppendModeTest.pdf";
        String cmpFile = cmpFolder + "cmp_addPageInAppendModeTest.pdf";
        createInputPdfADocument(inputFile);
        PdfDocument pdfADocument = new PdfADocument(new PdfReader(inputFile), new PdfWriter(outputFile),
                new StampingProperties().useAppendMode());
        PdfCanvas canvas = new PdfCanvas(pdfADocument.addNewPage());
        canvas.saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(
                        sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED), 16)
                .showText("This page 2")
                .endText()
                .restoreState();
        canvas.release();
        pdfADocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(inputFile)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(new VeraPdfValidator().validate(outputFile)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(new CompareTool().compareByContent(outputFile, cmpFile, destinationFolder, "diff_"));
    }

    private static void createInputPdfADocument(String docName) throws IOException {
        PdfWriter writer = new PdfWriter(docName);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        canvas.saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(
                        sourceFolder + "FreeSans.ttf", EmbeddingStrategy.PREFER_EMBEDDED), 16)
                .showText("This page 1")
                .endText()
                .restoreState();
        canvas.release();
        pdfDoc.close();
    }
}
