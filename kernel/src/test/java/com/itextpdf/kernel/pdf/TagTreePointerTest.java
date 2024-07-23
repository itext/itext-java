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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.exceptions.ExceptionUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructIdTree;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.WaitingTagsManager;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.AfterClass;
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

    @AfterClass
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void tagTreePointerTest01() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest01.pdf")
                .setCompressionLevel(CompressionConstants.NO_COMPRESSION);
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest02.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest03.pdf");
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
        assertEquals(KernelExceptionMessageConstant.TAG_TREE_POINTER_IS_IN_INVALID_STATE_IT_POINTS_AT_REMOVED_ELEMENT_USE_MOVE_TO_ROOT, exceptionMessage);

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
        assertEquals(KernelExceptionMessageConstant.TAG_TREE_POINTER_IS_IN_INVALID_STATE_IT_POINTS_AT_FLUSHED_ELEMENT_USE_MOVE_TO_ROOT, exceptionMessage);

        try {
            tagPointerCopy.moveToKid(0);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        assertEquals(KernelExceptionMessageConstant.CANNOT_MOVE_TO_FLUSHED_KID, exceptionMessage);

        document.close();
    }

    @Test
    public void tagTreePointerTest04() throws Exception {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest04.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest05.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest06.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest07.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagTreePointerTest08.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = pointer.moveToKid(StandardRoles.DIV).getProperties();
        String language = properties.getLanguage();
        Assert.assertEquals("en-Us", language);
        properties.setLanguage("EN-GB");

        pointer.moveToRoot().moveToKid(2, StandardRoles.P).getProperties().setRole(StandardRoles.H6);
        String role = pointer.getProperties().getRole();
        Assert.assertEquals("H6", role);
        document.close();

        compareResult("tagTreePointerTest08.pdf", "cmp_tagTreePointerTest08.pdf", "diff08_");
    }

    @Test
    public void changeExistedBackedAccessibilityPropertiesTest() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "changeExistedBackedAccessibilityPropertiesTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)).setCompressionLevel(
                CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = pointer.moveToKid(StandardRoles.DIV).getProperties();
        String altDescription = "Alternate Description";
        properties.setAlternateDescription(altDescription);
        Assert.assertEquals(altDescription, properties.getAlternateDescription());
        String expansion = "expansion";
        properties.setExpansion(expansion);
        Assert.assertEquals(expansion, properties.getExpansion());
        properties.setNamespace(new PdfNamespace(StandardNamespaces.PDF_2_0));
        Assert.assertEquals(StandardNamespaces.PDF_2_0, properties.getNamespace().getNamespaceName());
        String phoneme = "phoneme";
        properties.setPhoneme(phoneme);
        Assert.assertEquals(phoneme, properties.getPhoneme());
        String phoneticAlphabet = "Phonetic Alphabet";
        properties.setPhoneticAlphabet(phoneticAlphabet);
        Assert.assertEquals(phoneticAlphabet, properties.getPhoneticAlphabet());

        document.close();

        compareResult("changeExistedBackedAccessibilityPropertiesTest.pdf",
                "cmp_changeExistedBackedAccessibilityPropertiesTest.pdf", "diffBackProp01_");
    }

    @Test
    public void removeExistedBackedAccessibilityPropertiesTest() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "removeExistedBackedAccessibilityPropertiesTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)).setCompressionLevel(
                CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = pointer.moveToKid(StandardRoles.DIV).getProperties();
        Assert.assertNotNull(properties.getAttributesList());
        Assert.assertNotNull(properties.addAttributes(0, null));
        properties.clearAttributes();
        Assert.assertTrue(properties.getAttributesList().isEmpty());
        properties.addRef(pointer);
        Assert.assertFalse(properties.getRefsList().isEmpty());
        properties.clearRefs();
        Assert.assertTrue(properties.getRefsList().isEmpty());

        document.close();

        compareResult("removeExistedBackedAccessibilityPropertiesTest.pdf",
                "cmp_removeExistedBackedAccessibilityPropertiesTest.pdf", "diffBackProp02_");
    }

    @Test
    public void setDefaultAccessibilityPropertiesTest() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "setDefaultAccessibilityPropertiesTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)).setCompressionLevel(
                CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = new DefaultAccessibilityProperties(StandardRoles.DIV);
        properties.setRole(StandardRoles.H6);
        Assert.assertEquals(StandardRoles.H6, properties.getRole());
        String actualText = "Test text";
        properties.setActualText(actualText);
        Assert.assertEquals(actualText, properties.getActualText());
        String language = "EN-GB";
        properties.setLanguage(language);
        Assert.assertEquals(language, properties.getLanguage());
        String alternateDescription = "Alternate Description";
        properties.setAlternateDescription(alternateDescription);
        Assert.assertEquals(alternateDescription, properties.getAlternateDescription());
        String expansion = "expansion";
        properties.setExpansion(expansion);
        Assert.assertEquals(expansion, properties.getExpansion());
        properties.setNamespace(new PdfNamespace(StandardNamespaces.PDF_2_0));
        Assert.assertEquals(StandardNamespaces.PDF_2_0, properties.getNamespace().getNamespaceName());
        String phoneme = "phoneme";
        properties.setPhoneme(phoneme);
        Assert.assertEquals(phoneme, properties.getPhoneme());
        String phoneticAlphabet = "phoneticAlphabet";
        properties.setPhoneticAlphabet(phoneticAlphabet);
        Assert.assertEquals(phoneticAlphabet, properties.getPhoneticAlphabet());
        properties.addRef(pointer);
        Assert.assertFalse(properties.getRefsList().isEmpty());
        pointer.addTag(properties);

        document.close();

        compareResult("setDefaultAccessibilityPropertiesTest.pdf", "cmp_setDefaultAccessibilityPropertiesTest.pdf",
                "diffDefaultProp01_");
    }

    @Test
    public void removeDefaultAccessibilityPropertiesTest() throws Exception {
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "removeDefaultAccessibilityPropertiesTest.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)).setCompressionLevel(
                CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "taggedDocument2.pdf"), writer);

        TagTreePointer pointer = new TagTreePointer(document);
        AccessibilityProperties properties = new DefaultAccessibilityProperties(StandardRoles.DIV);
        PdfStructureAttributes testAttr = new PdfStructureAttributes("test");
        testAttr.addIntAttribute("N", 4);
        properties.addAttributes(testAttr);
        properties.addAttributes(1, testAttr);
        properties.getAttributesList();
        properties.clearAttributes();
        Assert.assertTrue(properties.getAttributesList().isEmpty());
        properties.addRef(pointer);
        Assert.assertFalse(properties.getRefsList().isEmpty());
        properties.clearRefs();
        Assert.assertTrue(properties.getRefsList().isEmpty());
        pointer.addTag(properties);

        document.close();

        compareResult("removeDefaultAccessibilityPropertiesTest.pdf",
                "cmp_removeDefaultAccessibilityPropertiesTest.pdf", "diffDefaultProp02_");
    }

    @Test
    public void tagStructureFlushingTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest01.pdf");
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

        assertEquals(KernelExceptionMessageConstant.CANNOT_FLUSH_DOCUMENT_ROOT_TAG_BEFORE_DOCUMENT_IS_CLOSED, exceptionMessage);
        compareResult("tagStructureFlushingTest01.pdf", "taggedDocument.pdf", "diffFlushing01_");
    }

    @Test
    public void tagStructureFlushingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest02.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest03.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest04.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest05.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureFlushingTest06.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureRemovingTest01.pdf");
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest01.pdf", "cmp_tagStructureRemovingTest01.pdf", "diffRemoving01_");
    }

    @Test
    public void tagStructureRemovingTest02() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocument.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureRemovingTest02.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureRemovingTest03.pdf");
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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "tagStructureRemovingTest04.pdf").setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(reader, writer);
        document.removePage(1);
        document.close();

        compareResult("tagStructureRemovingTest04.pdf", "cmp_tagStructureRemovingTest04.pdf", "diffRemoving04_");
    }

    @Test
    public void structureElementWithIdTest() throws Exception {
        String outfName = "structureElementWithIdTest.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + outfName).
                setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        compareResult(outfName, "cmp_" + outfName, "diff01_");
    }

    @Test
    public void structureElementWithIdFromPropsTest() throws IOException {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
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

        // create a tag with an ID, some attributes and other properties
        DefaultAccessibilityProperties spanProps = new DefaultAccessibilityProperties(StandardRoles.SPAN);
        spanProps.setStructureElementIdString("hello-element");
        PdfStructureAttributes attrs = new PdfStructureAttributes("Layout");
        attrs.addEnumAttribute("Placement", "Inline");
        spanProps.addAttributes(attrs);
        spanProps.setActualText("Hello!");
        spanProps.setAlternateDescription("This is a piece of sample text");

        tagPointer.addTag(StandardRoles.P).addTag(spanProps);
        canvas.openTag(tagPointer.getTagReference())
                .showText("Hello!")
                .closeTag();
        tagPointer.moveToParent();

        page1.flush();
        document.close();

        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
                PdfDocument documentToModify = new PdfDocument(r)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx
                    .getTagPointerById("hello-element".getBytes(StandardCharsets.UTF_8));
            PdfStructureAttributes layoutAttrs = ptrHello.getProperties().getAttributesList().get(0);
            assertEquals("Inline", layoutAttrs.getAttributeAsEnum("Placement"));
        }
    }

    @Test
    public void retrieveStructureElementsByIdTest() throws Exception {
        String infName = "cmp_structureElementWithIdTest.pdf";
        // check that we can retrieve the IDs in the output
        PdfReader r = new PdfReader(sourceFolder + infName);
        PdfDocument readPdfDoc = new PdfDocument(r);
        TagStructureContext ctx = readPdfDoc.getTagStructureContext();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        TagTreePointer ptrHello = ctx.getTagPointerByIdString("hello-element");
        assertArrayEquals(ptrHello.getProperties().getStructureElementId(), helloId);
    }

    @Test
    public void structureElementWithoutIdTest() throws Exception {
        String infName = "cmp_structureElementWithIdTest.pdf";
        PdfReader r = new PdfReader(sourceFolder + infName);
        PdfDocument readPdfDoc = new PdfDocument(r);
        TagStructureContext ctx = readPdfDoc.getTagStructureContext();

        TagTreePointer ptrHello = ctx.getTagPointerByIdString("hello-element");
        // the parent is a P without ID -> we should get null
        ptrHello.moveToParent();
        assertNull(ptrHello.getProperties().getStructureElementId());
    }

    @Test
    public void disambiguateStructureElementsByIdTest() throws Exception {
        String infName = "cmp_structureElementWithIdTest.pdf";
        PdfReader r = new PdfReader(sourceFolder + infName);
        PdfDocument readPdfDoc = new PdfDocument(r);
        TagStructureContext ctx = readPdfDoc.getTagStructureContext();

        TagTreePointer ptrHello = ctx.getTagPointerByIdString("hello-element");
        TagTreePointer ptrWorld = ctx.getTagPointerByIdString("world-element");
        assertFalse(ptrHello.isPointingToSameTag(ptrWorld));
    }

    @Test
    public void structureElementWithNonexistentIdTest() throws Exception {
        String infName = "cmp_structureElementWithIdTest.pdf";
        PdfReader r = new PdfReader(sourceFolder + infName);
        PdfDocument readPdfDoc = new PdfDocument(r);
        TagStructureContext ctx = readPdfDoc.getTagStructureContext();
        TagTreePointer ptrNone = ctx.getTagPointerById("nonexistent-element".getBytes(StandardCharsets.UTF_8));
        assertNull(ptrNone);
    }

    @Test
    public void structureElementRemoveIdTest() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
            PdfWriter w = new PdfWriter(baos2);
            PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            // remove the ID
            ptrHello.getProperties().setStructureElementId(null);
            assertNull(ctx.getTagPointerById(helloId));
        }
    }

    @Test
    public void structureElementRemoveIdNoopTest() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
                PdfWriter w = new PdfWriter(baos2);
                PdfDocument pdfDoc = new PdfDocument(r, w)) {
            PdfStructIdTree tree = pdfDoc.getStructTreeRoot().getIdTree();
            tree.removeEntry(new PdfString("i-dont-exist"));
            assertFalse(tree.isModified());
        }
    }

    @Test
    public void structureElementRemoveIdStringTest() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
                PdfWriter w = new PdfWriter(baos2);
                PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            // remove the ID
            ptrHello.getProperties().setStructureElementIdString(null);
            assertNull(ctx.getTagPointerById(helloId));
        }
    }

    @Test
    public void structureElementRemoveIdPersist() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addAndRemoveId(baos);
        // check if the changes were properly persisted
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
                PdfDocument documentRead = new PdfDocument(r)) {

            TagStructureContext ctx = documentRead.getTagStructureContext();
            assertNull(ctx.getTagPointerByIdString("hello-element"));
        }
    }

    @Test
    public void structureElementRemoveIdPersistNoCollateralDamage() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addAndRemoveId(baos);
        // check if the changes were properly persisted
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
                PdfDocument documentRead = new PdfDocument(r)) {

            TagStructureContext ctx = documentRead.getTagStructureContext();
            byte[] id = "world-element".getBytes(StandardCharsets.UTF_8);
            byte[] retrieved = ctx.getTagPointerById(id).getProperties().getStructureElementId();
            assertArrayEquals(id, retrieved);
        }
    }

    @Test
    public void structureElementModifyIdTest() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        byte[] helloId2 = "hello2-element".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
            PdfWriter w = new PdfWriter(baos2);
            PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            // modify the ID to a new value
            ptrHello.getProperties().setStructureElementId(helloId2);
            assertTrue(ptrHello.isPointingToSameTag(ctx.getTagPointerById(helloId2)));
        }
    }

    @Test
    public void structureElementModifyIdNoopTest() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
                PdfWriter w = new PdfWriter(baos2);
                PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            ptrHello.getProperties().setStructureElementId(helloId);
            assertFalse(documentToModify.getStructTreeRoot().getIdTree().isModified());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, count = 1)})
    public void structureElementClobberIdWarning() throws Exception {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos1);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        byte[] worldId = "world-element".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos1.toByteArray()));
                PdfWriter w = new PdfWriter(baos2);
                PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrWorld = ctx.getTagPointerById(worldId);
            // modify the ID to a new value
            ptrWorld.getProperties().setStructureElementId(helloId);
            // this should clobber the old value and trigger a warning
            assertTrue(ptrWorld.isPointingToSameTag(ctx.getTagPointerById(helloId)));
        }
    }

    @Test
    public void structureElementModifyIdNewRegistered() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addAndModifyStructElemId(baos);
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
                PdfDocument documentRead = new PdfDocument(r)) {

            TagStructureContext ctx = documentRead.getTagStructureContext();
            byte[] id = "hello2-element".getBytes(StandardCharsets.UTF_8);
            byte[] retrieved = ctx.getTagPointerById(id).getProperties().getStructureElementId();
            assertArrayEquals(id, retrieved);
        }
    }

    @Test
    public void structureElementModifyIdOldRemoved() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addAndModifyStructElemId(baos);
        // check if the changes were properly persisted
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
                PdfDocument documentRead = new PdfDocument(r)) {

            TagStructureContext ctx = documentRead.getTagStructureContext();
            assertNull(ctx.getTagPointerByIdString("hello-element"));
        }
    }

    @Test
    public void structureElementModifyIdNoCollateralDamage() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        addAndModifyStructElemId(baos);
        // check if the changes were properly persisted
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
                PdfDocument documentRead = new PdfDocument(r)) {

            TagStructureContext ctx = documentRead.getTagStructureContext();
            byte[] id = "world-element".getBytes(StandardCharsets.UTF_8);
            byte[] retrieved = ctx.getTagPointerById(id).getProperties().getStructureElementId();
            assertArrayEquals(id, retrieved);
        }
    }

    @Test
    public void accessibleAttributesInsertionTest01() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "accessibleAttributesInsertionTest01.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        // 2 attributes
        AccessibilityProperties properties = pointer.moveToKid(0).getProperties();

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
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "accessibleAttributesInsertionTest02.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfStructureAttributes testAttrDict = new PdfStructureAttributes("test");

        // 1 attribute array
        pointer.moveToKid(1).getProperties().addAttributes(testAttrDict);

        pointer.moveToRoot();
        // 3 attributes
        pointer.moveToKid(2).getProperties().addAttributes(testAttrDict);

        pointer.moveToRoot();
        // 1 attribute dictionary
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(testAttrDict);

        // no attributes
        pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(testAttrDict);

        document.close();

        compareResult("accessibleAttributesInsertionTest02.pdf", "cmp_accessibleAttributesInsertionTest02.pdf", "diffAttributes02_");
    }

    @Test
    public void accessibleAttributesInsertionTest03() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "accessibleAttributesInsertionTest03.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        // 1 attribute array
        pointer.moveToKid(1).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict));

        pointer.moveToRoot();
        // 3 attributes
        pointer.moveToKid(2).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict));

        pointer.moveToRoot();
        // 1 attribute dictionary
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict));

        // no attributes
        pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(0, new PdfStructureAttributes(testAttrDict));

        document.close();

        compareResult("accessibleAttributesInsertionTest03.pdf", "cmp_accessibleAttributesInsertionTest03.pdf", "diffAttributes03_");
    }

    @Test
    public void accessibleAttributesInsertionTest04() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "accessibleAttributesInsertionTest04.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        // 1 attribute array
        pointer.moveToKid(1).getProperties().addAttributes(1, new PdfStructureAttributes(testAttrDict));

        pointer.moveToRoot();
        // 3 attributes
        pointer.moveToKid(2).getProperties().addAttributes(3, new PdfStructureAttributes(testAttrDict));

        pointer.moveToRoot();
        // 1 attribute dictionary
        pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(1, new PdfStructureAttributes(testAttrDict));

        document.close();

        compareResult("accessibleAttributesInsertionTest04.pdf", "cmp_accessibleAttributesInsertionTest04.pdf", "diffAttributes04_");
    }

    @Test
    public void accessibleAttributesInsertionTest05() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocumentWithAttributes.pdf");
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + "accessibleAttributesInsertionTest05.pdf");
        PdfDocument document = new PdfDocument(reader, writer);

        TagTreePointer pointer = new TagTreePointer(document);

        PdfDictionary testAttrDict = new PdfDictionary();

        try {
            // 1 attribute array
            pointer.moveToKid(1).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        pointer.moveToRoot();
        try {
            // 3 attributes
            pointer.moveToKid(2).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        pointer.moveToRoot();
        try {
            // 1 attribute dictionary
            pointer.moveToKid(0).moveToKid(StandardRoles.LI).moveToKid(StandardRoles.LBODY).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(ExceptionUtil.isOutOfRange(e));
        }

        try {
            // no attributes
            pointer.moveToKid(StandardRoles.P).moveToKid(StandardRoles.SPAN).getProperties().addAttributes(5, new PdfStructureAttributes(testAttrDict));
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

    private static void addContentWithIds(PdfDocument document) throws IOException {

        PdfPage page1 = document.addNewPage();
        TagTreePointer tagPointer = new TagTreePointer(document);
        tagPointer.setPageForTagging(page1);

        PdfCanvas canvas = new PdfCanvas(page1);

        PdfFont standardFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        canvas.beginText()
                .setFontAndSize(standardFont, 24)
                .setTextMatrix(1, 0, 0, 1, 32, 512);

        DefaultAccessibilityProperties paraProps
                = new DefaultAccessibilityProperties(StandardRoles.P);
        tagPointer.addTag(paraProps).addTag(StandardRoles.SPAN);

        tagPointer.getProperties().setStructureElementIdString("hello-element");
        canvas.openTag(tagPointer.getTagReference())
                .showText("Hello ")
                .closeTag();
        tagPointer.moveToParent().addTag(StandardRoles.SPAN);

        tagPointer.getProperties().setStructureElementIdString("world-element");
        canvas.setFontAndSize(standardFont, 30)
                .openTag(tagPointer.getTagReference())
                .showText("World")
                .closeTag();

        tagPointer.moveToParent();

        canvas.endText().release();

        page1.flush();
    }

    private void addAndRemoveId(OutputStream baos) throws Exception {
        ByteArrayOutputStream preBaos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(preBaos);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(preBaos.toByteArray()));
                PdfWriter w = new PdfWriter(baos);
                PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            // remove the ID
            ptrHello.getProperties().setStructureElementId(null);
        }
    }

    private void addAndModifyStructElemId(OutputStream baos) throws Exception {
        ByteArrayOutputStream preBaos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(preBaos);
        PdfDocument document = new PdfDocument(writer);
        document.setTagged();
        addContentWithIds(document);
        document.close();

        byte[] helloId = "hello-element".getBytes(StandardCharsets.UTF_8);
        byte[] helloId2 = "hello2-element".getBytes(StandardCharsets.UTF_8);
        try(PdfReader r = new PdfReader(new ByteArrayInputStream(preBaos.toByteArray()));
                PdfWriter w = new PdfWriter(baos);
                PdfDocument documentToModify = new PdfDocument(r, w)) {

            TagStructureContext ctx = documentToModify.getTagStructureContext();
            TagTreePointer ptrHello = ctx.getTagPointerById(helloId);
            // modify the ID to a new value
            ptrHello.getProperties().setStructureElementId(helloId2);
        }
    }
}
