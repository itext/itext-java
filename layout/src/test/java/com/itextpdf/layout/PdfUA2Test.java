/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;


import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfUA2Test extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PdfUA2Test/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/PdfUA2Test/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }


    @Test
    public void checkXmpMetadataTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "xmpMetadataTest.pdf";
        String documentMetaData;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            byte[] byteMetaData = pdfDocument.getXmpMetadata();
            documentMetaData = new String(byteMetaData);
            document.add(paragraph);
        }
        // There is a typo in the specification and the Schema namespace must contain http
        Assert.assertTrue(documentMetaData.contains("http://www.aiim.org/pdfua/ns/id/"));
        Assert.assertTrue(documentMetaData.contains("pdfuaid:part=\"2\""));
        Assert.assertTrue(documentMetaData.contains("pdfuaid:rev=\"2024\""));
    }

    @Test
    public void checkRealContentTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "realContentTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Two-page paragraph test 1 part \n Two-page paragraph test 2 part")
                    .setFont(font)
                    .setMarginTop(730);
            document.add(paragraph);
            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();

            // We check that the paragraph remains one in the structure when it spans two pages.
            Assert.assertEquals(1, structTreeRoot.getKids().get(0).getKids().size());
        }
    }

    @Test
    public void checkArtifactTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "realContentTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Two-page paragraph test 1 part \n Two-page paragraph test 2 part")
                    .setFont(font)
                    .setMarginTop(730);
            paragraph.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            document.add(paragraph);
            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();

            // We check that there are no children because the paragraph has the Artifact role, and it is not real content.
            Assert.assertEquals(0, structTreeRoot.getKids().get(0).getKids().size());
        }
    }

    @Test
    public void checkStructureTypeNamespaceTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "structureTypeNamespaceTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            paragraph.getAccessibilityProperties().setRole("Custom Role");

            Exception e = Assert.assertThrows(PdfException.class, ()-> document.add(paragraph));
            Assert.assertEquals(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "Custom Role",
                    "http://iso.org/pdf2/ssn"), e.getMessage());
        }
    }

    @Test
    public void checkSectionTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "sectionTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            // Section creating
            Paragraph section = new Paragraph();
            section.getAccessibilityProperties().setRole(StandardRoles.SECT);

            // Adding heading into Section
            Text headingText = new Text("Heading text in Section");
            headingText.getAccessibilityProperties().setRole(StandardRoles.H2);
            section.add(headingText);
            document.add(section);

            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();
            IStructureNode sectionNode =  structTreeRoot.getKids().get(0).getKids().get(0);
            Assert.assertEquals(1, sectionNode.getKids().size());
            String childElementSection = sectionNode.getKids().get(0).getRole().toString();
            Assert.assertEquals("/H2", childElementSection);
        }
    }

    @Test
    public void checkParagraphTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "paragraphTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph p1 = new Paragraph("First P");
            document.add(p1);

            Paragraph p2 = new Paragraph("Second P");
            document.add(p2);

            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();
            Assert.assertEquals("/P", structTreeRoot.getKids().get(0).getKids().get(0).getRole().toString());
            Assert.assertEquals("/P", structTreeRoot.getKids().get(0).getKids().get(1).getRole().toString());
        }
    }

    @Test
    public void checkHeadingTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "headingTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            // For PdfUA2 we shall not use the H structure type
            Paragraph h1 = new Paragraph("H1 text");
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            document.add(h1);

            Paragraph h3 = new Paragraph("H3 text");
            h3.getAccessibilityProperties().setRole(StandardRoles.H3);
            document.add(h3);

            Paragraph h6 = new Paragraph("H6 text");
            h6.getAccessibilityProperties().setRole(StandardRoles.H6);
            document.add(h6);

            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();
            Assert.assertEquals("/H1", structTreeRoot.getKids().get(0).getKids().get(0).getRole().toString());
            Assert.assertEquals("/H3", structTreeRoot.getKids().get(0).getKids().get(1).getRole().toString());
            Assert.assertEquals("/H6", structTreeRoot.getKids().get(0).getKids().get(2).getRole().toString());
        }
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
}
