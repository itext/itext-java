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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

@Tag("IntegrationTest")
public class LayoutTaggingPdf2Test extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LayoutTaggingPdf2Test/";
    public static final String imageName = "Desert.jpg";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LayoutTaggingPdf2Test/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleDocDefault() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocDefault.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph h9 = new Paragraph("Header level 9");
        h9.getAccessibilityProperties().setRole("H9");

        Paragraph h11 = new Paragraph("Hello World from iText");
        h11.getAccessibilityProperties().setRole("H11");

        document.add(h9);
        addSimpleContentToDoc(document, h11);

        document.close();

        compareResult("simpleDocDefault");
    }

    @Test
    public void simpleDocNullNsByDefault() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocNullNsByDefault.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(null);

        Document document = new Document(pdfDocument);

        Paragraph h1 = new Paragraph("Header level 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText");

        document.add(h1);
        addSimpleContentToDoc(document, helloWorldPara);

        document.close();

        compareResult("simpleDocNullNsByDefault");
    }

    @Test
    public void simpleDocExplicitlyOldStdNs() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocExplicitlyOldStdNs.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        PdfNamespace namespace = tagsContext.fetchNamespace(StandardNamespaces.PDF_1_7);
        tagsContext.setDocumentDefaultNamespace(namespace);

        Document document = new Document(pdfDocument);

        Paragraph h1 = new Paragraph("Header level 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText");

        document.add(h1);
        addSimpleContentToDoc(document, helloWorldPara);

        document.close();

        compareResult("simpleDocExplicitlyOldStdNs");
    }

    @Test
    public void customRolesMappingPdf2() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "customRolesMappingPdf2.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();

        PdfNamespace stdNamespace2 = tagsContext.fetchNamespace(StandardNamespaces.PDF_2_0);
        PdfNamespace xhtmlNs = new PdfNamespace("http://www.w3.org/1999/xhtml");
        PdfNamespace html4Ns = new PdfNamespace("http://www.w3.org/TR/html4");


        String h9 = "H9";
        String h11 = "H11";

        // deliberately mapping to H9 tag
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.h1, h9, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, StandardRoles.P, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, StandardRoles.FIGURE, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, StandardRoles.L, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(StandardRoles.SPAN, HtmlRoles.span, xhtmlNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, StandardRoles.SPAN, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, StandardRoles.P, stdNamespace2);

        html4Ns.addNamespaceRoleMapping(HtmlRoles.center, HtmlRoles.center, xhtmlNs);

        // test some tricky mapping cases
        stdNamespace2.addNamespaceRoleMapping(h9, h11, stdNamespace2);
        stdNamespace2.addNamespaceRoleMapping(h11, h11, stdNamespace2);


        tagsContext.getAutoTaggingPointer().setNamespaceForNewTags(xhtmlNs);

        Document document = new Document(pdfDocument);

        addContentToDocInCustomNs(pdfDocument, stdNamespace2, xhtmlNs, html4Ns, h11, document);

        document.close();

        compareResult("customRolesMappingPdf2");
    }

    @Test
    public void customRolesMappingPdf17() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "customRolesMappingPdf17.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        PdfNamespace xhtmlNs = new PdfNamespace("http://www.w3.org/1999/xhtml");
        PdfNamespace html4Ns = new PdfNamespace("http://www.w3.org/TR/html4");


        String h9 = "H9";
        String h1 = StandardRoles.H1;

        // deliberately mapping to H9 tag
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.h1, h9);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, StandardRoles.P);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, StandardRoles.FIGURE);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, StandardRoles.L);
        xhtmlNs.addNamespaceRoleMapping(StandardRoles.SPAN, HtmlRoles.span, xhtmlNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, StandardRoles.SPAN);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, "Center");

        html4Ns.addNamespaceRoleMapping(HtmlRoles.center, HtmlRoles.center, xhtmlNs);

        // test some tricky mapping cases
        pdfDocument.getStructTreeRoot().addRoleMapping(h9, h1);
        pdfDocument.getStructTreeRoot().addRoleMapping(h1, h1);
        pdfDocument.getStructTreeRoot().addRoleMapping("Center", StandardRoles.P);
        pdfDocument.getStructTreeRoot().addRoleMapping("I", StandardRoles.SPAN);


        pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(null);
        pdfDocument.getTagStructureContext().getAutoTaggingPointer().setNamespaceForNewTags(xhtmlNs);

        Document document = new Document(pdfDocument);

        addContentToDocInCustomNs(pdfDocument, null, xhtmlNs, html4Ns, h1, document);

        document.close();

        compareResult("customRolesMappingPdf17");
    }

    @Test
    public void docWithExplicitAndImplicitDefaultNsAtTheSameTime() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithExplicitAndImplicitDefaultNsAtTheSameTime.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        tagsContext.setDocumentDefaultNamespace(null);
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardNamespaces.PDF_1_7);

        Document document = new Document(pdfDocument);

        Paragraph hPara = new Paragraph("This is header.");
        hPara.getAccessibilityProperties().setRole(StandardRoles.H);
        hPara.getAccessibilityProperties().setNamespace(explicitDefaultNs);
        document.add(hPara);


        PdfNamespace xhtmlNs = new PdfNamespace("http://www.w3.org/1999/xhtml");
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, StandardRoles.FIGURE, explicitDefaultNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, StandardRoles.L);


        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        img.getAccessibilityProperties().setRole(HtmlRoles.img);
        img.getAccessibilityProperties().setNamespace(xhtmlNs);
        document.add(img);

        List list = new List().setListSymbol("-> ");
        list.getAccessibilityProperties().setRole(HtmlRoles.ul);
        list.getAccessibilityProperties().setNamespace(xhtmlNs);
        list.add("list item").add("list item").add("list item").add("list item").add("list item");
        document.add(list);


        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, "Center", explicitDefaultNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, "Note", explicitDefaultNs);

        explicitDefaultNs.addNamespaceRoleMapping("Center", StandardRoles.P, explicitDefaultNs);
        explicitDefaultNs.addNamespaceRoleMapping("Note", "Note");

        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, "Note");
        pdfDocument.getStructTreeRoot().addRoleMapping("Note", StandardRoles.P);


        Paragraph centerPara = new Paragraph("centered text");
        centerPara.getAccessibilityProperties().setRole(HtmlRoles.center);
        centerPara.getAccessibilityProperties().setNamespace(xhtmlNs);

        Text simpleSpan = new Text("simple p with simple span");
        simpleSpan.getAccessibilityProperties().setRole(HtmlRoles.span);
        simpleSpan.getAccessibilityProperties().setNamespace(xhtmlNs);

        Paragraph simplePara = new Paragraph(simpleSpan);
        simplePara.getAccessibilityProperties().setRole(HtmlRoles.p);
        simplePara.getAccessibilityProperties().setNamespace(xhtmlNs);

        document.add(centerPara).add(simplePara);


        pdfDocument.getStructTreeRoot().addRoleMapping("I", StandardRoles.SPAN);


        Text iSpan = new Text("cursive span");
        iSpan.getAccessibilityProperties().setRole("I");
        document.add(new Paragraph(iSpan));


        document.close();

        compareResult("docWithExplicitAndImplicitDefaultNsAtTheSameTime");
    }

    @Test
    public void docWithInvalidMapping01() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping01.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        tagsContext.setDocumentDefaultNamespace(null);
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardNamespaces.PDF_1_7);

        try (Document document = new Document(pdfDocument)) {

            pdfDocument.getStructTreeRoot().addRoleMapping(HtmlRoles.p, StandardRoles.P);

            Paragraph customRolePara = new Paragraph("Hello world text.");
            customRolePara.getAccessibilityProperties().setRole(HtmlRoles.p);
            customRolePara.getAccessibilityProperties().setNamespace(explicitDefaultNs);

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> document.add(customRolePara)
            );
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                    "p", "http://iso.org/pdf/ssn"), e.getMessage());
        }
    }

    @Test
    public void docWithInvalidMapping02() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping02.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        tagsContext.setDocumentDefaultNamespace(null);
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardNamespaces.PDF_1_7);

        try (Document document = new Document(pdfDocument)) {

            explicitDefaultNs.addNamespaceRoleMapping(HtmlRoles.p, StandardRoles.P);

            Paragraph customRolePara = new Paragraph("Hello world text.");
            customRolePara.getAccessibilityProperties().setRole(HtmlRoles.p);

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> document.add(customRolePara)
            );
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "p"),
                    e.getMessage());
        }
    }

    @Test
    public void docWithInvalidMapping03() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping03.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        try (Document document = new Document(pdfDocument)) {

            Paragraph customRolePara = new Paragraph("Hello world text.");
            customRolePara.getAccessibilityProperties().setRole(HtmlRoles.p);

            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(customRolePara));
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                    "p", "http://iso.org/pdf2/ssn"), e.getMessage());
        }
    }

    @Test
    public void docWithInvalidMapping04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping04.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsCntxt = pdfDocument.getTagStructureContext();
        PdfNamespace stdNs2 = tagsCntxt.fetchNamespace(StandardNamespaces.PDF_2_0);
        // For /P elem a namespace is not explicitly specified, so PDF 1.7 namespace is used (see 14.8.6.1 of ISO 32000-2).
        // Mingling two standard namespaces in the same tag structure tree is valid in "core" PDF 2.0, however,
        // specifically the interaction between them will be addressed by ISO/TS 32005, which is currently still being drafted
        // (see DEVSIX-6676)
        stdNs2.addNamespaceRoleMapping(HtmlRoles.p, StandardRoles.P);


        Document document = new Document(pdfDocument);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.getAccessibilityProperties().setRole(HtmlRoles.p);
        document.add(customRolePara);

        document.close();

        compareResult("docWithInvalidMapping04");
    }

    @Test
    public void docWithInvalidMapping05() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping05.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        try (Document document = new Document(pdfDocument)) {

            // deliberately creating namespace via constructor instead of using TagStructureContext#fetchNamespace
            PdfNamespace stdNs2 = new PdfNamespace(StandardNamespaces.PDF_2_0);
            stdNs2.addNamespaceRoleMapping(HtmlRoles.p, StandardRoles.P, stdNs2);

            Paragraph customRolePara = new Paragraph("Hello world text.");
            customRolePara.getAccessibilityProperties().setRole(HtmlRoles.p);
            customRolePara.getAccessibilityProperties().setNamespace(stdNs2);
            document.add(customRolePara);

            Paragraph customRolePara2 = new Paragraph("Hello world text.");
            customRolePara2.getAccessibilityProperties().setRole(HtmlRoles.p);
            // not explicitly setting namespace that we've manually created. This will lead to the situation, when
            // /Namespaces entry in StructTreeRoot would have two different namespace dictionaries with the same name.
            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(customRolePara2));
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                    "p", "http://iso.org/pdf2/ssn"), e.getMessage());
        }
    }

    @Test
    public void docWithInvalidMapping06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping06.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);


        TagStructureContext tagCntxt = pdfDocument.getTagStructureContext();
        PdfNamespace pointerNs = tagCntxt.fetchNamespace(StandardNamespaces.PDF_2_0);
        pointerNs.addNamespaceRoleMapping(HtmlRoles.span, StandardRoles.SPAN, pointerNs);

        // deliberately creating namespace via constructor instead of using TagStructureContext#fetchNamespace
        PdfNamespace stdNs2 = new PdfNamespace(StandardNamespaces.PDF_2_0);
        stdNs2.addNamespaceRoleMapping(HtmlRoles.span, StandardRoles.EM, stdNs2);


        Text customRolePText1 = new Text("Hello world text 1.");
        customRolePText1.getAccessibilityProperties().setRole(HtmlRoles.span);
        customRolePText1.getAccessibilityProperties().setNamespace(stdNs2);
        document.add(new Paragraph(customRolePText1));

        Text customRolePText2 = new Text("Hello world text 2.");
        customRolePText2.getAccessibilityProperties().setRole(HtmlRoles.span);
        // not explicitly setting namespace that we've manually created. This will lead to the situation, when
        // /Namespaces entry in StructTreeRoot would have two different namespace dictionaries with the same name.
        document.add(new Paragraph(customRolePText2));

        document.close();

        compareResult("docWithInvalidMapping06");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CANNOT_RESOLVE_ROLE_IN_NAMESPACE_TOO_MUCH_TRANSITIVE_MAPPINGS, count = 1))
    public void docWithInvalidMapping07() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping07.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        try (Document document = new Document(pdfDocument)) {

            PdfNamespace stdNs2 = pdfDocument.getTagStructureContext().fetchNamespace(StandardNamespaces.PDF_2_0);
            int numOfTransitiveMappings = 120;
            String prevRole = HtmlRoles.span;
            for (int i = 0; i < numOfTransitiveMappings; ++i) {
                String nextRole = "span" + i;
                stdNs2.addNamespaceRoleMapping(prevRole, nextRole, stdNs2);
                prevRole = nextRole;
            }
            stdNs2.addNamespaceRoleMapping(prevRole, StandardRoles.SPAN, stdNs2);

            Text customRolePText1 = new Text("Hello world text.");
            customRolePText1.getAccessibilityProperties().setRole(HtmlRoles.span);
            customRolePText1.getAccessibilityProperties().setNamespace(stdNs2);

            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(new Paragraph(customRolePText1)));
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                    "span", "http://iso.org/pdf2/ssn"), e.getMessage());
        }
    }

    @Test
    public void docWithInvalidMapping08() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping08.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDocument.setTagged();

        try (Document document = new Document(pdfDocument)) {

            Paragraph h9Para = new Paragraph("Header level 9");
            h9Para.getAccessibilityProperties().setRole("H9");

            Exception e = Assertions.assertThrows(PdfException.class, () -> document.add(h9Para));
            Assertions.assertEquals(MessageFormat.format(LayoutExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE, "H9"), e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING))
    public void docWithInvalidMapping09() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping09.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        PdfNamespace ssn2 = tagsContext.fetchNamespace(StandardNamespaces.PDF_2_0);
        ssn2.addNamespaceRoleMapping(StandardRoles.DOCUMENT, "Book", ssn2);

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("hello world; root tag mapping"));

        document.close();

        compareResult("docWithInvalidMapping09");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CREATED_ROOT_TAG_HAS_MAPPING))
    public void docWithInvalidMapping10() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping10.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        PdfNamespace ssn2 = tagsContext.fetchNamespace(StandardNamespaces.PDF_2_0);
        ssn2.addNamespaceRoleMapping(StandardRoles.DOCUMENT, "Book", ssn2);
        ssn2.addNamespaceRoleMapping("Book", StandardRoles.PART, ssn2);

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("hello world; root tag mapping"));

        document.close();

        compareResult("docWithInvalidMapping10");
    }

    @Test
    public void stampTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDocOldStdNs.pdf"),
                new PdfWriter(destinationFolder + "stampTest01.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("stamped text"));
        document.close();

        compareResult("stampTest01");
    }

    @Test
    public void stampTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDocNoNs.pdf"),
                new PdfWriter(destinationFolder + "stampTest02.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("stamped text"));
        document.close();

        compareResult("stampTest02");
    }

    @Test
    public void stampTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDocNewStdNs.pdf"),
                new PdfWriter(destinationFolder + "stampTest03.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("stamped text"));
        document.close();

        compareResult("stampTest03");
    }

    @Test
    public void stampTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDoc1_7.pdf"),
                new PdfWriter(destinationFolder + "stampTest04.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new AreaBreak(AreaBreakType.NEXT_PAGE))
                .add(new Paragraph("stamped text"));
        document.close();

        compareResult("stampTest04");
    }

    @Test
    public void stampTest05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDocNewStdNs.pdf"),
                new PdfWriter(destinationFolder + "stampTest05.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        TagStructureContext tagCntxt = pdfDocument.getTagStructureContext();
        PdfNamespace xhtmlNs = tagCntxt.fetchNamespace("http://www.w3.org/1999/xhtml");
        PdfNamespace ssn2 = tagCntxt.fetchNamespace(StandardNamespaces.PDF_2_0);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, StandardRoles.L, ssn2);

        TagTreePointer pointer = new TagTreePointer(pdfDocument);
        pointer.moveToKid(StandardRoles.TABLE).moveToKid(StandardRoles.TR).moveToKid(1, StandardRoles.TD).moveToKid(StandardRoles.L);
        pointer.setRole(HtmlRoles.ul).getProperties().setNamespace(xhtmlNs);

        pdfDocument.close();

        compareResult("stampTest05");
    }

    @Test
    public void copyTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument srcPdf = new PdfDocument(new PdfReader(sourceFolder + "simpleDocNewStdNs.pdf"));
        PdfDocument outPdf = new PdfDocument(new PdfWriter(destinationFolder + "copyTest01.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        outPdf.setTagged();
        srcPdf.copyPagesTo(1, 1, outPdf);

        srcPdf.close();
        outPdf.close();

        compareResult("copyTest01");
    }

    @Test
    public void docWithSectInPdf2() throws IOException, ParserConfigurationException, SAXException,
            InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithSectInPdf2.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Div section = new Div();
        section.getAccessibilityProperties().setRole(StandardRoles.SECT);
        Paragraph h1 = new Paragraph("This is a header");
        h1.getAccessibilityProperties().setRole("H1");
        section.add(h1);

        section.add(new Paragraph("This is a paragraph."));
        Paragraph para = new Paragraph("This is another paragraph, ");

        Text emphasised = new Text("with semantic emphasis!");
        emphasised.setUnderline();
        emphasised.getAccessibilityProperties().setRole(StandardRoles.EM);
        para.add(emphasised);
        section.add(para);

        document.add(section);
        document.close();

        compareResult("docWithSectInPdf2");
    }

    @Test
    public void copyTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument srcPdf = new PdfDocument(new PdfReader(sourceFolder + "docSeveralNs.pdf"));
        PdfDocument outPdf = new PdfDocument(new PdfWriter(destinationFolder + "copyTest02.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        outPdf.setTagged();
        srcPdf.copyPagesTo(1, 1, outPdf);

        srcPdf.close();
        outPdf.close();

        compareResult("copyTest02");
    }

    private static class HtmlRoles {
        static String h1 = "h1";
        static String p = "p";
        static String img = "img";
        static String ul = "ul";
        static String center = "center";
        static String span = "span";
    }

    private void addSimpleContentToDoc(Document document, Paragraph p2) throws MalformedURLException {
        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();

        for (int k = 0; k < 5; k++) {
            table.addCell(p2);

            List list = new List().setListSymbol("-> ");
            list.add("list item").add("list item").add("list item").add("list item").add("list item");
            Cell cell = new Cell().add(list);
            table.addCell(cell);

            Cell c = new Cell().add(img);
            table.addCell(c);

            Table innerTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
            int j = 0;
            while (j < 9) {
                innerTable.addCell("Hi");
                j++;
            }
            table.addCell(innerTable);
        }

        document.add(table);
    }

    private void addContentToDocInCustomNs(PdfDocument pdfDocument, PdfNamespace defaultNamespace, PdfNamespace xhtmlNs, PdfNamespace html4Ns,
                                           String hnRole,
                                           Document document) throws MalformedURLException {
        Paragraph h1P = new Paragraph("Header level 1");
        h1P.getAccessibilityProperties().setRole(HtmlRoles.h1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText");
        helloWorldPara.getAccessibilityProperties().setRole(HtmlRoles.p);

        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        img.getAccessibilityProperties().setRole(HtmlRoles.img);

        document.add(h1P);
        document.add(helloWorldPara);
        document.add(img);

        pdfDocument.getTagStructureContext().getAutoTaggingPointer().setNamespaceForNewTags(defaultNamespace);

        List list = new List().setListSymbol("-> ");
        list.getAccessibilityProperties().setRole(HtmlRoles.ul);
        list.getAccessibilityProperties().setNamespace(xhtmlNs);
        list.add("list item").add("list item").add("list item").add("list item").add(new ListItem("list item"));
        document.add(list);

        Paragraph center = new Paragraph("centered text").setTextAlignment(TextAlignment.CENTER);
        center.getAccessibilityProperties().setRole(HtmlRoles.center);
        center.getAccessibilityProperties().setNamespace(html4Ns);
        document.add(center);

        Paragraph h11Para = new Paragraph("Heading level 11");
        h11Para.getAccessibilityProperties().setRole(hnRole);
        document.add(h11Para);

        if (defaultNamespace == null) {
            Text i = new Text("italic text");
            i.getAccessibilityProperties().setRole("I");
            Paragraph pi = new Paragraph(i.setItalic());
            document.add(pi);
        }
    }

    private void compareResult(String testName)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = testName + ".pdf";
        String cmpFileName = "cmp_" + outFileName;

        CompareTool compareTool = new CompareTool();
        String outPdf = destinationFolder + outFileName;
        String cmpPdf = sourceFolder + cmpFileName;

        String contentDifferences = compareTool.compareByContent(outPdf,
                cmpPdf, destinationFolder, testName + "Diff_");
        String taggedStructureDifferences = compareTool.compareTagStructures(outPdf, cmpPdf);

        String errorMessage = "";
        errorMessage += taggedStructureDifferences == null ? "" : taggedStructureDifferences + "\n";
        errorMessage += contentDifferences == null ? "" : contentDifferences;
        if (!errorMessage.isEmpty()) {
            Assertions.fail(errorMessage);
        }
    }
}
