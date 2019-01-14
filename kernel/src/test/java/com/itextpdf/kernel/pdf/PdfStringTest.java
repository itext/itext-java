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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Category(IntegrationTest.class)
public class PdfStringTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfStringTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfStringTest/";

    @Before
    public void before() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void testPdfDocumentInfoStringEncoding01() throws IOException, InterruptedException {
        String fileName = "testPdfDocumentInfoStringEncoding01.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + fileName, new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
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

        PdfDocument readDoc = new PdfDocument(new PdfReader(destinationFolder + fileName));
        Assert.assertEquals(author, readDoc.getDocumentInfo().getAuthor());
        Assert.assertEquals(title, readDoc.getDocumentInfo().getTitle());
        Assert.assertEquals(subject, readDoc.getDocumentInfo().getSubject());
        Assert.assertEquals(keywords, readDoc.getDocumentInfo().getKeywords());
        Assert.assertEquals(creator, readDoc.getDocumentInfo().getCreator());

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, destinationFolder, "diff_"));
    }

    @Test
    public void testUnicodeString() throws IOException, InterruptedException {
        String unicode = "Привет!";
        PdfString string = new PdfString(unicode);
        Assert.assertNotEquals(unicode, string.toUnicodeString());
    }

    @Test
    public void readUtf8ActualText() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-actual-text.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        String text = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1), new LocationTextExtractionStrategy().setUseActualText(true));
        pdfDoc.close();
        //  शांति देवनागरी
        Assert.assertEquals("\u0936\u093e\u0902\u0924\u093f \u0926\u0947\u0935\u0928\u093E\u0917\u0930\u0940", text);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.EXISTING_TAG_STRUCTURE_ROOT_IS_NOT_STANDARD)
    })
    public void readUtf8AltText() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-alt-text.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename), new PdfWriter(destinationFolder + "whatever"));
        TagTreePointer tagTreePointer = new TagTreePointer(pdfDoc);
        String alternateDescription = tagTreePointer.moveToKid(0).moveToKid(0).moveToKid(0).getProperties().getAlternateDescription();
        pdfDoc.close();
        //  2001: A Space Odyssey (Космическая одиссея)
        Assert.assertEquals("2001: A Space Odyssey (\u041A\u043E\u0441\u043C\u0438\u0447\u0435\u0441\u043A\u0430\u044F " +
                "\u043E\u0434\u0438\u0441\u0441\u0435\u044F)", alternateDescription);
    }

    @Test
    public void readUtf8Bookmarks() throws java.io.IOException, InterruptedException {
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
            Assert.assertEquals(expected.get(i), children.get(i));
    }

    @Test
    public void readUtf8PageLabelPrefix() throws java.io.IOException, InterruptedException {
        String filename = sourceFolder + "utf-8-page-label-prefix.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        String[] labels = pdfDoc.getPageLabels();
        String[] expected = new String[] {"A", "B", "1", "2", "3", "4", "Movies-5", "Movies-6", "Movies-7", "Movies-8",
                "Movies-9", "Movies-10", "Movies-11", "Movies-12"};
        pdfDoc.close();
        for (int i = 0; i < labels.length; i++)
            Assert.assertEquals(expected[i], labels[i]);
    }

    @Test
    public void writeUtf8AltText() throws java.io.IOException, InterruptedException {
        String RESOURCE = sourceFolder + "Space Odyssey.jpg";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8AltText.pdf"));
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
        canvas.addImage(img, 36, 700, 100, false, false);
        canvas.closeTag();
        canvas.endText();
        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8AltText.pdf", sourceFolder + "cmp_writeUtf8AltText.pdf", destinationFolder, "diffAltText_"));
    }

    @Test
    public void writeUtf8Bookmarks() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8Bookmarks.pdf"));

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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8Bookmarks.pdf", sourceFolder + "cmp_writeUtf8Bookmarks.pdf", destinationFolder, "diffBookmarks_"));
    }

    @Test
    public void writeUtf8PageLabelPrefix() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8PageLabelPrefix.pdf"));

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
        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8PageLabelPrefix.pdf", sourceFolder + "cmp_writeUtf8PageLabelPrefix.pdf", destinationFolder, "diffPageLabelPrefix_"));
    }

    @Test
    public void writeUtf8ActualText() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8ActualText.pdf"));
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

        Assert.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8ActualText.pdf", sourceFolder + "cmp_writeUtf8ActualText.pdf", destinationFolder, "diffActualText_"));
    }
}
