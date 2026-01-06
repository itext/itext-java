/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfNamedDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@Tag("IntegrationTest")
public class AddLinkAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddLinkAnnotationTest/";
    public static final String destinationFolder = TestUtil.getOutputPath() + "/kernel/pdf/annot/AddLinkAnnotationTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void addLinkAnnotation01() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotation01.pdf"));
        document.setTagged();

        PdfPage page1 = document.addNewPage();
        PdfPage page2 = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 1");
        canvas.moveText(0, -30);
        canvas.showText("Link to page 2. Click here!");
        canvas.endText();
        canvas.release();
        page1.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 560, 260, 25)).setDestination(
                PdfExplicitDestination.createFit(page2)).setBorder(new PdfArray(new float[] {0, 0, 1})));
        page1.flush();

        canvas = new PdfCanvas(page2);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Page 2");
        canvas.endText();
        canvas.release();
        page2.flush();

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "linkAnnotation01.pdf", sourceFolder + "cmp_linkAnnotation01.pdf",
                        destinationFolder, "diff_"));

    }

    @Test
    public void addLinkAnnotation02() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotation02.pdf"));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.endText();
        canvas.release();

        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setDestination(PdfExplicitDestination.createFit(page)).
                setBorder(new PdfArray(new float[] {0, 0, 1})).
                setColor(new PdfArray(new float[] {1, 0, 0})));
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "linkAnnotation02.pdf", sourceFolder + "cmp_linkAnnotation02.pdf",
                        destinationFolder, "diff_"));

    }

    @Test
    public void linkAnnotationReferenceTest() throws Exception {
        PdfDocument document = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotationReference.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        document.setTagged();
        document.getTagStructureContext().getAutoTaggingPointer().addTag("P");

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.endText();
        canvas.release();

        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setDestination(PdfExplicitDestination.createFit(page)).
                setBorder(new PdfArray(new float[] {0, 0, 1})).
                setColor(new PdfArray(new float[] {1, 0, 0})));
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotationReference.pdf",
                sourceFolder + "cmp_linkAnnotationReference.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void linkAnnotationReference2Test() throws Exception {
        PdfDocument document = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotationReference2.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        document.setTagged();
        document.getTagStructureContext().getAutoTaggingPointer().addTag("P")
                .setNamespaceForNewTags(PdfNamespace.getDefault(document)).addTag("Reference");

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.endText();
        canvas.release();

        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(page))).
                setBorder(new PdfArray(new float[] {0, 0, 1})).
                setColor(new PdfArray(new float[] {1, 0, 0})));
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotationReference2.pdf",
                sourceFolder + "cmp_linkAnnotationReference2.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void severalLinkAnnotationsTest() throws Exception {
        try (PdfDocument document = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "severalLinkAnnotations.pdf"))) {
            document.setTagged();
            document.getTagStructureContext().getAutoTaggingPointer();

            PdfPage page = document.addNewPage();
            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25))
                    .setAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(page))));

            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25))
                    .setAction(PdfAction.createGoToR(new PdfStringFS("Some fake destination"),
                            new PdfExplicitDestination(new PdfArray(new PdfNumber(2))))));

            PdfDictionary destinationDictionary = new PdfDictionary();
            destinationDictionary.put(PdfName.D, PdfExplicitDestination.createFit(page).getPdfObject());
            PdfDictionary destinationDictionary2 = new PdfDictionary();
            destinationDictionary2.put(PdfName.SD, PdfExplicitDestination.createFit(page).getPdfObject());

            PdfDictionary dests = new PdfDictionary();
            dests.put(new PdfName("destination_name"), destinationDictionary);
            dests.put(new PdfName("destination_name_2"), destinationDictionary2);

            document.getCatalog().put(PdfName.Dests, dests);
            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                    setDestination(new PdfNamedDestination("destination_name")));

            document.getCatalog().getNameTree(PdfName.Dests).addEntry("destination_name2",
                    new PdfExplicitRemoteGoToDestination(new PdfArray(new PdfNumber(1))).getPdfObject());
            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                    setDestination(new PdfStringDestination("destination_name2")));
        }

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "severalLinkAnnotations.pdf",
                sourceFolder + "cmp_severalLinkAnnotations.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void linkAnnotationWithDictionaryStringDestinationTest() throws Exception {
        try (PdfDocument document = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotationWithDictionaryStringDestination.pdf"))) {
            document.setTagged();
            document.getTagStructureContext().getAutoTaggingPointer();

            PdfPage page = document.addNewPage();
            PdfDictionary destinationDictionary = new PdfDictionary();
            destinationDictionary.put(PdfName.D, PdfExplicitDestination.createFit(page).getPdfObject());
            document.getCatalog().getNameTree(PdfName.Dests)
                    .addEntry("destination_name", destinationDictionary);

            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                    setDestination(new PdfStringDestination("destination_name")));
        }

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotationWithDictionaryStringDestination.pdf",
                sourceFolder + "cmp_linkAnnotationWithDictionaryStringDestination.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void linkAnnotationWithCyclicReferencesTest() throws Exception {
        try (PdfDocument document = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotationWithCyclicReferences.pdf"))) {
            document.setTagged();
            document.getTagStructureContext().getAutoTaggingPointer();
            PdfPage page = document.addNewPage();

            PdfDictionary dests = new PdfDictionary();
            dests.put(new PdfName("destination_name"), new PdfName("destination_name"));
            document.getCatalog().put(PdfName.Dests, dests);

            page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25))
                    .setAction(PdfAction.createGoTo(new PdfNamedDestination("destination_name"))));
        }

        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "linkAnnotationWithCyclicReferences.pdf",
                sourceFolder + "cmp_linkAnnotationWithCyclicReferences.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void addAndGetLinkAnnotations() throws Exception {
        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + "linkAnnotation03.pdf"));

        PdfPage page = document.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(StandardFonts.COURIER_BOLD), 14);
        canvas.moveText(100, 600);
        canvas.showText("Click here to go to itextpdf site.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf blog.");
        canvas.moveText(0, -50);
        canvas.showText("Click here to go to itextpdf FAQ.");
        canvas.endText();
        canvas.release();
        int[] borders = {0, 0, 1};
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 590, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com")).
                setBorder(new PdfArray(borders)).
                setColor(new PdfArray(new float[] {1, 0, 0})));
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com/node")).
                setBorder(new PdfArray(borders)).
                setColor(new PdfArray(new float[] {0, 1, 0})));
        page.addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 490, 300, 25)).
                setAction(PdfAction.createURI("http://itextpdf.com/salesfaq")).
                setBorder(new PdfArray(borders)).
                setColor(new PdfArray(new float[] {0, 0, 1})));
        page.flush();

        document.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + "linkAnnotation03.pdf", sourceFolder + "cmp_linkAnnotation03.pdf",
                        destinationFolder, "diff_"));

        document = new PdfDocument(CompareTool.createOutputReader(destinationFolder + "linkAnnotation03.pdf"));
        page = document.getPage(1);
        Assertions.assertEquals(3, page.getAnnotsSize());
        List<PdfAnnotation> annotations = page.getAnnotations();
        Assertions.assertEquals(3, annotations.size());
        PdfLinkAnnotation link = (PdfLinkAnnotation) annotations.get(0);
        Assertions.assertEquals(page, link.getPage());
        document.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DESTINATION_NOT_PERMITTED_WHEN_ACTION_IS_SET)})
    public void linkAnnotationActionDestinationTest() throws IOException, InterruptedException {
        String fileName = "linkAnnotationActionDestinationTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName));
        PdfArray array = new PdfArray();
        array.add(pdfDocument.addNewPage().getPdfObject());
        array.add(PdfName.XYZ);
        array.add(new PdfNumber(36));
        array.add(new PdfNumber(100));
        array.add(new PdfNumber(1));

        PdfDestination dest = PdfDestination.makeDestination(array);

        PdfLinkAnnotation link = new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0));
        link.setAction(PdfAction.createGoTo("abc"));
        link.setDestination(dest);

        pdfDocument.getPage(1).addAnnotation(link);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName, destinationFolder));
    }

    @Test
    public void removeLinkAnnotationTaggedAsLinkTest() throws IOException, InterruptedException {
        String input = sourceFolder + "taggedLinkAnnotationAsLink.pdf";
        String output = destinationFolder + "removeLinkAnnotationTaggedAsLinkTest.pdf";
        String cmp =  sourceFolder + "cmp_" + "removeLinkAnnotationTaggedAsLinkTest.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output))) {
            PdfPage page = pdfDoc.getPage(1);
            page.removeAnnotation(page.getAnnotations().get(0));
        }

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder));
    }

    @Test
    public void removeLinkAnnotationTaggedAsAnnotTest() throws IOException, InterruptedException {
        String input = sourceFolder + "taggedLinkAnnotationAsAnnot.pdf";
        String output = destinationFolder + "removeLinkAnnotationTaggedAsAnnotTest.pdf";
        String cmp =  sourceFolder + "cmp_" + "removeLinkAnnotationTaggedAsAnnotTest.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output))) {
            PdfPage page = pdfDoc.getPage(1);
            page.removeAnnotation(page.getAnnotations().get(0));
        }

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder));
    }

    @Test
    public void removeLinkAnnotationTagWithContentTest() throws IOException, InterruptedException {
        String input = sourceFolder + "taggedLinkAnnotationTagWithContent.pdf";
        String output = destinationFolder + "removeLinkAnnotationTagWithContentTest.pdf";
        String cmp =  sourceFolder + "cmp_" + "removeLinkAnnotationTagWithContentTest.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output))) {
            PdfPage page = pdfDoc.getPage(1);
            page.removeAnnotation(page.getAnnotations().get(0));
        }

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder));
    }

    @Test
    public void removeLinkAnnotationWithNoTagTest() throws IOException, InterruptedException {
        String input = sourceFolder + "taggedInvalidNoLinkAnnotationTag.pdf";
        String output = destinationFolder + "removeLinkAnnotationWithNoTagTest.pdf";
        String cmp =  sourceFolder + "cmp_" + "removeLinkAnnotationWithNoTagTest.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(input), CompareTool.createTestPdfWriter(output))) {
            PdfPage page = pdfDoc.getPage(1);
            page.removeAnnotation(page.getAnnotations().get(0));
        }

        Assertions.assertNull(new CompareTool().compareByContent(output, cmp, destinationFolder));
    }

    @Test
    public void addLinkAnnotInTagged13PdfTest() throws Exception {
        String outPdf = destinationFolder + "addLinkAnnotInTagged13PdfTest.pdf";
        String cmpPdf = sourceFolder + "cmp_addLinkAnnotInTagged13PdfTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(
                outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_1_3)))) {
            pdfDoc.setTagged();

            PdfPage page = pdfDoc.addNewPage();

            PdfLinkAnnotation annot = (PdfLinkAnnotation) new PdfLinkAnnotation(new Rectangle(100, 600, 50, 40))
                    .setAction(PdfAction.createURI("http://itextpdf.com"))
                    .setBorder(new PdfArray(new float[]{0, 0, 1}))
                    .setColor(new PdfArray(new float[]{1, 0, 0}));
            page.addAnnotation(annot);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }
}
