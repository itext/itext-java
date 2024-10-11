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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Tag("IntegrationTest")
public class PdfStringTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStringTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStringTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void testPdfDocumentInfoStringEncoding01() throws IOException, InterruptedException {
        String fileName = "testPdfDocumentInfoStringEncoding01.pdf";

        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName, new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.addNewPage();

        String author = "Алексей";
        String title = "Заголовок";
        String subject = "Тема";
        String keywords = "Ключевые слова";
        String creator = "English text";

        pdfDocument.getDocumentInfo().setAuthor(author);
        pdfDocument.getDocumentInfo().setTitle(title);
        pdfDocument.getDocumentInfo().setSubject(subject);
        pdfDocument.getDocumentInfo().setKeywords(keywords);
        pdfDocument.getDocumentInfo().setCreator(creator);

        pdfDocument.close();

        PdfDocument readDoc = new PdfDocument(CompareTool.createOutputReader(destinationFolder + fileName));
        Assertions.assertEquals(author, readDoc.getDocumentInfo().getAuthor());
        Assertions.assertEquals(title, readDoc.getDocumentInfo().getTitle());
        Assertions.assertEquals(subject, readDoc.getDocumentInfo().getSubject());
        Assertions.assertEquals(keywords, readDoc.getDocumentInfo().getKeywords());
        Assertions.assertEquals(creator, readDoc.getDocumentInfo().getCreator());

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void testUnicodeString() {
        String unicode = "Привет!";
        PdfString string = new PdfString(unicode);
        Assertions.assertNotEquals(unicode, string.toUnicodeString());
    }

    @Test
    public void readUtf8ActualText() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-actual-text.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        String text = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1), new LocationTextExtractionStrategy().setUseActualText(true));
        pdfDoc.close();
        //  शांति देवनागरी
        Assertions.assertEquals("\u0936\u093e\u0902\u0924\u093f \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940", text);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.EXISTING_TAG_STRUCTURE_ROOT_IS_NOT_STANDARD)
    })
    public void readUtf8AltText() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-alt-text.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename), CompareTool.createTestPdfWriter(destinationFolder + "whatever"));
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        String alternateDescription = tagTreePointer.moveToKid(0).moveToKid(0).moveToKid(0).getProperties().getAlternateDescription();
        pdfDoc.close();
        //  2001: A Space Odyssey (Космическая одиссея)
        Assertions.assertEquals("2001: A Space Odyssey (\u041A\u043E\u0441\u043C\u0438\u0447\u0435\u0441\u043A\u0430\u044F " +
                "\u043E\u0434\u0438\u0441\u0441\u0435\u044F)", alternateDescription);
    }

    @Test
    public void readUtf8Bookmarks() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-bookmarks.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfOutline outline = pdfDoc.getOutlines(true);
        List<String> children = new ArrayList<>(6);
        for (PdfOutline child : outline.getAllChildren()) {
            children.add(child.getTitle());
            for (PdfOutline childOfChild : child.getAllChildren())
                children.add(childOfChild.getTitle());
        }
        pdfDoc.close();
        List<String> expected = new ArrayList<>(6);
        //  福昕
        expected.add("\u798F\u6615 bookmark 1");
        expected.add("\u798F\u6615  bookmark 1-1");
        expected.add("\u798F\u6615  bookmark 1-2");
        //  中国
        expected.add("\u4E2D\u56FD bookmark 2");
        expected.add("\u4E2D\u56FD  bookmark 2-1");
        expected.add("\u4E2D\u56FD  bookmark 2-2");
        for (int i = 0; i < 6; i++)
            Assertions.assertEquals(expected.get(i), children.get(i));
    }

    @Test
    public void readUtf8PageLabelPrefix() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-page-label-prefix.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        String[] labels = pdfDoc.getPageLabels();
        String[] expected = new String[] {"A", "B", "1", "2", "3", "4", "Movies-5", "Movies-6", "Movies-7", "Movies-8",
                "Movies-9", "Movies-10", "Movies-11", "Movies-12"};
        pdfDoc.close();
        for (int i = 0; i < labels.length; i++)
            Assertions.assertEquals(expected[i], labels[i]);
    }

    @Test
    public void writeUtf8AltText() throws java.io.IOException, InterruptedException {
        String RESOURCE = sourceFolder + "Space Odyssey.jpg";
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "writeUtf8AltText.pdf"));
        pdfDoc.setTagged();

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
        tagPointer.setPageForTagging(page);
        tagPointer.addTag(StandardRoles.DIV);

        tagPointer.addTag(StandardRoles.SPAN);
        //  2001: A Space Odyssey (Космическая одиссея)
        tagPointer.getContext().getPointerStructElem(tagPointer)
                .setAlt(new PdfString("2001: A Space Odyssey (\u041A\u043E\u0441\u043C\u0438\u0447\u0435\u0441\u043A\u0430\u044F " +
                        "\u043E\u0434\u0438\u0441\u0441\u0435\u044F)", PdfEncodings.UTF8));
        ImageData img = ImageDataFactory.create(RESOURCE);
        canvas.openTag(tagPointer.getTagReference());
        canvas.addImageFittedIntoRectangle(img, new Rectangle(36, 700, 65, 100), false);
        canvas.closeTag();
        canvas.endText();
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8AltText.pdf", sourceFolder + "cmp_writeUtf8AltText.pdf", destinationFolder, "diffAltText_"));
    }

    @Test
    public void writeUtf8Bookmarks() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "writeUtf8Bookmarks.pdf"));

        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.MAGENTA);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 30);
        canvas.setTextMatrix(25, 500);
        canvas.showText("This file has bookmarks encoded with utf-8");
        canvas.endText();

        PdfOutline root = pdfDoc.getOutlines(false);

        PdfOutline first = root.addOutline("");
        //  福昕
        first.getContent().put(PdfName.Title, new PdfString("\u798F\u6615 bookmark 1", PdfEncodings.UTF8));
        first.addOutline("").getContent().put(PdfName.Title, new PdfString("\u798F\u6615  bookmark 1-1", PdfEncodings.UTF8));
        first.addOutline("").getContent().put(PdfName.Title, new PdfString("\u798F\u6615  bookmark 1-2", PdfEncodings.UTF8));

        PdfOutline second = root.addOutline("");
        //  中国
        second.getContent().put(PdfName.Title, new PdfString("\u4E2D\u56FD bookmark 2", PdfEncodings.UTF8));
        second.addOutline("").getContent().put(PdfName.Title, new PdfString("\u4E2D\u56FD  bookmark 2-1", PdfEncodings.UTF8));
        second.addOutline("").getContent().put(PdfName.Title, new PdfString("\u4E2D\u56FD  bookmark 2-2", PdfEncodings.UTF8));

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8Bookmarks.pdf", sourceFolder + "cmp_writeUtf8Bookmarks.pdf", destinationFolder, "diffBookmarks_"));
    }

    @Test
    public void writeUtf8PageLabelPrefix() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "writeUtf8PageLabelPrefix.pdf"));

        PdfPage page = pdfDoc.addNewPage();;
        PdfDictionary pageLabel = new PdfDictionary();
        pageLabel.put(PdfName.S, PdfName.D);
        pageLabel.put(PdfName.P, new PdfString("PREFIX-", PdfEncodings.UTF8));
        pageLabel.put(PdfName.St, new PdfNumber(1));
        pdfDoc.getCatalog().getPageLabelsTree(true).addEntry(pdfDoc.getPageNumber(page) - 1, pageLabel);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.MAGENTA);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 30);
        canvas.setTextMatrix(25, 500);
        String text = "This page has pageLabel prefix " + "PREFIX-";
        canvas.showText(text);
        canvas.endText();

        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8PageLabelPrefix.pdf", sourceFolder + "cmp_writeUtf8PageLabelPrefix.pdf", destinationFolder, "diffPageLabelPrefix_"));
    }

    @Test
    public void writeUtf8ActualText() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "writeUtf8ActualText.pdf"));
        pdfDoc.setTagged();
        PdfPage page = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        TagTreePointer tagPointer = new TagTreePointer(pdfDoc);
        tagPointer.setPageForTagging(page);
        tagPointer.addTag(StandardRoles.DIV);
        tagPointer.addTag(StandardRoles.SPAN);
        tagPointer.getContext().getPointerStructElem(tagPointer).setActualText(new PdfString("actual", PdfEncodings.UTF8));
        canvas.beginText();
        canvas.moveText(36, 788);
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN), 12);
        canvas.openTag(tagPointer.getTagReference());
        canvas.showText("These piece of text has an actual text property. Can be viewed via properties of span in the tag tree.");
        canvas.closeTag();
        canvas.endText();
        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8ActualText.pdf", sourceFolder + "cmp_writeUtf8ActualText.pdf", destinationFolder, "diffActualText_"));
    }
}
