package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardStructureNamespace;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXException;

@Category(IntegrationTest.class)
public class AutoTaggingPdf2Test extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/AutoTaggingPdf2Test/";
    public static final String imageName = "Desert.jpg";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/AutoTaggingPdf2Test/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    // TODO {1}
    public void simpleDocDefault() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocDefault.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph h9 = new Paragraph("Header level 9");
        h9.setRole(new PdfName("H9"));

        Paragraph h11 = new Paragraph("Hello World from iText7");
        h11.setRole(new PdfName("H11"));

        document.add(h9);
        addSimpleContentToDoc(document, h11);

        document.close();

        compareResult("simpleDocDefault");
    }

    @Test
    // TODO {0}
    public void simpleDocNullNsByDefault() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocNullNsByDefault.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        pdfDocument.getTagStructureContext().setDocumentDefaultNamespace(null);

        Document document = new Document(pdfDocument);

        // TODO test this along with 1.7 doc, also with standard namespace of 1.7 explicitly set
//        boolean expectedExcThrown = true;
//        try {
//            Paragraph h9 = new Paragraph("Header level 9");
//            h9.setRole(new PdfName("H9"));
//            document.add(h9);
//            expectedExcThrown = false;
//        } catch (PdfException ex) {
//            if (!MessageFormat.format(PdfException.RoleIsNotMappedToAnyStandardRole, "/H9").equals(ex.getMessage())) {
//                expectedExcThrown = false;
//            }
//        }
//        if (!expectedExcThrown) {
//            Assert.fail("Expected exception was not thrown.");
//        }

        Paragraph h1 = new Paragraph("Header level 1");
        h1.setRole(PdfName.H1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText7");

        document.add(h1);
        addSimpleContentToDoc(document, helloWorldPara);

        document.close();

        compareResult("simpleDocNullNsByDefault");
    }

    @Test
    // TODO {0}
    public void simpleDocExplicitlyOldStdNs() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "simpleDocExplicitlyOldStdNs.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        PdfNamespace namespace = tagsContext.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_1_7);
        tagsContext.setDocumentDefaultNamespace(namespace);

        Document document = new Document(pdfDocument);

        Paragraph h1 = new Paragraph("Header level 1");
        h1.setRole(PdfName.H1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText7");

        document.add(h1);
        addSimpleContentToDoc(document, helloWorldPara);

        document.close();

        compareResult("simpleDocExplicitlyOldStdNs");
    }

    @Test
    // TODO {2}
    public void customRolesMappingPdf2() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "customRolesMappingPdf2.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();

        // TODO still attributes applier didn't apply L unordered type for the list when it's mapped
            // actually, because at this moment namespace in auto tagging pointer is not std2

        // TODO would be nice to acquire it somewhere specific to has always same object
        PdfNamespace stdNamespace2 = tagsContext.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        PdfNamespace xhtmlNs = new PdfNamespace("http://www.w3.org/1999/xhtml");
        PdfNamespace html4Ns = new PdfNamespace("http://www.w3.org/TR/html4");


        PdfName h9 = new PdfName("H9");
        PdfName h11 = new PdfName("H11");

        // deliberately mapping to H9 tag
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.h1, h9, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, PdfName.P, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, PdfName.Figure, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, PdfName.L, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(PdfName.Span, HtmlRoles.span, xhtmlNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, PdfName.Span, stdNamespace2);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, PdfName.P, stdNamespace2);

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


        PdfName h9 = new PdfName("H9");
        PdfName h1 = PdfName.H1;

        // deliberately mapping to H9 tag
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.h1, h9);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, PdfName.P);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, PdfName.Figure);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, PdfName.L);
        xhtmlNs.addNamespaceRoleMapping(PdfName.Span, HtmlRoles.span, xhtmlNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, PdfName.Span);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, PdfName.Center);

        html4Ns.addNamespaceRoleMapping(HtmlRoles.center, HtmlRoles.center, xhtmlNs);

        // test some tricky mapping cases
        pdfDocument.getStructTreeRoot().addRoleMapping(h9, h1);
        pdfDocument.getStructTreeRoot().addRoleMapping(h1, h1);
        pdfDocument.getStructTreeRoot().addRoleMapping(PdfName.Center, PdfName.P);
        pdfDocument.getStructTreeRoot().addRoleMapping(PdfName.I, PdfName.Span);


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
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_1_7);

        Document document = new Document(pdfDocument);

        Paragraph hPara = new Paragraph("This is header.");
        hPara.setRole(PdfName.H);
        hPara.getAccessibilityProperties().setNamespace(explicitDefaultNs);
        document.add(hPara);


        PdfNamespace xhtmlNs = new PdfNamespace("http://www.w3.org/1999/xhtml");
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.img, PdfName.Figure, explicitDefaultNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, PdfName.L);


        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        img.setRole(HtmlRoles.img);
        img.getAccessibilityProperties().setNamespace(xhtmlNs);
        document.add(img);

        List list = new List().setListSymbol("-> ");
        list.setRole(HtmlRoles.ul);
        list.getAccessibilityProperties().setNamespace(xhtmlNs);
        list.add("list item").add("list item").add("list item").add("list item").add("list item");
        document.add(list);


        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.center, PdfName.Center, explicitDefaultNs);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.p, PdfName.Note, explicitDefaultNs);

        explicitDefaultNs.addNamespaceRoleMapping(PdfName.Center, PdfName.P, explicitDefaultNs);
        explicitDefaultNs.addNamespaceRoleMapping(PdfName.Note, PdfName.Note);

        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.span, PdfName.Note);
        pdfDocument.getStructTreeRoot().addRoleMapping(PdfName.Note, PdfName.P);


        Paragraph centerPara = new Paragraph("centered text");
        centerPara.setRole(HtmlRoles.center);
        centerPara.getAccessibilityProperties().setNamespace(xhtmlNs);

        Text simpleSpan = new Text("simple p with simple span");
        simpleSpan.setRole(HtmlRoles.span);
        simpleSpan.getAccessibilityProperties().setNamespace(xhtmlNs);

        Paragraph simplePara = new Paragraph(simpleSpan);
        simplePara.setRole(HtmlRoles.p);
        simplePara.getAccessibilityProperties().setNamespace(xhtmlNs);

        document.add(centerPara).add(simplePara);


        pdfDocument.getStructTreeRoot().addRoleMapping(PdfName.I, PdfName.Span);


        Text iSpan = new Text("cursive span");
        iSpan.setRole(PdfName.I);
        document.add(new Paragraph(iSpan));


        document.close();

        compareResult("docWithExplicitAndImplicitDefaultNsAtTheSameTime");
    }

    @Test
    public void docWithInvalidMapping01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleInNamespaceIsNotMappedToAnyStandardRole, "/p", "http://www.iso.org/pdf/ssn"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping01.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        tagsContext.setDocumentDefaultNamespace(null);
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_1_7);

        Document document = new Document(pdfDocument);

        pdfDocument.getStructTreeRoot().addRoleMapping(HtmlRoles.p, PdfName.P);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.setRole(HtmlRoles.p);
        customRolePara.getAccessibilityProperties().setNamespace(explicitDefaultNs);
        document.add(customRolePara);

        document.close();

        // compareResult("docWithInvalidMapping01");
    }

    @Test
    public void docWithInvalidMapping02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleIsNotMappedToAnyStandardRole, "/p"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping02.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();
        TagStructureContext tagsContext = pdfDocument.getTagStructureContext();
        tagsContext.setDocumentDefaultNamespace(null);
        PdfNamespace explicitDefaultNs = tagsContext.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_1_7);

        Document document = new Document(pdfDocument);

        explicitDefaultNs.addNamespaceRoleMapping(HtmlRoles.p, PdfName.P);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.setRole(HtmlRoles.p);
        document.add(customRolePara);

        document.close();

        // compareResult("docWithInvalidMapping02");
    }

    @Test
    public void docWithInvalidMapping03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleInNamespaceIsNotMappedToAnyStandardRole, "/p", "http://www.iso.org/pdf2/ssn"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping03.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.setRole(HtmlRoles.p);
        document.add(customRolePara);

        document.close();

        // compareResult("docWithInvalidMapping03");
    }

    @Test
    public void docWithInvalidMapping04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // TODO this test passes, however it seems, that mingling two standard namespaces in the same tag structure tree should be illegal
        // May be this should be checked if we would implement conforming PDF/UA docs generations in a way PDF/A docs are generated
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping04.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        TagStructureContext tagsCntxt = pdfDocument.getTagStructureContext();
        PdfNamespace stdNs2 = tagsCntxt.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        stdNs2.addNamespaceRoleMapping(HtmlRoles.p, PdfName.P);


        Document document = new Document(pdfDocument);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.setRole(HtmlRoles.p);
        document.add(customRolePara);

        document.close();

        compareResult("docWithInvalidMapping04");
    }

    @Test
    public void docWithInvalidMapping05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleInNamespaceIsNotMappedToAnyStandardRole, "/p", "http://www.iso.org/pdf2/ssn"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping05.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        // deliberately creating namespace via constructor instead of using TagStructureContext#fetchNamespace
        PdfNamespace stdNs2 = new PdfNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        stdNs2.addNamespaceRoleMapping(HtmlRoles.p, PdfName.P, stdNs2);

        Paragraph customRolePara = new Paragraph("Hello world text.");
        customRolePara.setRole(HtmlRoles.p);
        customRolePara.getAccessibilityProperties().setNamespace(stdNs2);
        document.add(customRolePara);

        Paragraph customRolePara2 = new Paragraph("Hello world text.");
        customRolePara2.setRole(HtmlRoles.p);
        // not explicitly setting namespace that we've manually created. This will lead to the situation, when
        // /Namespaces entry in StructTreeRoot would have two different namespace dictionaries with the same name.
        document.add(customRolePara2);

        document.close();

//         compareResult("docWithInvalidMapping05");
    }

    @Test
    public void docWithInvalidMapping06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping06.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);


        TagStructureContext tagCntxt = pdfDocument.getTagStructureContext();
        PdfNamespace pointerNs = tagCntxt.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        pointerNs.addNamespaceRoleMapping(HtmlRoles.span, PdfName.Span, pointerNs);

        // deliberately creating namespace via constructor instead of using TagStructureContext#fetchNamespace
        PdfNamespace stdNs2 = new PdfNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        stdNs2.addNamespaceRoleMapping(HtmlRoles.span, PdfName.Em, stdNs2);


        Text customRolePText1 = new Text("Hello world text 1.");
        customRolePText1.setRole(HtmlRoles.span);
        customRolePText1.getAccessibilityProperties().setNamespace(stdNs2);
        document.add(new Paragraph(customRolePText1));

        Text customRolePText2 = new Text("Hello world text 2.");
        customRolePText2.setRole(HtmlRoles.span);
        // not explicitly setting namespace that we've manually created. This will lead to the situation, when
        // /Namespaces entry in StructTreeRoot would have two different namespace dictionaries with the same name.
        document.add(new Paragraph(customRolePText2));

        document.close();

        compareResult("docWithInvalidMapping06");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.CANNOT_RESOLVE_ROLE_IN_NAMESPACE_TOO_MUCH_TRANSITIVE_MAPPINGS, count = 2))
    public void docWithInvalidMapping07() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleInNamespaceIsNotMappedToAnyStandardRole, "/span", "http://www.iso.org/pdf2/ssn"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping07.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        PdfNamespace stdNs2 = pdfDocument.getTagStructureContext().fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        int numOfTransitiveMappings = 120;
        PdfName prevRole = HtmlRoles.span;
        for (int i = 0; i < numOfTransitiveMappings; ++i) {
            String nextRoleName = "span" + i;
            PdfName nextRole = new PdfName(nextRoleName);
            stdNs2.addNamespaceRoleMapping(prevRole, nextRole, stdNs2);
            prevRole = nextRole;
        }
        stdNs2.addNamespaceRoleMapping(prevRole, PdfName.Span, stdNs2);


        Text customRolePText1 = new Text("Hello world text.");
        customRolePText1.setRole(HtmlRoles.span);
        customRolePText1.getAccessibilityProperties().setNamespace(stdNs2);
        document.add(new Paragraph(customRolePText1));

        document.close();

//        compareResult("docWithInvalidMapping07");
    }

    @Test
    public void docWithInvalidMapping08() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(MessageFormat.format(PdfException.RoleIsNotMappedToAnyStandardRole, "/H9"));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "docWithInvalidMapping08.pdf",
                new WriterProperties().setPdfVersion(PdfVersion.PDF_1_7)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph h9Para = new Paragraph("Header level 9");
        h9Para.setRole(new PdfName("H9"));
        document.add(h9Para);

        document.close();

//        compareResult("docWithInvalidMapping08");
    }

    @Test
    public void stampTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "simpleDocOldStdNs.pdf"),
                new PdfWriter(destinationFolder + "stampTest01.pdf",
                        new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("stamped text"));
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
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("stamped text"));
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
        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("stamped text"));
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
        PdfNamespace xhtmlNs = tagCntxt.fetchNamespace(new PdfString("http://www.w3.org/1999/xhtml"));
        PdfNamespace ssn2 = tagCntxt.fetchNamespace(StandardStructureNamespace.STANDARD_STRUCTURE_NAMESPACE_FOR_2_0);
        xhtmlNs.addNamespaceRoleMapping(HtmlRoles.ul, PdfName.L, ssn2);

        TagTreePointer pointer = new TagTreePointer(pdfDocument);
        pointer.moveToKid(PdfName.Table).moveToKid(PdfName.TR).moveToKid(1, PdfName.TD).moveToKid(PdfName.L);
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
        static PdfName h1 = new PdfName("h1");
        static PdfName p = new PdfName("p");
        static PdfName img = new PdfName("img");
        static PdfName ul = new PdfName("ul");
        static PdfName center = new PdfName("center");
        static PdfName span = new PdfName("span");
    }

    private void addSimpleContentToDoc(Document document, Paragraph p2) throws MalformedURLException {
        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        Table table = new Table(4);

        for (int k = 0; k < 5; k++) {
            table.addCell(p2);

            List list = new List().setListSymbol("-> ");
            list.add("list item").add("list item").add("list item").add("list item").add("list item");
            Cell cell = new Cell().add(list);
            table.addCell(cell);

            Cell c = new Cell().add(img);
            table.addCell(c);

            Table innerTable = new Table(3);
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
                                           PdfName hnRole,
                                           Document document) throws MalformedURLException {
        Paragraph h1P = new Paragraph("Header level 1");
        h1P.setRole(HtmlRoles.h1);

        Paragraph helloWorldPara = new Paragraph("Hello World from iText7");
        helloWorldPara.setRole(HtmlRoles.p);

        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100);
        img.setRole(HtmlRoles.img);

        document.add(h1P);
        document.add(helloWorldPara);
        document.add(img);

        pdfDocument.getTagStructureContext().getAutoTaggingPointer().setNamespaceForNewTags(defaultNamespace);

        List list = new List().setListSymbol("-> ");
        list.setRole(HtmlRoles.ul);
        list.getAccessibilityProperties().setNamespace(xhtmlNs);
        list.add("list item").add("list item").add("list item").add("list item").add(new ListItem("list item"));
        document.add(list);

        Paragraph center = new Paragraph("centered text").setTextAlignment(TextAlignment.CENTER);
        center.setRole(HtmlRoles.center);
        center.getAccessibilityProperties().setNamespace(html4Ns);
        document.add(center);

        Paragraph h11Para = new Paragraph("Heading level 11");
        h11Para.setRole(hnRole);
        document.add(h11Para);

        if (defaultNamespace == null) {
            Text i = new Text("italic text");
            i.setRole(PdfName.I);
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
            Assert.fail(errorMessage);
        }
    }
}
