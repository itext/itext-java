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

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PageLabelNumberingStyle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfStructureDestination;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.CaptionSide;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfUA2Test extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PdfUA2Test/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/PdfUA2Test/";
    public static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkXmpMetadataTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "xmpMetadataTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_xmpMetadataTest.pdf";
        String documentMetaData;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            byte[] byteMetaData = pdfDocument.getXmpMetadata();
            documentMetaData = new String(byteMetaData);
            document.add(paragraph);
        }
        // There is a typo in the specification and the Schema namespace must contain http
        Assertions.assertTrue(documentMetaData.contains("http://www.aiim.org/pdfua/ns/id/"));
        Assertions.assertTrue(documentMetaData.contains("pdfuaid:part=\"2\""));
        Assertions.assertTrue(documentMetaData.contains("pdfuaid:rev=\"2024\""));
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkRealContentTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "realContentTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_realContentTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Two-page paragraph test 1 part \n Two-page paragraph test 2 part")
                    .setFont(font)
                    .setMarginTop(730);
            document.add(paragraph);
            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();

            // We check that the paragraph remains one in the structure when it spans two pages.
            Assertions.assertEquals(1, structTreeRoot.getKids().get(0).getKids().size());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkArtifactTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "artifactTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_artifactTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Two-page paragraph test 1 part \n Two-page paragraph test 2 part")
                    .setFont(font)
                    .setMarginTop(730);
            paragraph.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
            document.add(paragraph);
            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();

            // We check that there are no children because the paragraph has the Artifact role, and it is not real content.
            Assertions.assertEquals(0, structTreeRoot.getKids().get(0).getKids().size());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkStructureTypeNamespaceTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "structureTypeNamespaceTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_structureTypeNamespaceTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            paragraph.getAccessibilityProperties().setRole("Custom Role");

            Exception e = Assertions.assertThrows(PdfException.class, ()-> document.add(paragraph));
            Assertions.assertEquals(MessageFormatUtil.format(
                    KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "Custom Role",
                    "http://iso.org/pdf2/ssn"), e.getMessage());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void addNamespaceRoleMappingTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "addNamespaceRoleMappingTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_addNamespaceRoleMappingTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);

            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            paragraph.getAccessibilityProperties().setRole("Custom Role");

            paragraph.getAccessibilityProperties().setNamespace(new PdfNamespace(StandardNamespaces.PDF_2_0));
            paragraph.getAccessibilityProperties().getNamespace().addNamespaceRoleMapping("Custom Role", StandardRoles.H3);
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkArticleTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "articleTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_articleTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            // Article creating
            Paragraph article = new Paragraph();
            article.getAccessibilityProperties().setRole(StandardRoles.ART).setNamespace(new PdfNamespace(
                    StandardNamespaces.PDF_1_7));

            // Adding Title into Article
            Text title = new Text("Title in Article Test");
            title.getAccessibilityProperties().setRole(StandardRoles.TITLE);
            article.add(title);
            document.add(article);

            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();
            IStructureNode articleNode =  structTreeRoot.getKids().get(0).getKids().get(0);
            Assertions.assertEquals(1, articleNode.getKids().size());
            String childElementSection = articleNode.getKids().get(0).getRole().toString();
            Assertions.assertEquals("/Title", childElementSection);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkSectionTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "sectionTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_sectionTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
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
            Assertions.assertEquals(1, sectionNode.getKids().size());
            String childElementSection = sectionNode.getKids().get(0).getRole().toString();
            Assertions.assertEquals("/H2", childElementSection);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkTableOfContentsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "tableOfContentsTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_tableOfContentsTestTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph tocTitle = new Paragraph("Table of Contents\n");
            tocTitle.getAccessibilityProperties().setRole(StandardRoles.TOC).setNamespace(new PdfNamespace(StandardNamespaces.PDF_1_7));
            Paragraph tociElement = new Paragraph("- TOCI element");
            tociElement.getAccessibilityProperties().setRole(StandardRoles.TOCI).setNamespace(new PdfNamespace(StandardNamespaces.PDF_1_7));
            Paragraph tociRef = new Paragraph("The referenced paragraph");
            document.add(tociRef);
            TagTreePointer pointer = new TagTreePointer(pdfDocument);
            pointer.moveToKid(StandardRoles.P);
            tociElement.getAccessibilityProperties().addRef(pointer);
            tocTitle.add(tociElement);
            document.add(tocTitle);

            pointer.moveToParent().moveToKid(StandardRoles.TOCI);
            // We check that TOCI contains the previously added Paragraph ref
            Assertions.assertEquals(1, pointer.getProperties().getRefsList().size());
            Assertions.assertEquals(StandardRoles.P, pointer.getProperties().getRefsList().get(0).getRole());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void createValidAsideTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "validAsideTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_validAsideTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            document.add(new Paragraph("Section 1:"));

            Paragraph section1Content = new Paragraph("Paragraph 1.1");

            Paragraph aside = new Paragraph("Additional content related to Section 1.");
            aside.getAccessibilityProperties().setRole(StandardRoles.ASIDE);
            section1Content.add(aside);
            document.add(section1Content);
            document.add(new Paragraph("Section 2:"));
            document.add(new Paragraph("Paragraph 2.1"));
            document.add(new Paragraph("Paragraph 2.2"));

            Paragraph aside2 = new Paragraph("Additional content related to Section 2.");
            aside2.getAccessibilityProperties().setRole(StandardRoles.ASIDE);
            document.add(aside2);

            document.add(new Paragraph("Section 3:"));
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkParagraphTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "paragraphTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_paragraphTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph p1 = new Paragraph("First P");
            document.add(p1);

            Paragraph p2 = new Paragraph("Second P");
            document.add(p2);

            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();
            Assertions.assertEquals("/P", structTreeRoot.getKids().get(0).getKids().get(0).getRole().toString());
            Assertions.assertEquals("/P", structTreeRoot.getKids().get(0).getKids().get(1).getRole().toString());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkHeadingTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "headingTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_headingTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
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
            Assertions.assertEquals("/H1", structTreeRoot.getKids().get(0).getKids().get(0).getRole().toString());
            Assertions.assertEquals("/H3", structTreeRoot.getKids().get(0).getKids().get(1).getRole().toString());
            Assertions.assertEquals("/H6", structTreeRoot.getKids().get(0).getKids().get(2).getRole().toString());
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkLabelTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "labelTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_labelTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Div lblStructure = new Div();
            lblStructure.getAccessibilityProperties().setRole(StandardRoles.LBL);
            Paragraph labelContent = new Paragraph("Label: ");
            lblStructure.add(labelContent);

            Paragraph targetContent = new Paragraph("Marked content");
            targetContent.getAccessibilityProperties().setActualText("Marked content");

            document.add(lblStructure);
            document.add(targetContent);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkLinkTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "linkTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_linkTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            PdfLinkAnnotation annotation1 = new PdfLinkAnnotation(new Rectangle(50, 50, 100, 100))
                    .setAction(PdfAction.createURI("http://itextpdf.com"));
            Link linkStructure1 = new Link("Link 1", annotation1);
            linkStructure1.getAccessibilityProperties().setRole(StandardRoles.LINK);
            linkStructure1.getAccessibilityProperties().setAlternateDescription("Alt text 1");
            document.add(new Paragraph(linkStructure1));

            PdfLinkAnnotation annotation2 = new PdfLinkAnnotation(new Rectangle(100, 100, 100, 100))
                    .setAction(PdfAction.createURI("http://apryse.com"));
            Link linkStructure2 = new Link("Link 2", annotation2);
            linkStructure2.getAccessibilityProperties().setRole(StandardRoles.LINK);
            linkStructure2.getAccessibilityProperties().setAlternateDescription("Alt text");
            document.add(new Paragraph(linkStructure2));
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkListTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "listTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_listTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            List list = new List(ListNumberingType.DECIMAL).setSymbolIndent(20).
                    add("One").add("Two").add("Three").add("Four").
                    add("Five").add("Six").add("Seven");
            document.add(list);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkTableTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "tableTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_tableTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);
            Table table = new Table(new float[]{1, 2, 2, 2});
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setWidth(200);
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Age");
            table.addCell("Country");

            for (int i = 1; i <= 10; i++) {
                table.addCell("ID: " + i);
                table.addCell("Name " + i);
                table.addCell("Age: " + (20 + i));
                table.addCell("Country " + i);
            }
            document.add(table);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkCaptionTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "captionTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_captionTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);
            Table table = new Table(new float[]{1, 2, 2});
            Paragraph caption = new Paragraph("This is Caption").setBackgroundColor(ColorConstants.GREEN);
            table.setCaption(new Div().add(caption));
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setWidth(200);
            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Age");

            for (int i = 1; i <= 5; i++) {
                table.addCell("ID: " + i);
                table.addCell("Name " + i);
                table.addCell("Age: " + (20 + i));
            }
            document.add(table);
            PdfStructTreeRoot structTreeRoot = pdfDocument.getStructTreeRoot();

            IStructureNode tableNode = structTreeRoot.getKids().get(0).getKids().get(0);
            String tableChildRole  =  tableNode.getKids().get(0).getRole().toString();
            Assertions.assertEquals("/Caption" , tableChildRole);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkCaptionPlacementInTreeStructure() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "checkCaptionPlacementInTreeStructure.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_checkCaptionPlacementInTreeStructure.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);
            Table tableCaptionBottom = new Table(new float[]{1, 2, 2});
            Paragraph caption = new Paragraph("This is Caption to the bottom").setBackgroundColor(ColorConstants.GREEN);
            tableCaptionBottom.setCaption(new Div().add(caption), CaptionSide.BOTTOM);
            tableCaptionBottom.setHorizontalAlignment(HorizontalAlignment.CENTER);
            tableCaptionBottom.setWidth(200);
            tableCaptionBottom.addHeaderCell("ID");
            tableCaptionBottom.addHeaderCell("Name");
            tableCaptionBottom.addHeaderCell("Age");

            for (int i = 1; i <= 5; i++) {
                tableCaptionBottom.addCell("ID: " + i);
                tableCaptionBottom.addCell("Name " + i);
                tableCaptionBottom.addCell("Age: " + (20 + i));
            }
            document.add(tableCaptionBottom);

            Table captionTopTable = new Table(new float[]{1,2,3});
            captionTopTable.setCaption(new Div().add(new Paragraph("Caption on top")));

           captionTopTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
           captionTopTable.setWidth(200);
           captionTopTable.addHeaderCell("ID");
           captionTopTable.addHeaderCell("Name");
           captionTopTable.addHeaderCell("Age");

            for (int i = 1; i <= 5; i++) {
               captionTopTable.addCell("ID: " + i);
               captionTopTable.addCell("Name " + i);
               captionTopTable.addCell("Age: " + (20 + i));
            }

            document.add(captionTopTable);
        }

        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkFigurePropertiesTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "figurePropertiesTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_figurePropertiesTest.pdf";


        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Div figureWithAltText = new Div().setWidth(100).setHeight(100);
            figureWithAltText.setBackgroundColor(ColorConstants.GREEN);
            figureWithAltText.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            figureWithAltText.getAccessibilityProperties().setAlternateDescription("Figure alt text");
            document.add(figureWithAltText);

            Div figureWithActualText = new Div().setWidth(100).setHeight(100);
            figureWithActualText.setBackgroundColor(ColorConstants.GREEN);
            figureWithActualText.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
            figureWithActualText.getAccessibilityProperties().setActualText("Figure actual ext");
            document.add(figureWithActualText);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkFormulaTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "formulaTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_formulaTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Div formulaStruct = new Div();
            formulaStruct.getAccessibilityProperties().setRole(StandardRoles.FORMULA);
            formulaStruct.getAccessibilityProperties().setAlternateDescription("Alt text");
            Paragraph formulaContent = new Paragraph("E=mc^2");
            formulaStruct.add(formulaContent);

            document.add(formulaStruct);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkBibliographicEntryTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "bibliographicEntryTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_bibliographicEntryTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph section = new Paragraph("Bibliography section:\n");
            section.getAccessibilityProperties().setRole(StandardRoles.SECT);
            Paragraph bibliography = new Paragraph("1. Author A. Title of Book. Publisher, Year.");
            bibliography.getAccessibilityProperties().setRole(StandardRoles.BIBENTRY).setNamespace(new PdfNamespace(
                    StandardNamespaces.PDF_1_7));
            section.add(bibliography);
            document.add(section);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkMetadataNoTitleTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaMetadataNoTitleTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
            XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
            pdfDocument.setXmpMetadata(xmpMeta);
            pdfDocument.setTagged();
            pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
            pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkMetadataDisplayDocTitleFalseTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaMetadataDisplayDocTitleFalseTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
            XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
            pdfDocument.setXmpMetadata(xmpMeta);
            pdfDocument.setTagged();
            pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(false));
            pdfDocument.getCatalog().setLang(new PdfString("en-US"));
            PdfDocumentInfo info = pdfDocument.getDocumentInfo();
            info.setTitle("PdfUA2 Title");
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkMetadataNoViewerPrefTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaMetadataNoViewerPrefTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
            XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
            pdfDocument.setXmpMetadata(xmpMeta);
            pdfDocument.setTagged();
            pdfDocument.getCatalog().setLang(new PdfString("en-US"));
            PdfDocumentInfo info = pdfDocument.getDocumentInfo();
            info.setTitle("PdfUA2 Title");
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkEmbeddedFileTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaEmbeddedFileTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaEmbeddedFileTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            new Document(pdfDocument).add(paragraph);
            PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, SOURCE_FOLDER + "sample.wav", "sample.wav", "sample", null, null);
            pdfDocument.addFileAttachment("specificname", spec);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkEmbeddedFileNoDescTest() throws IOException, XMPException {
        String outFile = DESTINATION_FOLDER + "pdfuaEmbeddedFileNoDescTest.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDocument, SOURCE_FOLDER + "sample.wav", "sample.wav", "sample", null, null);
            ((PdfDictionary) spec.getPdfObject()).remove(PdfName.Desc);
            pdfDocument.addFileAttachment("specificname", spec);
        }
        Assertions.assertNotNull(new VeraPdfValidator().validate(outFile));// Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void checkPageLabelTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaPageLabelTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaPageLabelTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            new Document(pdfDocument).add(paragraph);
            pdfPage.setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, null, 1);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkPageNumberAndLabelTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pdfuaPageNumLabelTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_pdfuaPageNumLabelTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            createSimplePdfUA2Document(pdfDocument);
            Document document = new Document(pdfDocument);
            PdfPage pdfPage = pdfDocument.addNewPage();
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            Paragraph paragraph = new Paragraph("Hello PdfUA2").setFont(font);
            document.add(paragraph);
            pdfPage.getPdfObject().getAsStream(PdfName.Contents).put(PdfName.PageNum, new PdfNumber(5));
            pdfPage.setPageLabel(PageLabelNumberingStyle.DECIMAL_ARABIC_NUMERALS, null, 5);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkStructureDestinationTest() throws IOException, InterruptedException, XMPException {
        String outFile = DESTINATION_FOLDER + "structureDestination01Test.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_structureDestination01Test.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Some text");
            document.add(paragraph);

            // Now add a link to the paragraph
            TagStructureContext context = pdfDocument.getTagStructureContext();
            TagTreePointer tagPointer = context.getAutoTaggingPointer();
            PdfStructElem structElem = context.getPointerStructElem(tagPointer);

            PdfLinkAnnotation linkExplicitDest = new PdfLinkAnnotation(new Rectangle(35, 785, 160, 15));
            PdfStructureDestination dest = PdfStructureDestination.createFit(structElem);
            PdfAction gotoStructAction = PdfAction.createGoTo(dest);
            gotoStructAction.put(PdfName.SD, dest.getPdfObject());
            linkExplicitDest.setAction(gotoStructAction);

            document.add(new AreaBreak());

            Link linkElem = new Link("Link to paragraph", linkExplicitDest);
            linkElem.getAccessibilityProperties().setRole(StandardRoles.LINK);
            linkElem.getAccessibilityProperties().setAlternateDescription("Some text");

            document.add(new Paragraph(linkElem));
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkOutlinesAsStructureDestinationsTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "checkOutlinesAsStructureDestinations.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_checkOutlinesAsStructureDestinations.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile,
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)))) {
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);

            createSimplePdfUA2Document(pdfDocument);

            PdfOutline topOutline = pdfDocument.getOutlines(false);
            PdfOutline header1Outline = topOutline.addOutline("header1 title");
            PdfAction action1 = PdfAction.createGoTo("header1");
            header1Outline.addAction(action1);

            PdfOutline header11Outline = header1Outline.addOutline("header1.1 title");
            PdfAction action11 = PdfAction.createGoTo("header1.1");
            header11Outline.addAction(action11);


            Paragraph header1 = new Paragraph("header1 text");
            header1.setProperty(Property.DESTINATION,
                    new Tuple2<String, PdfDictionary>("header1", action1.getPdfObject()));
            Paragraph header11 = new Paragraph("header1.1 text");
            header11.setProperty(Property.DESTINATION,
                    new Tuple2<String, PdfDictionary>("header1.1", action11.getPdfObject()));

            document.add(header1);
            document.add(header11);
        }

        compareAndValidate(outFile, cmpFile);

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(outFile))) {
            PdfOutline outline = pdfDocument.getOutlines(false);
            Assertions.assertEquals("header1", outline.getAllChildren().get(0)
                    .getDestination().getPdfObject().toString());
            Assertions.assertEquals("header1.1", outline.getAllChildren().get(0).getAllChildren().get(0)
                    .getDestination().getPdfObject().toString());
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

    private void compareAndValidate(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
