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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfUARoleMappingTest extends ExtendedITextTest {
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUARoleMappingTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUARoleMappingTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void nonStandardMappingViaTagTreePointer1_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardMappingViaTagTreePointer1Test.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1);

        Exception e = Assertions.assertThrows(PdfException.class, () -> tagPointer.addTag("chapter"));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter"),
                e.getMessage());
    }

    @Test
    public void nonStandardMappingViaTagTreePointer2_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardMappingViaTagTreePointer2Test.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", "chapterGrandchild");

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1);

        Exception e = Assertions.assertThrows(PdfException.class, () -> tagPointer.addTag("chapter"));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter"),
                e.getMessage());
    }

    @Test
    public void nonStandardMappingViaTagTreePointer3_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardMappingViaTagTreePointer3Test.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1);

        // Although PDF/UA defines the nomenclature for heading levels above <H6> (<Hn>), these are not standard
        // structure types and therefore <Hn> tags must be role-mapped to a standard structure type.
        // According to PDF/UA-1, PDF/UA-conforming processors are expected to ignore such mappings and respect the heading level.
        Exception e = Assertions.assertThrows(PdfException.class, () -> tagPointer.addTag("H7"));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "H7"),
                e.getMessage());
    }

    @Test
    public void nonStandardMappingViaPdfName_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardMappingViaPdfNameTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);


        // Another attempts of PDF/UA document creation with non-standard tags see in PdfUACanvasTest class
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> canvas.openTag(new CanvasTag(new PdfName("chapter"))));
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.TAG_MAPPING_DOESNT_TERMINATE_WITH_STANDARD_TYPE, "chapter"),
                e.getMessage());
    }

    @Test
    public void nonStandardMappingViaPdfMcr_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardMappingViaPdfMcrTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));
        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page1, paragraph));

        PdfCanvas canvas = new PdfCanvas(page1);

        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> canvas.openTag(new CanvasTag(mcr)));
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.TAG_MAPPING_DOESNT_TERMINATE_WITH_STANDARD_TYPE, "chapter"),
                e.getMessage());
    }

    @Test
    public void standardMappingViaTagTreePointer_02_001_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "standardMappingViaTagTreePointerTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_standardMappingViaTagTreePointerTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", StandardRoles.SECT);

        PdfPage page1 = pdfDoc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page1);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc)
                .setPageForTagging(page1)
                .addTag("chapter");

        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .closeTag();

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.VERSION_INCOMPATIBILITY_FOR_DICTIONARY_ENTRY, count = 2, logLevel = LogLevelConstants.WARN)
    })
    public void standardMappingViaNamespace_02_001_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "standardMappingViaNamespaceTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        // Napespaces are actual only for PDF-2.0, which is actual only for PDF/UA-2
        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1999/xhtml");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        PdfMcr mcr = chapter.addKid(new PdfMcrNumber(page1, chapter));

        PdfCanvas canvas = new PdfCanvas(page1);

        // VeraPdf also complains about non-standard mapping
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> canvas.openTag(new CanvasTag(mcr)));
        Assertions.assertEquals(
                MessageFormatUtil.format(PdfUAExceptionMessageConstants.TAG_MAPPING_DOESNT_TERMINATE_WITH_STANDARD_TYPE, "chapter"),
                e.getMessage());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CANNOT_RESOLVE_ROLE_TOO_MUCH_TRANSITIVE_MAPPINGS, logLevel = LogLevelConstants.ERROR)
    })
    public void cycleMappingViaTagTreePointer1_02_003_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "cycleMappingViaTagTreePointer1Test.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", "chapter");

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1);

        Exception e = Assertions.assertThrows(PdfException.class, () -> tagPointer.addTag("chapter"));
        Assertions.assertEquals(
                MessageFormatUtil.format(KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter"),
                e.getMessage());
    }

    @Test
    public void cycleMappingViaTagTreePointer2_02_003_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "cycleMappingViaTagTreePointer2Test.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", StandardRoles.SPAN);
        root.addRoleMapping(StandardRoles.SPAN, "chapter");

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1).addTag("chapter");

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .closeTag();

        // VeraPdf complains about circular mapping
        Exception e = Assertions.assertThrows(PdfException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.ONE_OR_MORE_STANDARD_ROLE_REMAPPED, e.getMessage());
    }

    @Test
    public void mappingStandardRoles_02_004_Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "mappingStandardRolesTest.pdf";

        PdfUATestPdfDocument pdfDoc = new PdfUATestPdfDocument(
                new PdfWriter(outPdf));
        PdfPage page1 = pdfDoc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", StandardRoles.SPAN);
        root.addRoleMapping(StandardRoles.SPAN, StandardRoles.SECT);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1).addTag("chapter");

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .closeTag();

        // VeraPdf doesn't complain
        Exception e = Assertions.assertThrows(PdfException.class, () -> pdfDoc.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.ONE_OR_MORE_STANDARD_ROLE_REMAPPED, e.getMessage());
    }
}
