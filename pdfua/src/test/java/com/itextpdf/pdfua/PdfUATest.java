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
package com.itextpdf.pdfua;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUATest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUATest/";

    private static final String DOG = "./src/test/resources/com/itextpdf/pdfua/img/DOG.bmp";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final String FOX = "./src/test/resources/com/itextpdf/pdfua/img/FOX.bmp";

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkPoint01_007_suspectsHasEntryTrue() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
        Assert.assertNotNull(markInfo);
        markInfo.put(PdfName.Suspects, new PdfBoolean(true));
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.SUSPECTS_ENTRY_IN_MARK_INFO_DICTIONARY_SHALL_NOT_HAVE_A_VALUE_OF_TRUE,
                e.getMessage());
    }


    @Test
    public void checkPoint01_007_suspectsHasEntryFalse() {
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        PdfDictionary markInfo = (PdfDictionary) pdfDoc.getCatalog().getPdfObject().get(PdfName.MarkInfo);
        markInfo.put(PdfName.Suspects, new PdfBoolean(false));
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }

    @Test
    public void checkPoint01_007_suspectsHasNoEntry() {
        // suspects entry is optional so it is ok to not have it according to the spec
        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), PdfUATestPdfDocument.createWriterProperties()));
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }


    @Test
    public void emptyPageDocument() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "emptyPageDocument.pdf";
        try (PdfDocument pdfDocument = new PdfUATestPdfDocument(
                new PdfWriter(outPdf, PdfUATestPdfDocument.createWriterProperties()))) {
            pdfDocument.addNewPage();
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_emptyPageDocument.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    public void manualPdfUaCreation() throws IOException, InterruptedException {

        final String outPdf = DESTINATION_FOLDER + "manualPdfUaCreation.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf,
                new WriterProperties().addUAXmpMetadata().setPdfVersion(PdfVersion.PDF_1_7)));
        Document document = new Document(pdfDoc, PageSize.A4.rotate());

        //TAGGED PDF
        //Make document tagged
        pdfDoc.setTagged();

        //PDF/UA
        //Set document metadata
        pdfDoc.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDoc.getDocumentInfo();
        info.setTitle("English pangram");

        Paragraph p = new Paragraph();

        //PDF/UA
        //Embed font
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.PREFER_EMBEDDED);
        p.setFont(font);

        p.add("The quick brown ");

        Image img = new Image(ImageDataFactory.create(FOX));

        //PDF/UA
        //Set alt text
        img.getAccessibilityProperties().setAlternateDescription("Fox");
        p.add(img);
        p.add(" jumps over the lazy ");

        img = new Image(ImageDataFactory.create(DOG));

        //PDF/UA
        //Set alt text
        img.getAccessibilityProperties().setAlternateDescription("Dog");
        p.add(img);

        document.add(p);

        p = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n").setFont(font).setFontSize(20);
        document.add(p);

        List list = new List().setFont(font).setFontSize(20);
        list.add(new ListItem("quick"));
        list.add(new ListItem("brown"));
        list.add(new ListItem("fox"));
        list.add(new ListItem("jumps"));
        list.add(new ListItem("over"));
        list.add(new ListItem("the"));
        list.add(new ListItem("lazy"));
        list.add(new ListItem("dog"));
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, SOURCE_FOLDER + "cmp_manualPdfUaCreation.pdf",
                DESTINATION_FOLDER, "diff_"));
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }
}
