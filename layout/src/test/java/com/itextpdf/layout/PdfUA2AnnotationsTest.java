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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.Pdf3DAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfInkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfRedactAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTrapNetworkAnnotation;
import com.itextpdf.kernel.pdf.annot.da.AnnotationDefaultAppearance;
import com.itextpdf.kernel.pdf.annot.da.StandardAnnotationFont;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfUA2AnnotationsTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PdfUA2AnnotationsTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/PdfUA2AnnotationsTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfUA2LinkAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaLinkAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaLinkAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Link link = createLinkAnnotation();
            Paragraph paragraph = new Paragraph();
            paragraph.setFont(font);
            paragraph.add(link);
            new Document(pdfDocument).add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }


    @Test
    public void pdfUA2LinkAnnotNoAltTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaLinkAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Link link = createLinkAnnotation();
            link.getLinkAnnotation().getPdfObject().remove(PdfName.Contents);
            Paragraph paragraph = new Paragraph();
            paragraph.setFont(font);
            paragraph.add(link);
            new Document(pdfDocument).add(paragraph);
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2FileAttachmentAnnotTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaFileAttachmentAnnotTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaFileAttachmentAnnotTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            PdfPage pdfPage = pdfDocument.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            createSimplePdfUA2Document(pdfDocument);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(StandardCharsets.UTF_8), "description", "file.txt", null, null, null);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            annot.setContents("Hello world");
            annot.getPdfObject().put(PdfName.Type, PdfName.Annot);
            pdfPage.addAnnotation(annot);

            PdfFormXObject xObject = new PdfFormXObject(rect);
            annot.setNormalAppearance(xObject.getPdfObject());

            pdfPage.addAnnotation(annot);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2FileAttachmentAnnotNoAFRelTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaFileAttachmentAnnotNoArtifactTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            PdfPage pdfPage = pdfDocument.addNewPage();
            Rectangle rect = new Rectangle(100, 650, 400, 100);
            createSimplePdfUA2Document(pdfDocument);
            PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                    pdfDocument, "file".getBytes(StandardCharsets.UTF_8), "description", "file.txt", null, null, null);
            ((PdfDictionary) fs.getPdfObject()).remove(PdfName.AFRelationship);
            PdfFileAttachmentAnnotation annot = new PdfFileAttachmentAnnotation(rect, fs);
            annot.setContents("Hello world");
            pdfPage.addAnnotation(annot);
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void pdfUA2RubberStampAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaRubberstampAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaRubberstampAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            stamp.setContents("stamp contents");
            stamp.getPdfObject().put(PdfName.Type, PdfName.Annot);
            pdfDocument.getTagStructureContext().getAutoTaggingPointer().addTag(StandardRoles.SECT);
            pdfPage.addAnnotation(stamp);
            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2RubberStampNoContentsAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaRubberstampAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfStampAnnotation stamp = new PdfStampAnnotation(new Rectangle(0, 0, 100, 50));
            stamp.setStampName(PdfName.Approved);
            pdfPage.addAnnotation(stamp);
            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2ScreenAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaScreenAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("screen annotation");
            pdfPage.addAnnotation(screen);
            pdfPage.flush();
        }
        Assertions.assertNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2ScreenNoContentsAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaScreenNoContentsAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            pdfPage.addAnnotation(screen);
            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2InkAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaInkAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaInkAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfInkAnnotation ink = createInkAnnotation();
            pdfPage.addAnnotation(ink);

            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2InkAnnotationsNoContentTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaInkAnnotationNoContentTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfInkAnnotation ink = createInkAnnotation();
            ink.getPdfObject().remove(PdfName.Contents);
            pdfPage.addAnnotation(ink);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2RedactionAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaRedactionAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaRedactionAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfRedactAnnotation redact = createRedactionAnnotation();
            pdfPage.addAnnotation(redact);

            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2RedactionNoContentsAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaRedactionNoContentsAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfRedactAnnotation redact = createRedactionAnnotation();
            redact.getPdfObject().remove(PdfName.Contents);

            pdfPage.addAnnotation(redact);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA23DAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfua3DAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfua3DAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            Pdf3DAnnotation annot = create3DAnnotation();
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA23DNoContentsAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfua3DNoContentsAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            Pdf3DAnnotation annot = create3DAnnotation();
            annot.getPdfObject().remove(PdfName.Contents);
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2RichMediaAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaRichMediaAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaRichMediaAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2RichMediaNoContentsAnnotationsTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaRichMediaNoContentsAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            annot.getPdfObject().remove(PdfName.Contents);
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2ContentsRCTheSameTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaRcContentAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaRcContentAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfTextAnnotation annot = createRichTextAnnotation();
            annot.setRichText(new PdfString("Rich media annot"));
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void pdfUA2NotAllowedTrapNetAnnotationTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfua2TrapNetAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfFormXObject form = new PdfFormXObject(PageSize.A4);
            PdfCanvas canvas = new PdfCanvas(form, pdfDocument);
            canvas
                    .saveState()
                    .circle(272, 795, 5)
                    .setColor(ColorConstants.GREEN, true)
                    .fill()
                    .restoreState();
            canvas.release();
            form.setProcessColorModel(PdfName.DeviceN);
            PdfTrapNetworkAnnotation annot = new PdfTrapNetworkAnnotation(PageSize.A4, form);
            pdfPage.addAnnotation(annot);
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2NotAllowedSoundAnnotationTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfua2SoundAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
            pdfPage.addAnnotation(annot);
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2AltContentDiffAnnotationTest()
            throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfua2ArtifactsAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Link link = createLinkAnnotation();
            link.getAccessibilityProperties().setAlternateDescription("some description");
            Paragraph paragraph = new Paragraph();
            paragraph.setFont(font);
            paragraph.add(link);
            new Document(pdfDocument).add(paragraph);
        }

        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void pdfUA2TabAnnotationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaMultipleAnnotsTabAnnotationTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaMultipleAnnotsTabAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));
            screen.setContents("screen annotation");
            pdfDocument.addNewPage().addAnnotation(screen);
            pdfDocument.addNewPage().addAnnotation(screen);
            pdfDocument.addNewPage().addAnnotation(screen);

            for (int i = 0; i < pdfDocument.getNumberOfPages(); i++) {
                PdfDictionary pageObject = pdfDocument.getPage(i+1).getPdfObject();
                Assertions.assertTrue(pageObject.containsKey(PdfName.Tabs));
                PdfObject pageT = pageObject.get(PdfName.Tabs);
                Assertions.assertEquals(PdfName.S, pageT);
            }
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void annotationInvisibleButNoArtifactTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaInvisibleAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfAnnotation annot = createRichTextAnnotation();
            annot.setFlags(PdfAnnotation.INVISIBLE);
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void annotationNoViewButNoArtifactTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaNoViewAnnotationTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfAnnotation annot = createRichTextAnnotation();
            annot.setFlags(PdfAnnotation.NO_VIEW);
            pdfPage.addAnnotation(annot);

            pdfPage.flush();
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    private void createSimplePdfUA2Document(PdfDocument pdfDocument) throws IOException, XMPException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        pdfDocument.setXmpMetadata(xmpMeta);
        pdfDocument.setTagged();
        pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("PdfUA2 Title");
    }

    private void compareAndValidate(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    private Link createLinkAnnotation() {
        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfLinkAnnotation annot = new PdfLinkAnnotation(rect).setAction(
                PdfAction.createURI("https://itextpdf.com/"));
        annot.setContents("link annot");
        Link link = new Link("Link to iText",
                annot);
        link.getAccessibilityProperties().setRole(StandardRoles.LINK);
        return link;
    }

    private PdfTextAnnotation createRichTextAnnotation() {
        PdfTextAnnotation annot = new PdfTextAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setContents("Rich media annot");
        PdfDictionary annotPdfObject = annot.getPdfObject();
        annotPdfObject.put(PdfName.Subtype, PdfName.RichMedia);
        return annot;
    }

    private Pdf3DAnnotation create3DAnnotation() {
        PdfStream stream3D = new PdfStream();
        stream3D.put(PdfName.Type, PdfName._3D);
        stream3D.put(PdfName.Subtype, new PdfName("U3D"));
        stream3D.setCompressionLevel(CompressionConstants.UNDEFINED_COMPRESSION);

        Pdf3DAnnotation annot = new Pdf3DAnnotation(new Rectangle(300, 300, 100, 50), stream3D);

        PdfDictionary dict3D = new PdfDictionary();
        dict3D.put(PdfName.Type, PdfName._3DView);
        dict3D.put(new PdfName("XN"), new PdfString("Default"));
        dict3D.put(new PdfName("IN"), new PdfString("Unnamed"));
        dict3D.put(new PdfName("MS"), PdfName.M);
        dict3D.put(new PdfName("C2W"),
                new PdfArray(new float[] {1, 0, 0, 0, 0, -1, 0, 1, 0, 3, -235, 28}));
        dict3D.put(PdfName.CO, new PdfNumber(235));

        annot.setDefaultInitialView(dict3D);
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setAppearance(PdfName.N, new PdfStream());
        annot.setContents("3D annot");
        return annot;
    }

    private PdfInkAnnotation createInkAnnotation() {
        float[] array1 = {100, 100, 100, 200, 200, 200, 300, 300};
        PdfArray firstPoint = new PdfArray(array1);

        PdfArray resultArray = new PdfArray();
        resultArray.add(firstPoint);

        PdfDictionary borderStyle = new PdfDictionary();
        borderStyle.put(PdfName.Type, PdfName.Border);
        borderStyle.put(PdfName.W, new PdfNumber(3));

        PdfInkAnnotation ink = new PdfInkAnnotation(new Rectangle(0, 0, 575, 842), resultArray);
        ink.setBorderStyle(borderStyle);
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        ink.setColor(colors);
        ink.setContents("ink annotation");

        return ink;
    }

    private PdfRedactAnnotation createRedactionAnnotation() {
        PdfRedactAnnotation redact = new PdfRedactAnnotation(new Rectangle(0, 0, 100, 50))
                .setDefaultAppearance(new AnnotationDefaultAppearance()
                        .setColor(DeviceCmyk.MAGENTA)
                        .setFont(StandardAnnotationFont.CourierOblique)
                        .setFontSize(20))
                .setOverlayText(new PdfString("Redact CMYK courier-oblique"));

        redact.setContents("redact annotation");
        return redact;
    }
}
