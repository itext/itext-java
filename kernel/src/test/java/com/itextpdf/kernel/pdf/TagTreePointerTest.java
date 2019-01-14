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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.ExceptionUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
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

        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.P).addTag(StandardRoles.SPAN);

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
              .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.P).addTag(StandardRoles.SPAN);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(StandardRoles.SPAN);

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
        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        PdfStructureAttributes attributes = new PdfStructureAttributes("random attributes");
        attributes.addTextAttribute("hello", "world");

        tagPointer.addTag(StandardRoles.P).addTag(StandardRoles.SPAN).getProperties()
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
        tagPointer.moveToKid(StandardRoles.TABLE).moveToKid(2, StandardRoles.TR);
        TagTreePointer tagPointerCopy = new TagTreePointer(tagPointer);
        tagPointer.removeTag();

        // tagPointerCopy now points at removed tag

        String exceptionMessage = null;
        try {
            tagPointerCopy.addTag(StandardRoles.SPAN);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        assertEquals(PdfException.TagTreePointerIsInInvalidStateItPointsAtRemovedElementUseMoveToRoot, exceptionMessage);

        tagPointerCopy.moveToRoot().moveToKid(StandardRoles.TABLE);

        tagPointerCopy.moveToKid(StandardRoles.TR);
        TagTreePointer tagPointerCopyCopy = new TagTreePointer(tagPointerCopy);
        tagPointerCopy.flushTag();

        // tagPointerCopyCopy now points at flushed tag

        try {
            tagPointerCopyCopy.addTag(StandardRoles.SPAN);
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
        tagPointer.moveToKid(StandardRoles.TABLE).moveToKid(2, StandardRoles.TR);
        tagPointer.removeTag();

        tagPointer.moveToKid(StandardRoles.TR).moveToKid(StandardRoles.TD)
                .moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN);
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
        tagPointer1.moveToKid(2, StandardRoles.TR);

        TagTreePointer tagPointer2 = new TagTreePointer(document);
        tagPointer2.moveToKid(0, StandardRoles.TR);
        tagPointer1.relocateKid(0, tagPointer2);

        tagPointer1 = new TagTreePointer(document).moveToKid(5, StandardRoles.TR).moveToKid(2, StandardRoles.TD).moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN);
        tagPointer2.moveToKid(StandardRoles.TD).moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN);
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
        tagPointer.setRole(StandardRoles.PART);
        assertEquals(tagPointer.getRole(), "Part");
        tagPointer.moveToKid(StandardRoles.TABLE).getProperties().setLanguage("en-US");
        tagPointer.moveToKid(StandardRoles.P);
        String actualText1 = "Some looong latin text";
        tagPointer.getProperties().setActualText(actualText1);

        WaitingTagsManager waitingTagsManager = document.getTagStructureContext().getWaitingTagsManager();
//        assertNull(waitingTagsManager.getAssociatedObject(tagPointer));
        Object associatedObj = new Object();
        waitingTagsManager.assignWaitingState(tagPointer, associatedObj);

        tagPointer.moveToRoot().moveToKid(StandardRoles.TABLE).moveToKid(1, StandardRoles.TR).getProperties().setActualText("More latin text");

        waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, associatedObj);
        tagPointer.setRole(StandardRoles.DIV);
        tagPointer.getProperties().setLanguage("en-Us");
        assertEquals(tagPointer.getProperties().getActualText(), actualText1);

        document.close();

        compareResult("tagTreePointerTest06.pdf", "cmp_tagTreePointerTest06.pdf", "diff06_");
    }

    @Test
    public void tagTreePointerTest07() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest07.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document).setPageForTagging(page);
        tagPointer.addTag(StandardRoles.SPAN);

        PdfCanvas canvas = new PdfCanvas(page);

        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.beginText()
                .setFontAndSize(standardFont, 24)
                .setTextMatrix(1, 0, 0, 1, 32, 512);

        canvas.openTag(tagPointer.getTagReference())
                .showText("Hello ")
                .closeTag();

        canvas.openTag(tagPointer.getTagReference().addProperty(PdfName.E, new PdfString("Big Mister")))
                .showText(" BMr. ")
                .closeTag();

        canvas.setFontAndSize(standardFont, 30)
                .openTag(tagPointer.getTagReference())
                .showText("World")
                .closeTag();

        canvas.endText();

        document.close();

        compareResult("tagTreePointerTest07.pdf", "cmp_tagTreePointerTest07.pdf", "diff07_");
    }

    @Test
    public void tagTreePointerTest08() throws Exception {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagTreePointerTest08.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = pointer.moveToKid(StandardRoles.DIV).getProperties();
        String language = properties.getLanguage();
        Assert.assertEquals("en-Us", language);
        properties.setLanguage("EN-GB");

        pointer.moveToRoot().moveToKid(2, StandardRoles.P).getProperties().setRole(StandardRoles.H6);
        document.close();

        compareResult("tagTreePointerTest08.pdf", "cmp_tagTreePointerTest08.pdf", "diff08_");
    }

    @Test
    public void tagStructureFlushingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.moveToKid(StandardRoles.TABLE).moveToKid(2, StandardRoles.TR).flushTag();
        tagPointer.moveToKid(3, StandardRoles.TR).moveToKid(StandardRoles.TD).flushTag();
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

        List<IStructureNode> kids = document.getStructTreeRoot().getKids();
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
        tagPointer.moveToKid(StandardRoles.TABLE).moveToKid(2, StandardRoles.TR).flushTag();
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

        tagPointer.addTag(StandardRoles.DIV);

        tagPointer.addTag(StandardRoles.P);
        WaitingTagsManager waitingTagsManager = tagPointer.getContext().getWaitingTagsManager();
        Object pWaitingTagObj = new Object();
        waitingTagsManager.assignWaitingState(tagPointer, pWaitingTagObj);

        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.SPAN);

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

        waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, pWaitingTagObj);
        tagPointer.addTag(StandardRoles.SPAN);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("again")
              .closeTag();

        waitingTagsManager.removeWaitingState(pWaitingTagObj);
        tagPointer.moveToRoot();

        canvas.endText()
              .release();

        PdfPage page2 = document.addNewPage();
        tagPointer.setPageForTagging(page2);
        canvas = new PdfCanvas(page2);

        tagPointer.addTag(StandardRoles.P);
        canvas.beginText()
              .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);
        tagPointer.addTag(StandardRoles.SPAN);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        tagPointer.moveToParent().addTag(StandardRoles.SPAN);

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
    public void tagStructureFlushingTest06() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tagStructureFlushingTest06.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();

        PdfPage page1 = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        tagPointer.addTag(StandardRoles.DIV);

        tagPointer.addTag(StandardRoles.P);

        canvas.beginText();
        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.SPAN);
        WaitingTagsManager waitingTagsManager = document.getTagStructureContext().getWaitingTagsManager();
        Object associatedObj = new Object();
        waitingTagsManager.assignWaitingState(tagPointer, associatedObj);

        canvas.openTag(tagPointer.getTagReference())
              .showText("Hello ")
              .closeTag();

        canvas.setFontAndSize(standardFont, 30)
              .openTag(tagPointer.getTagReference())
              .showText("World")
              .closeTag();

        canvas.endText()
              .release();

        page1.flush();

        tagPointer.relocateKid(0,
                new TagTreePointer(document)
                        .moveToKid(StandardRoles.DIV)
                        .setNextNewKidIndex(0)
                        .addTag(StandardRoles.P)
        );
        tagPointer.removeTag();

        waitingTagsManager.removeWaitingState(associatedObj);
        document.getTagStructureContext().flushPageTags(page1);
        document.getStructTreeRoot().createParentTreeEntryForPage(page1);

        document.close();

        compareResult("tagStructureFlushingTest06.pdf", "cmp_tagStructureFlushingTest06.pdf", "diffFlushing06_");
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

        tagPointer.addTag(StandardRoles.P);
        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.SPAN);

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

        tagPointer.addTag(StandardRoles.P);

        WaitingTagsManager waitingTagsManager = tagPointer.getContext().getWaitingTagsManager();
        Object pWaitingTagObj = new Object();
        waitingTagsManager.assignWaitingState(tagPointer, pWaitingTagObj);

        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.beginText()
              .setFontAndSize(standardFont, 24)
              .setTextMatrix(1, 0, 0, 1, 32, 512);

        tagPointer.addTag(StandardRoles.SPAN);

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

        waitingTagsManager.tryMovePointerToWaitingTag(tagPointer, pWaitingTagObj);
        tagPointer.addTag(StandardRoles.SPAN);

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

    @Test
    public void accessibleAttributesInsertionTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "accessibleAttributesInsertionTest01.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        AccessibilityProperties properties = pointer.moveToKid(0).getProperties(); // 2 attributes

        PdfStructureAttributes testAttr = new PdfStructureAttributes("test");
        testAttr.addIntAttribute("N", 4);
        properties.addAttributes(testAttr);

        testAttr = new PdfStructureAttributes("test");
        testAttr.addIntAttribute("N", 0);
        properties.addAttributes(0, testAttr);

        testAttr = new PdfStructureAttributes("test");
        testAttr.addIntAttribute("N", 5);
        properties.addAttributes(4, testAttr);

        testAttr = new PdfStructureAttributes("test");
        testAttr.addIntAttribute("N", 2);
        properties.addAttributes(2, testAttr);

        try {
            properties.addAttributes(10, testAttr);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        document.close();

        compareResult("accessibleAttributesInsertionTest01.pdf", "cmp_accessibleAttributesInsertionTest01.pdf", "diffAttributes01_");
    }

    @Test
    public void accessibleAttributesInsertionTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "accessibleAttributesInsertionTest02.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfStructureAttributes testAttrDict = new PdfStructureAttributes("test");

        pointer.moveToKid(1).getProperties().addAttributes(testAttrDict); // 1 attribute array

        pointer.moveToRoot();
        pointer.moveToKid(2).getProperties().addAttributes(testAttrDict); // 3 attributes

        pointer.moveToRoot();
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(testAttrDict); // 1 attribute dictionary

        pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(testAttrDict); // no attributes

        document.close();

        compareResult("accessibleAttributesInsertionTest02.pdf", "cmp_accessibleAttributesInsertionTest02.pdf", "diffAttributes02_");
    }

    @Test
    public void accessibleAttributesInsertionTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "accessibleAttributesInsertionTest03.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        pointer.moveToKid(1).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict)); // 1 attribute array

        pointer.moveToRoot();
        pointer.moveToKid(2).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict)); // 3 attributes

        pointer.moveToRoot();
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict)); // 1 attribute dictionary

        pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict)); // no attributes

        document.close();

        compareResult("accessibleAttributesInsertionTest03.pdf", "cmp_accessibleAttributesInsertionTest03.pdf", "diffAttributes03_");
    }

    @Test
    public void accessibleAttributesInsertionTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "accessibleAttributesInsertionTest04.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        pointer.moveToKid(1).getProperties().addAttributes(1, new PdfStructureAttributes(testAttrDict)); // 1 attribute array

        pointer.moveToRoot();
        pointer.moveToKid(2).getProperties().addAttributes(3, new PdfStructureAttributes(testAttrDict)); // 3 attributes

        pointer.moveToRoot();
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(1, new PdfStructureAttributes(testAttrDict)); // 1 attribute dictionary

        document.close();

        compareResult("accessibleAttributesInsertionTest04.pdf", "cmp_accessibleAttributesInsertionTest04.pdf", "diffAttributes04_");
    }

    @Test
    public void accessibleAttributesInsertionTest05() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = new PdfWriter(destinationFolder + "accessibleAttributesInsertionTest05.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        try {
            pointer.moveToKid(1).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict)); // 1 attribute array
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        pointer.moveToRoot();
        try {
            pointer.moveToKid(2).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict)); // 3 attributes
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        pointer.moveToRoot();
        try {
            pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict)); // 1 attribute dictionary
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        try {
            pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict)); // no attributes
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        document.close();

        compareResult("accessibleAttributesInsertionTest05.pdf", "cmp_accessibleAttributesInsertionTest05.pdf", "diffAttributes05_");
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
