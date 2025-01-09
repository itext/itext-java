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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("IntegrationTest")
public class PdfDestinationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDestinationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDestinationTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }
    
    @Test
    public void destTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleNoLinks.pdf";
        String outFile = destinationFolder + "destTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destTest01.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outFile));
        PdfPage firstPage = document.getPage(1);
        
        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(PdfExplicitDestination.createFit(document.getPage(2))));
        firstPage.addAnnotation(linkExplicitDest);
        
        PdfLinkAnnotation linkStringDest = new PdfLinkAnnotation(new Rectangle(35, 760, 160, 15));
        PdfExplicitDestination destToPage3 = PdfExplicitDestination.createFit(document.getPage(3));
        String stringDest = "thirdPageDest";
        document.addNamedDestination(stringDest, destToPage3.getPdfObject());
        linkStringDest.setAction(PdfAction.createGoTo(new PdfStringDestination(stringDest)));
        firstPage.addAnnotation(linkStringDest);
        
        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest01() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest01.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest01.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest02() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest02.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest02.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest03() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest03.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest03.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest04() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest04.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest04.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 3), destDoc);
        destDoc.close();
        
        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest05() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "simpleWithLinks.pdf";
        String outFile = destinationFolder + "destCopyingTest05.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest05.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 3, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest06() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sourceWithNamedDestination.pdf";
        String outFile = destinationFolder + "destCopyingTest06.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest06.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void destCopyingTest07() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "sourceStringDestWithPageNumber.pdf";
        String outFile = destinationFolder + "destCopyingTest07.pdf";
        String cmpFile = sourceFolder + "cmp_destCopyingTest07.pdf";
        PdfDocument srcDoc = new PdfDocument(new PdfReader(srcFile));

        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        srcDoc.copyPagesTo(Arrays.asList(1, 2, 1), destDoc);
        destDoc.close();

        srcDoc.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void structureDestinationWithoutRemoteIdTest() throws IOException {
        String srcFile = sourceFolder + "customRolesMappingPdf2.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), new PdfWriter(new ByteArrayOutputStream()));

        PdfStructElem imgElement = new PdfStructElem((PdfDictionary) document.getPdfObject(13));
        try {
            PdfAction.createGoToR(new PdfStringFS("Some fake destination"),
                    PdfStructureDestination.createFit(imgElement));
            Assertions.fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Structure destinations shall specify structure element ID in remote go-to actions. Structure element that has no ID is specified instead", e.getMessage());
        }

        document.close();
    }

    @Test
    public void structureDestination01Test() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "customRolesMappingPdf2.pdf";
        String outFile = destinationFolder + "structureDestination01Test.pdf";
        String cmpFile = sourceFolder + "cmp_structureDestination01Test.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outFile));

        PdfStructElem imgElement = new PdfStructElem((PdfDictionary) document.getPdfObject(13));
        PdfStructureDestination dest = PdfStructureDestination.createFit(imgElement);

        PdfPage secondPage = document.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(dest));
        secondPage.addAnnotation(linkExplicitDest);

        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void structureDestination02Test() throws IOException, InterruptedException {
        String srcFile = sourceFolder + "customRolesMappingPdf2.pdf";
        String outFile = destinationFolder + "structureDestination02Test.pdf";
        String cmpFile = sourceFolder + "cmp_structureDestination02Test.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(srcFile), CompareTool.createTestPdfWriter(outFile));

        PdfStructElem imgElement = new PdfStructElem((PdfDictionary) document.getPdfObject(13));
        PdfStructureDestination dest = PdfStructureDestination.createFit(imgElement);

        PdfPage secondPage = document.addNewPage();
        PdfPage thirdPage = document.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        PdfAction gotoStructAction = PdfAction.createGoTo(PdfExplicitDestination.createFit(thirdPage));
        gotoStructAction.put(PdfName.SD, dest.getPdfObject());
        linkExplicitDest.setAction(gotoStructAction);
        secondPage.addAnnotation(linkExplicitDest);

        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void makeDestination01Test() throws IOException {
        String srcFile = sourceFolder + "cmp_structureDestination01Test.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcFile));
        PdfObject destObj = ((PdfLinkAnnotation) pdfDocument.getPage(2).getAnnotations().get(0)).getAction().get(PdfName.D);
        PdfDestination destWrapper = PdfDestination.makeDestination(destObj);
        Assertions.assertEquals(PdfStructureDestination.class, destWrapper.getClass());
    }

    @Test
    public void remoteGoToDestinationTest01() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToDestinationTest01.pdf";
        String outFile = destinationFolder + "remoteGoToDestinationTest01.pdf";

        PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        out.addNewPage();

        List<PdfDestination> destinations = new ArrayList<>(7);
        destinations.add(PdfExplicitRemoteGoToDestination.createFit(1));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitH(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitV(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitR(1, 10, 10, 10, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitB(1));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitBH(1, 10));
        destinations.add(PdfExplicitRemoteGoToDestination.createFitBV(1, 10));

        int y = 785;
        for (PdfDestination destination : destinations) {
            PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, y, 160, 15));
            PdfAction action = PdfAction.createGoToR(new PdfStringFS("Some fake destination"), destination);
            linkExplicitDest.setAction(action);
            out.getFirstPage().addAnnotation(linkExplicitDest);
            y -= 20;
        }
        out.close();
        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void remoteGoToDestinationTest02() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToDestinationTest02.pdf";
        String outFile = destinationFolder + "remoteGoToDestinationTest02.pdf";

        PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        out.addNewPage();
        out.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        PdfAction action = PdfAction.createGoToR(new PdfStringFS("Some fake destination"),
                PdfExplicitRemoteGoToDestination.createFitR(2, 10, 10, 10, 10), true);
        linkExplicitDest.setAction(action);
        out.getFirstPage().addAnnotation(linkExplicitDest);

        out.close();
        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void remoteGoToRIllegalDestinationTest() throws IOException {
        String outFile = destinationFolder + "remoteGoToDestinationTest01.pdf";

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.addNewPage();
        document.addNewPage();

        try {
            PdfAction.createGoToR(new PdfStringFS("Some fake destination"),
                    PdfExplicitDestination.createFitB(document.getPage(1)));
            Assertions.fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Explicit destinations shall specify page number in remote go-to actions instead of page dictionary", e.getMessage());
        }
        document.close();
    }

    @Test
    public void remoteGoToRByIntDestinationTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToRByIntDestinationTest.pdf";
        String outFile = destinationFolder + "remoteGoToRByIntDestinationTest.pdf";

        PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        out.addNewPage();
        out.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        PdfAction action = PdfAction.createGoToR("Some fake destination", 2);
        linkExplicitDest.setAction(action);
        out.getFirstPage().addAnnotation(linkExplicitDest);

        out.close();
        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void remoteGoToRByStringDestinationTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToRByStringDestinationTest.pdf";
        String outFile = destinationFolder + "remoteGoToRByStringDestinationTest.pdf";

        PdfDocument out = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        out.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        PdfAction action = PdfAction.createGoToR("Some fake destination", "1");
        linkExplicitDest.setAction(action);
        out.getFirstPage().addAnnotation(linkExplicitDest);

        out.close();
        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.INVALID_DESTINATION_TYPE)})
    public void remoteGoToNotValidExplicitDestinationTest() throws IOException, InterruptedException {
        String cmpFile = sourceFolder + "cmp_remoteGoToNotValidExplicitDestinationTest.pdf";
        String outFile = destinationFolder + "remoteGoToNotValidExplicitDestinationTest.pdf";

        PdfDocument document = new PdfDocument(CompareTool.createTestPdfWriter(outFile));
        document.addNewPage();

        PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
        linkExplicitDest.setAction(PdfAction.createGoTo(PdfExplicitRemoteGoToDestination.createFit(1)));
        document.getFirstPage().addAnnotation(linkExplicitDest);
        document.close();

        assertNull(new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_"));
    }

    @Test
    public void copyNullDestination() throws IOException {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            pdfDocument.addNewPage();

            PdfDestination copiedDestination = pdfDocument.getCatalog()
                    .copyDestination(null, new HashMap<PdfPage, PdfPage>(), pdfDocument);

            // We expect null to be returned if the destination to be copied is null
            Assertions.assertNull(copiedDestination);
        }
    }
}
