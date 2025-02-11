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
package com.itextpdf.kernel.pdf.copy;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfMarkupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfAnnotationCopyingTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/PdfAnnotationCopyingTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PdfAnnotationCopyingTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    @Disabled("Unignore when DEVSIX-3585 would be implemented")
    public void testCopyingPageWithAnnotationContainingPopupKey() throws IOException {
        String inFilePath = SOURCE_FOLDER + "annotation-with-popup.pdf";
        String outFilePath = DESTINATION_FOLDER + "copy-annotation-with-popup.pdf";
        PdfDocument originalDocument = new PdfDocument(new PdfReader(inFilePath));
        PdfDocument outDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFilePath));

        originalDocument.copyPagesTo(1, 1, outDocument);
        // During the second copy call we have to rebuild/preserve all the annotation relationship (Popup in this case),
        // so that we don't end up with annotation on one page referring to an annotation on another page as its popup
        // or as its parent
        originalDocument.copyPagesTo(1, 1, outDocument);

        originalDocument.close();
        outDocument.close();

        outDocument = new PdfDocument(new PdfReader(outFilePath));
        for (int pageNum = 1; pageNum <= outDocument.getNumberOfPages(); pageNum++) {
            PdfPage page = outDocument.getPage(pageNum);
            Assertions.assertEquals(2, page.getAnnotsSize());
            Assertions.assertEquals(2, page.getAnnotations().size());
            boolean foundMarkupAnnotation = false;
            for (PdfAnnotation annotation : page.getAnnotations()) {
                PdfDictionary annotationPageDict = annotation.getPageObject();
                if (annotationPageDict != null) {
                    Assertions.assertSame(page.getPdfObject(), annotationPageDict);
                }
                if (annotation instanceof PdfMarkupAnnotation) {
                    foundMarkupAnnotation = true;
                    PdfPopupAnnotation popup = ((PdfMarkupAnnotation) annotation).getPopup();
                    Assertions.assertTrue(page.containsAnnotation(popup), MessageFormatUtil.format(
                            "Popup reference must point to annotation present on the same page (# {0})", pageNum));
                    PdfDictionary parentAnnotation = popup.getParentObject();
                    Assertions.assertSame(annotation.getPdfObject(), parentAnnotation,
                            "Popup annotation parent must point to the annotation that specified it as Popup");
                }
            }
            Assertions.assertTrue(foundMarkupAnnotation, "Markup annotation expected to be present but not found");
        }
        outDocument.close();
    }

    @Test
    @Disabled("Unignore when DEVSIX-3585 would be implemented")
    public void testCopyingPageWithAnnotationContainingIrtKey() throws IOException {
        String inFilePath = SOURCE_FOLDER + "annotation-with-irt.pdf";
        String outFilePath = DESTINATION_FOLDER + "copy-annotation-with-irt.pdf";
        PdfDocument originalDocument = new PdfDocument(new PdfReader(inFilePath));
        PdfDocument outDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFilePath));

        originalDocument.copyPagesTo(1, 1, outDocument);
        // During the second copy call we have to rebuild/preserve all the annotation relationship (IRT in this case),
        // so that we don't end up with annotation on one page referring to an annotation on another page as its IRT
        // or as its parent
        originalDocument.copyPagesTo(1, 1, outDocument);

        originalDocument.close();
        outDocument.close();

        outDocument = new PdfDocument(new PdfReader(outFilePath));
        for (int pageNum = 1; pageNum <= outDocument.getNumberOfPages(); pageNum++) {
            PdfPage page = outDocument.getPage(pageNum);
            Assertions.assertEquals(4, page.getAnnotsSize());
            Assertions.assertEquals(4, page.getAnnotations().size());
            boolean foundMarkupAnnotation = false;
            for (PdfAnnotation annotation : page.getAnnotations()) {
                PdfDictionary annotationPageDict = annotation.getPageObject();
                if (annotationPageDict != null) {
                    Assertions.assertSame(page.getPdfObject(), annotationPageDict);
                }
                if (annotation instanceof PdfMarkupAnnotation) {
                    foundMarkupAnnotation = true;
                    PdfDictionary inReplyTo = ((PdfMarkupAnnotation) annotation).getInReplyToObject();
                    Assertions.assertTrue(page.containsAnnotation(PdfAnnotation.makeAnnotation(inReplyTo)),
                            "IRT reference must point to annotation present on the same page");
                }
            }
            Assertions.assertTrue(foundMarkupAnnotation, "Markup annotation expected to be present but not found");
        }
        outDocument.close();
    }

    @Test
    public void copySameLinksWithGoToSmartModeTest() throws IOException, InterruptedException {
        String cmpFilePath = SOURCE_FOLDER + "cmp_copySameLinksWithGoToSmartMode.pdf";
        String outFilePath = DESTINATION_FOLDER + "copySameLinksWithGoToSmartMode.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(outFilePath).setSmartMode(true);
        copyLinksGoToActionTest(writer, true, false);

        Assertions.assertNull(new CompareTool().compareByContent(outFilePath, cmpFilePath, DESTINATION_FOLDER));
    }

    @Test
    public void copyDiffDestLinksWithGoToSmartModeTest() throws IOException, InterruptedException {
        String cmpFilePath = SOURCE_FOLDER + "cmp_copyDiffDestLinksWithGoToSmartMode.pdf";
        String outFilePath = DESTINATION_FOLDER + "copyDiffDestLinksWithGoToSmartMode.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(outFilePath).setSmartMode(true);
        copyLinksGoToActionTest(writer, false, false);

        Assertions.assertNull(new CompareTool().compareByContent(outFilePath, cmpFilePath, DESTINATION_FOLDER));
    }

    @Test
    public void copyDiffDisplayLinksWithGoToSmartModeTest() throws IOException, InterruptedException {
        String cmpFilePath = SOURCE_FOLDER + "cmp_copyDiffDisplayLinksWithGoToSmartMode.pdf";
        String outFilePath = DESTINATION_FOLDER + "copyDiffDisplayLinksWithGoToSmartMode.pdf";

        PdfWriter writer = CompareTool.createTestPdfWriter(outFilePath).setSmartMode(true);
        copyLinksGoToActionTest(writer, false, true);

        Assertions.assertNull(new CompareTool().compareByContent(outFilePath, cmpFilePath, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)})
    public void copyPagesWithWidgetAnnotGoToActionExplicitDestTest() throws IOException, InterruptedException {
        String srcFilePath = SOURCE_FOLDER + "pageToCopyWithWidgetAnnotGoToActionExplicitDest.pdf";
        String cmpFilePath = SOURCE_FOLDER + "cmp_copyPagesWithWidgetAnnotGoToActionExplicitDest.pdf";
        String outFilePath = DESTINATION_FOLDER + "copyPagesWithWidgetAnnotGoToActionExplicitDest.pdf";

        copyPages(srcFilePath, outFilePath, cmpFilePath);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY)})
    public void copyPagesWithWidgetAnnotGoToActionNamedDestTest() throws IOException, InterruptedException {
        String srcFilePath = SOURCE_FOLDER + "pageToCopyWithWidgetAnnotGoToActionNamedDest.pdf";
        String cmpFilePath = SOURCE_FOLDER + "cmp_copyPagesWithWidgetAnnotGoToActionNamedDest.pdf";
        String outFilePath = DESTINATION_FOLDER + "copyPagesWithWidgetAnnotGoToActionNamedDest.pdf";

        copyPages(srcFilePath, outFilePath, cmpFilePath);
    }

    @Test
    public void copyPagesWithScreenAnnotGoToActionExplicitDestTest() throws IOException, InterruptedException {
        String srcFilePath = SOURCE_FOLDER + "pageToCopyWithScreenAnnotGoToActionExplicitDest.pdf";
        String cmpFilePath = SOURCE_FOLDER + "cmp_copyPagesWithScreenAnnotGoToActionExplicitDest.pdf";
        String outFilePath = DESTINATION_FOLDER + "copyPagesWithScreenAnnotGoToActionExplicitDest.pdf";

        copyPages(srcFilePath, outFilePath, cmpFilePath);
    }

    private void copyLinksGoToActionTest(PdfWriter writer, boolean isTheSameLinks, boolean diffDisplayOptions)
            throws IOException {
        PdfDocument destDoc = new PdfDocument(writer);
        ByteArrayOutputStream sourceBaos1 = createPdfWithGoToAnnot(isTheSameLinks, diffDisplayOptions);
        PdfDocument sourceDoc1 = new PdfDocument(new PdfReader(new ByteArrayInputStream(sourceBaos1.toByteArray())));

        sourceDoc1.copyPagesTo(1, sourceDoc1.getNumberOfPages(), destDoc);

        sourceDoc1.close();
        destDoc.close();
    }

    private void copyPages(String sourceFile, String outFilePath, String cmpFilePath)
            throws IOException, InterruptedException {
        PdfWriter writer = CompareTool.createTestPdfWriter(outFilePath);
        try (PdfDocument pdfDoc = new PdfDocument(writer)) {
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();

            try (PdfReader reader = new PdfReader(sourceFile);
                    PdfDocument srcDoc = new PdfDocument(reader)) {
                srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfDoc);
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFilePath, cmpFilePath, DESTINATION_FOLDER));
    }

    private ByteArrayOutputStream createPdfWithGoToAnnot(boolean isTheSameLink, boolean diffDisplayOptions) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(stream));

        pdfDocument.addNewPage();
        pdfDocument.addNewPage();
        pdfDocument.addNewPage();

        Rectangle linkLocation = new Rectangle(523, 770, 36, 36);
        PdfExplicitDestination destination = PdfExplicitDestination.createFit(pdfDocument.getPage(3));
        PdfAnnotation annotation = new PdfLinkAnnotation(linkLocation)
                .setAction(PdfAction.createGoTo(destination))
                .setBorder(new PdfArray(new int[]{0, 0, 1}));
        pdfDocument.getFirstPage().addAnnotation(annotation);

        if (!isTheSameLink) {
            destination = (diffDisplayOptions)
                    ? PdfExplicitDestination.create(pdfDocument.getPage(3), PdfName.XYZ, 350, 350,
                    0, 0, 1)
                    : PdfExplicitDestination.createFit(pdfDocument.getPage(1));
        }

        annotation = new PdfLinkAnnotation(linkLocation)
                .setAction(PdfAction.createGoTo(destination))
                .setBorder(new PdfArray(new int[]{0, 0, 1}));
        pdfDocument.getPage(2).addAnnotation(annotation);
        pdfDocument.close();

        return stream;
    }

}
