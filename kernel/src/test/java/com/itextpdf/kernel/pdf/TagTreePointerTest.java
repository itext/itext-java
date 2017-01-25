/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.IPdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class TagTreePointerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/TagTreePointerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/TagTreePointerTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void tagTreePointerTest01() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "tagTreePointerTest01.pdf");
        PdfWriter writer = new PdfWriter(fos).setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page1 = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.P).addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();
        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        tagPointer.moveToParent().moveToParent();

        canvas.endText()
              .release();

        PdfPage page2 = document.addNewPage();
        tagPointer.setPageForTagging(page2);
        canvas = new PdfCanvas(page2);

        canvas.beginText()
              .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.P).addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
        page1.flush();
        page2.flush();

        document.close();

        compareResult("tagTreePointerTest01.pdf", "cmp_tagTreePointerTest01.pdf", "diff01_");
    }

    @Test
    public void tagTreePointerTest02() throws Exception {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "tagTreePointerTest02.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page);

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, new PdfString("random attributes"));
        attributes.put(new PdfName("hello"), new PdfString("world"));

        tagPointer.addTag(PdfName.P).addTag(PdfName.Span).getProperties()
                .setActualText("Actual text for span is: Hello World")
                .setLanguage("en-GB")
                .addAttributes(attributes);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
        page.flush();

        document.close();

        compareResult("tagTreePointerTest02.pdf", "cmp_tagTreePointerTest02.pdf", "diff02_");
    }

    @Test
    public void tagTreePointerTest03() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR);
        TagTreePointer tagPointerCopy = new TagTreePointer(tagPointer);
        tagPointer.removeTag();

        // tagPointerCopy now points at removed tag

        String exceptionMessage = null;
        try {
            tagPointerCopy.addTag(PdfName.Span);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        assertEquals(PdfException.TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot, exceptionMessage);

        tagPointerCopy.moveToRoot().moveToKid(PdfName.Table);

        tagPointerCopy.moveToKid(PdfName.TR);
        TagTreePointer tagPointerCopyCopy = new TagTreePointer(tagPointerCopy);
        tagPointerCopy.flushTag();

        // tagPointerCopyCopy now points at flushed tag

        try {
            tagPointerCopyCopy.addTag(PdfName.Span);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        assertEquals(PdfException.TagTreePointerIsInInvalidStateItPointsAtFlushedElementUseMoveToRoot, exceptionMessage);

        try {
            tagPointerCopy.moveToKid(0);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        assertEquals(PdfException.CannotMoveToFlushedKid, exceptionMessage);

        document.close();
    }

    @Test
    public void tagTreePointerTest04() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest04.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR);
        tagPointer.removeTag();

        tagPointer.moveToKid(PdfName.TR).moveToKid(PdfName.TD)
                .moveToKid(PdfName.P).moveToKid(PdfName.Span);
        tagPointer.removeTag()
                .removeTag();

        document.close();

        compareResult("tagTreePointerTest04.pdf", "cmp_tagTreePointerTest04.pdf", "diff04_");
    }

    @Test
    public void tagTreePointerTest05() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest05.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer1 = new TagTreePointer(document);
        tagPointer1.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR);

        TagTreePointer tagPointer2 = new TagTreePointer(document);
        tagPointer2.moveToKid(PdfName.Table).moveToKid(0, PdfName.TR);
        tagPointer1.relocateKid(0, tagPointer2);

        tagPointer1.moveToParent().moveToKid(5, PdfName.TR).moveToKid(2, PdfName.TD).moveToKid(PdfName.P).moveToKid(PdfName.Span);
        tagPointer2.moveToKid(PdfName.TD).moveToKid(PdfName.P).moveToKid(PdfName.Span);
        tagPointer2.setNextNewKidIndex(3);
        tagPointer1.relocateKid(4, tagPointer2);

        document.close();

        compareResult("tagTreePointerTest05.pdf", "cmp_tagTreePointerTest05.pdf", "diff05_");
    }

    @Test
    public void tagTreePointerTest06() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setRole(PdfName.Part);
        assertEquals(tagPointer.getRole().getValue(), "Part");
        tagPointer.moveToKid(PdfName.Table).getProperties().setLanguage("en-US");
        tagPointer.moveToKid(PdfName.TR).moveToKid(PdfName.TD).moveToKid(PdfName.P);
        String actualText1 = "Some looong latin text";
        tagPointer.getProperties().setActualText(actualText1);

        assertNull(tagPointer.getConnectedElement(false));
        IAccessibleElement connectedElement = tagPointer.getConnectedElement(true);

        tagPointer.moveToRoot().moveToKid(PdfName.Table).moveToKid(1, PdfName.TR).getProperties().setActualText("More latin text");
        connectedElement.setRole(PdfName.Div);
        connectedElement.getAccessibilityProperties().setLanguage("en-Us");
        assertEquals(connectedElement.getAccessibilityProperties().getActualText(), actualText1);

        document.close();

        compareResult("tagTreePointerTest06.pdf", "cmp_tagTreePointerTest06.pdf", "diff06_");
    }

    @Test
    public void tagStructureFlushingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        tagPointer.moveToKid(3, PdfName.TR).moveToKid(PdfName.TD).flushTag();
        tagPointer.moveToParent().flushTag();

        String exceptionMessage = null;
        try {
            tagPointer.flushTag();
        } catch(PdfException e) {
            exceptionMessage = e.getMessage();
        }

        document.close();

        assertEquals(PdfException.CannotFlushDocumentRootTagBeforeDocumentIsClosed, exceptionMessage);
        compareResult("tagStructureFlushingTest01.pdf", "taggedDocument.pdf", "diffFlushing01_");
    }

    @Test
    public void tagStructureFlushingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest02.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagStructureContext tagStructure = document.getTagStructureContext();
        tagStructure.flushPageTags(document.getPage(1));

        List<IPdfStructElem> kids = document.getStructTreeRoot().getKids();
        assertTrue(!((PdfStructElem)kids.get(0)).getPdfObject().isFlushed());
        assertTrue(!((PdfStructElem)kids.get(0).getKids().get(0)).getPdfObject().isFlushed());
        PdfArray rowsTags = (PdfArray) ((PdfStructElem) kids.get(0).getKids().get(0)).getK();
        assertTrue(rowsTags.get(0).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest02.pdf", "taggedDocument.pdf", "diffFlushing02_");
    }

    @Test
    public void tagStructureFlushingTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        document.getPage(2).flush();
        document.getPage(1).flush();

        PdfArray kids = document.getStructTreeRoot().getKidsObject();
        assertFalse(kids.get(0).isFlushed());
        assertTrue(kids.getAsDictionary(0).getAsDictionary(PdfName.K).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest03.pdf", "taggedDocument.pdf", "diffFlushing03_");
    }

    @Test
    public void tagStructureFlushingTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest04.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(PdfName.Table).moveToKid(2, PdfName.TR).flushTag();
        // intended redundant call to flush page tags separately from page. Page tags are flushed when the page is flushed.
        document.getTagStructureContext().flushPageTags(document.getPage(1));
        document.getPage(1).flush();
        tagPointer.moveToKid(5).flushTag();
        document.getPage(2).flush();

        PdfArray kids = document.getStructTreeRoot().getKidsObject();
        assertFalse(kids.get(0).isFlushed());
        assertTrue(kids.getAsDictionary(0).getAsDictionary(PdfName.K).isFlushed());

        document.close();

        compareResult("tagStructureFlushingTest04.pdf", "taggedDocument.pdf", "diffFlushing04_");
    }

    @Test
    public void tagStructureFlushingTest05() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest05.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page1 = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagPointer.addTag(PdfName.Div);

        tagPointer.addTag(PdfName.P);
        IAccessibleElement paragraphElement = tagPointer.getConnectedElement(true);
        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        tagPointer.moveToParent().moveToParent();

        // Flushing /Div tag and it's children. /P tag shall not be flushed, as it is has connected paragraphElement
        // object. On removing connection between paragraphElement and /P tag, /P tag shall be flushed.
        // When tag is flushed, tagPointer begins to point to tag's parent. If parent is also flushed - to the root.
        tagPointer.flushTag();

        tagPointer.moveToTag(paragraphElement);
        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("again")
              .closeTag();

        tagPointer.removeElementConnectionToTag(paragraphElement);
        tagPointer.moveToRoot();

        canvas.endText()
              .release();

        PdfPage page2 = document.addNewPage();
        tagPointer.setPageForTagging(page2);
        canvas = new PdfCanvas(page2);

        tagPointer.addTag(PdfName.P);
        canvas.beginText()
              .setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);
        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();
        page1.flush();
        page2.flush();

        document.close();

        compareResult("tagStructureFlushingTest05.pdf", "cmp_tagStructureFlushingTest05.pdf", "diffFlushing05_");
    }

    @Test
    public void tagStructureRemovingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest01.pdf", "cmp_tagStructureRemovingTest01.pdf", "diffRemoving01_");
    }

    @Test
    public void tagStructureRemovingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest02.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        PdfPage firstPage = document.getPage(1);
        PdfPage secondPage = document.getPage(2);
        document.removePage(firstPage);
        document.removePage(secondPage);

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagPointer.addTag(PdfName.P);
        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag()
              .endText();

        document.close();

        compareResult("tagStructureRemovingTest02.pdf", "cmp_tagStructureRemovingTest02.pdf", "diffRemoving02_");
    }

    @Test
    public void tagStructureRemovingTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest03.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page);

        PdfCanvas canvas = new PdfCanvas(page);

        tagPointer.addTag(PdfName.P);
        IAccessibleElement paragraphElement = tagPointer.getConnectedElement(true);

        PdfFont standardFont = PdfFontFactory.createFont(FontConstants.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag()
              .endText();

        tagPointer.moveToParent().moveToParent();

        document.removePage(1);

        PdfPage newPage = document.addNewPage();
        canvas = new PdfCanvas(newPage);
        tagPointer.setPageForTagging(newPage);

        tagPointer.moveToTag(paragraphElement).addTag(PdfName.Span);

        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(standardFont, 24)
                .setTextMatrix(1, 0, 0, 1, 32, 512)
                .showText("Hello.")
                .endText()
                .closeTag();

        document.close();

        compareResult("tagStructureRemovingTest03.pdf", "cmp_tagStructureRemovingTest03.pdf", "diffRemoving03_");
    }

    @Test
    public void tagStructureRemovingTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAnnots.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureRemovingTest04.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest04.pdf", "cmp_tagStructureRemovingTest04.pdf", "diffRemoving04_");
    }

    private void compareResult(String outFileName, String cmpFileName, String diffNamePrefix)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String outPdf = destinationFolder + outFileName;
        String cmpPdf = sourceFolder + cmpFileName;

        String contentDifferences = compareTool.compareByContent(outPdf,
                cmpPdf, destinationFolder, diffNamePrefix);
        String taggedStructureDifferences = compareTool.compareTagStructures(outPdf, cmpPdf);

        String errorMessage = "";
        errorMessage += taggedStructureDifferences == null ? "" : taggedStructureDifferences + "\n";
        errorMessage += contentDifferences == null ? "" : contentDifferences;
        if (!errorMessage.isEmpty()) {
            fail(errorMessage);
        }
    }
}
