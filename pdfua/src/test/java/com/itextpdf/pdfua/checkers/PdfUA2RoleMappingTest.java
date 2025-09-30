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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.pdfua.PdfUA2TestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfUA2RoleMappingTest extends ExtendedITextTest {
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/PdfUA2RoleMappingTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    // Valid tests:

    @Test
    public void standardNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "standardNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace defaultNamespace = new PdfNamespace(StandardNamespaces.PDF_1_7);
        chapter.setNamespace(defaultNamespace);
        defaultNamespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN, namespace20);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(defaultNamespace);

        showText(chapter, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void mathMLNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "mathMLNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace mathMLNamespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(mathMLNamespace);
        mathMLNamespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(mathMLNamespace);

        showText(chapter, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void nonStandardNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem formula = doc.addKid(new PdfStructElem(pdfDoc, PdfName.Formula));
        PdfStructElem expression = formula.addKid(new PdfStructElem(pdfDoc, new PdfName("expression"), page1));

        PdfNamespace nonStandardNamespace = new PdfNamespace("http://www.w3.org/1999/xhtml");
        PdfNamespace mathMLNamespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        expression.setNamespace(nonStandardNamespace);
        nonStandardNamespace.addNamespaceRoleMapping("expression", StandardRoles.SPAN, mathMLNamespace);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(nonStandardNamespace);
        pdfDoc.getStructTreeRoot().addNamespace(mathMLNamespace);

        showText(expression, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void noExplicitNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "noExplicitNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", StandardRoles.SPAN);

        showText(chapter, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void nonStandardNamespaceTransitiveToStandardTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardNamespaceTransitiveToStandardTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1999/xhtml");
        chapter.setNamespace(namespace);

        PdfNamespace otherNamespace = new PdfNamespace("http://www.w3.org/2000/svg");
        namespace.addNamespaceRoleMapping("chapter", "chapterChild", otherNamespace);
        otherNamespace.addNamespaceRoleMapping("chapterChild", StandardRoles.SPAN);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(otherNamespace);

        showText(chapter, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void standardStructureTypeIsRemappedThroughNonStandardOneTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "stStructTypeRemappedNonStandardOne.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName(StandardRoles.SPAN), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping(StandardRoles.SPAN, "chapter", namespace20);
        namespace20.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);

        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace20);

        showText(chapter, page1);

        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    // UA-2 rule check: structure type with explicit namespace is role mapped to other structure type in the same NS:

    @Test
    public void mappingToTheSameNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "mappingToTheSameNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN, namespace);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        showText(chapter, page1);

        // VeraPDF: Structure type http://www.w3.org/1998/Math/MathML:chapter is role mapped to other structure type
        // in the same namespace
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                        STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName(), "chapter"), e.getMessage());
    }

    @Test
    public void transitiveMappingToTheSameNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "transitiveMappingToTheSameNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN, namespace20);
        namespace20.addNamespaceRoleMapping(StandardRoles.SPAN, StandardRoles.SPAN, namespace);

        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace20);

        showText(chapter, page1);

        // Structure type http://www.w3.org/1998/Math/MathML:chapter is role mapped to other structure type in the
        // same namespace
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                        STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName(), "chapter"), e.getMessage());
    }

    @Test
    public void structureTypesAreMappedToOtherWithinTheSameNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "structureTypesAreMappedToOtherWithinTheSameNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_1_7);
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        showText(chapter, page1);

        // VeraPDF: Structure type http://iso.org/pdf/ssn:chapter is role mapped to other structure type in the same
        // namespace
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                        STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName(), "chapter"), e.getMessage());
    }

    @Test
    public void standardStructureTypeIsRemappedThroughNonStandardOneInTheSameNSTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "standardStructureTypeIsRemappedThroughNonStandardOneInTheSameNSTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName(StandardRoles.SPAN), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping(StandardRoles.SPAN, "chapter", namespace);
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        showText(chapter, page1);

        // VeraPDF: Structure type http://www.w3.org/1998/Math/MathML:Span is role mapped to other structure type in
        // the same namespace
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                        STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName(), StandardRoles.SPAN), e.getMessage());
    }

    // Role is not mapped to any standard role:

    @Test
    public void notMappedToStandardNamespaceTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "notMappedToStandardNamespaceTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        showText(chapter, page1);

        // VeraPDF: Non-standard structure type chapter is not mapped to a standard type
        Exception e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter"), e.getMessage());
    }

    @Test
    public void nonStandardNamespaceTransitiveToNonStandardTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "nonStandardNamespaceTransitiveToNonStandardTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDoc.setTagged();
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1999/xhtml");
        chapter.setNamespace(namespace);

        PdfNamespace otherNamespace = new PdfNamespace("http://www.w3.org/2000/svg");
        namespace.addNamespaceRoleMapping("chapter", "chapterChild", otherNamespace);
        otherNamespace.addNamespaceRoleMapping("chapterChild", "chapterGrandchild");

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(otherNamespace);

        showText(chapter, page1);

        // VeraPDF: Non-standard structure type chapter is not mapped to a standard type
        Exception e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter",
                namespace.getNamespaceName()), e.getMessage());
    }

    @Test
    public void notMappedToStandardNamespaceButNotUsedTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "notMappedToStandardNamespaceButNotUsedTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));
        chapter.setNamespace(namespace);

        PdfNamespace mathML = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        namespace.addNamespaceRoleMapping("chapter", StandardRoles.SPAN);
        mathML.addNamespaceRoleMapping("notUsed", "non-standard", namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(mathML);

        showText(chapter, page1);

        // This case is valid according to VeraPDF.
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void circularMappingWithNonStandardTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "circularMappingWithNonStandardTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName("chapter"), page1));

        namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        chapter.setNamespace(namespace);

        PdfNamespace mathML = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        namespace.addNamespaceRoleMapping(StandardRoles.SPAN, "chapter", mathML);
        mathML.addNamespaceRoleMapping("chapter", StandardRoles.SPAN, namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        showText(chapter, page1);

        // VeraPDF: Non-standard structure type chapter is not mapped to a standard type
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(
                PdfUAExceptionMessageConstants.STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName() , "Span"), e.getMessage());
    }

    @Test
    public void circularMappingWithStandardTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "circularMappingWithStandardTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        paragraph.setNamespace(namespace);

        PdfNamespace namespace2 = new PdfNamespace(StandardNamespaces.PDF_1_7);

        namespace.addNamespaceRoleMapping(StandardRoles.P, StandardRoles.P, namespace2);
        namespace2.addNamespaceRoleMapping(StandardRoles.P, StandardRoles.P, namespace);

        pdfDoc.getStructTreeRoot().addNamespace(namespace);
        pdfDoc.getStructTreeRoot().addNamespace(namespace2);

        TagTreePointer tagPointer = new TagTreePointer(pdfDoc).setPageForTagging(page1).addTag(StandardRoles.P);

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.openTag(tagPointer.getTagReference())
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .closeTag();

        // A circular mapping exists for http://iso.org/pdf2/ssn:/P structure type
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(PdfUAExceptionMessageConstants.
                        STRUCTURE_TYPE_IS_ROLE_MAPPED_TO_OTHER_STRUCTURE_TYPE_IN_THE_SAME_NAMESPACE,
                namespace.getNamespaceName(), StandardRoles.P), e.getMessage());
    }

    @Test
    public void circularMappingLevel2Test() throws IOException {
        String outPdf = DESTINATION_FOLDER + "circularMappingLevel2Test.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();
        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace);

        PdfStructTreeRoot root = pdfDoc.getStructTreeRoot();
        root.addRoleMapping("chapter", "chapterChild");
        root.addRoleMapping("chapterChild", StandardRoles.SPAN);
        root.addRoleMapping(StandardRoles.SPAN, "chapter");

        // VeraPDF: A circular mapping exists for chapter structure type
        Exception e = Assertions.assertThrows(PdfException.class, () -> new TagTreePointer(pdfDoc)
                .setPageForTagging(page1).addTag("chapter"));
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "chapter",
                namespace.getNamespaceName()), e.getMessage());
    }

    @Test
    public void standardStructureTypeIsRemappedIntoNonStandardOneTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "standardStructureTypeIsRemappedIntoNonStandardOneTest.pdf";

        PdfUA2TestPdfDocument pdfDoc = new PdfUA2TestPdfDocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfPage page1 = pdfDoc.addNewPage();

        PdfStructElem doc = pdfDoc.getStructTreeRoot().addKid(new PdfStructElem(pdfDoc, PdfName.Document));
        PdfNamespace namespace20 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        doc.setNamespace(namespace20);

        PdfStructElem paragraph = doc.addKid(new PdfStructElem(pdfDoc, PdfName.P));
        PdfStructElem chapter = paragraph.addKid(new PdfStructElem(pdfDoc, new PdfName(StandardRoles.SPAN), page1));

        PdfNamespace namespace = new PdfNamespace("http://www.w3.org/1998/Math/MathML");
        chapter.setNamespace(namespace);
        namespace.addNamespaceRoleMapping(StandardRoles.SPAN, "chapter");

        pdfDoc.getStructTreeRoot().addNamespace(namespace20);
        pdfDoc.getStructTreeRoot().addNamespace(namespace);

        showText(chapter, page1);

        // VeraPDF: The standard structure type Span is remapped to a non-standard type
        Exception e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> pdfDoc.close());
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                StandardRoles.SPAN, namespace.getNamespaceName()), e.getMessage());
    }

    private static void showText(PdfStructElem chapter, PdfPage page1) {
        PdfMcr mcr = chapter.addKid(new PdfMcrNumber(page1, chapter));

        PdfCanvas canvas = new PdfCanvas(page1);

        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .restoreState()
                .closeTag();
    }
}
