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
package com.itextpdf.layout.tagging;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Tag("IntegrationTest")
public class ProhibitedTagRelationsResolverTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout" +
            "/ResolveProhibitedRelationsRuleTest/";

    public static final String FONT_LOCATION = "./src/test/resources/com/itextpdf/layout/fonts/NotoSans-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }


    @Test
    public void addTestForPinPMappingToSpan() throws XMPException, IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        ProhibitedTagRelationsResolver resolver = pdfDocument.getDiContainer()
                .getInstance(ProhibitedTagRelationsResolver.class);
        resolver.overwriteTaggingRule(StandardRoles.P, StandardRoles.P, StandardRoles.LBL);


        Paragraph paragraph = new Paragraph();
        paragraph.add(new Paragraph("Hello World1"));
        paragraph.add(new Paragraph("Hello World2"));
        document.add(paragraph);


        TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
        tagPointer.moveToKid(StandardRoles.P);

        Assertions.assertEquals(StandardRoles.LBL, tagPointer.moveToKid(0).getRole());
        tagPointer.moveToParent();
        Assertions.assertEquals(StandardRoles.LBL, tagPointer.moveToKid(1).getRole());


        pdfDocument.close();
    }

    @Test
    public void usingNonStructDoesntRemapP() throws XMPException, IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        Paragraph paragraph = new Paragraph();
        paragraph.getAccessibilityProperties().setRole(StandardRoles.NONSTRUCT);
        paragraph.add(new Paragraph("Hello World1"));
        paragraph.add(new Paragraph("Hello World2"));
        document.add(paragraph);



        TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
        tagPointer.moveToKid(StandardRoles.NONSTRUCT);
        Assertions.assertEquals(StandardRoles.P, tagPointer.moveToKid(0).getRole());
        tagPointer.moveToParent();
        Assertions.assertEquals(StandardRoles.P, tagPointer.moveToKid(1).getRole());


        pdfDocument.close();
    }

    @Test
    public void areaBreakNonAccessibleDoesntChange() throws XMPException, IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        Paragraph paragraph = new Paragraph();
        paragraph.getAccessibilityProperties().setRole(StandardRoles.NONSTRUCT);
        paragraph.add(new Paragraph("Hello World1"));
        document.add(paragraph);
        document.add(new AreaBreak());



        TagTreePointer tagPointer = new TagTreePointer(pdfDocument);
        tagPointer.moveToKid(StandardRoles.NONSTRUCT);
        Assertions.assertEquals(StandardRoles.P, tagPointer.moveToKid(0).getRole());

        pdfDocument.close();
    }

    @Test
    public void tableWithSomeNotExistingTags() throws XMPException, IOException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(
                        PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        Table table = new Table(2);
        for (int i = 0; i < 10; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello World")));
        }
        table.addHeaderCell(new Cell().add(new Paragraph("Hello World")));
        table.addHeaderCell(new Cell().add(new Paragraph("Hello World")));

        Div caption = new Div();
        table.setCaption(caption);

        document.add(table);
        pdfDocument.close();
    }

    @Test
    public void layoutList() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testLayoutList.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        List list = new List();
        list.add("Hello world!");
        list.add("Hello world1!");
        list.add("Hello world2!");

        document.add(list);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void customRoleMapping() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "customRoleMapping.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);
        String role = "FancyHeading";


        PdfNamespace space = pdfDocument.getTagStructureContext().getDocumentDefaultNamespace();
        space.addNamespaceRoleMapping(role, StandardRoles.H1);
        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        Paragraph paragraph = new Paragraph("Hello World");
        paragraph.getAccessibilityProperties().setRole(role);

        Paragraph paragraph1 = new Paragraph("Hello World1");
        paragraph.add(paragraph1);

        document.add(paragraph);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void customRoleMappingWithSkipParent() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "customRoleMappingWithSkipParent.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);
        String role = "FancyHeading";


        PdfNamespace space = pdfDocument.getTagStructureContext().getDocumentDefaultNamespace();
        space.addNamespaceRoleMapping(role, StandardRoles.P);
        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        Paragraph paragraph = new Paragraph("Hello World");
        paragraph.getAccessibilityProperties().setRole(role);

        Paragraph paragraph1 = new Paragraph("Hello World1");

        paragraph.add(paragraph1);

        document.add(paragraph);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }


    @Test
    public void layoutListWithImage() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "testLayoutListWithImage.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));

        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        List list = new List();
        Image img = new Image(ImageDataFactory.create("./src/test/resources/com/itextpdf/layout/ImageTest/itis.jpg"));
        img.getAccessibilityProperties().setAlternateDescription("cat");
        list.add(new ListItem(img));
        list.add(new ListItem("Hello world1!"));

        document.add(list);

        pdfDocument.close();
        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    @Test
    public void layoutRole() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "layoutRole.pdf";


        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);


        Paragraph h1 = new Paragraph("Header 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);
        Paragraph paragraph = new Paragraph("Hello World");
        h1.add(paragraph);
        document.add(h1);

        pdfDocument.close();

        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }


    @Test
    public void layoutHeaderInTable() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "layoutHeaderInTable.pdf";


        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        Table table = new Table(2);
        for (int i = 0; i < 20; i++) {
            table.addCell(new Cell().add(new Paragraph("Hello World")));
        }

        Div div = new Div();
        Paragraph h1 = new Paragraph("Header 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);
        Paragraph normalText = new Paragraph("Hello World");
        h1.add(normalText);
        div.add(h1);
        table.setCaption(div);

        document.add(table);

        pdfDocument.close();

        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }


    @Test
    public void layoutTableWithHeaderLargeTable() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "layoutTableWithHeaderLargeTable.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        for (int i = 0; i < 300; i++) {
            document.add(new Paragraph("Hello World"));
        }

        Table table = new Table(2);
        Cell cell = new Cell();
        cell.add(new Paragraph("Hello World"));
        cell.getAccessibilityProperties().setRole(StandardRoles.TH);
        table.addCell(cell);

        Paragraph h1 = new Paragraph("Header 1");
        h1.getAccessibilityProperties().setRole(StandardRoles.H1);
        Cell cell1 = new Cell();
        h1.add(new Paragraph("Bing"));
        cell1.add(h1);
        cell1.getAccessibilityProperties().setRole(StandardRoles.TH);
        table.addCell(cell1);

        document.add(table);
        pdfDocument.close();

        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }


    @Test
    public void layoutWithFlushing() throws IOException, XMPException {
        String dest = DESTINATION_FOLDER + "layoutWithFlushing.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)));
        convertToUa2(pdfDocument);

        PdfFont font = PdfFontFactory.createFont(FONT_LOCATION);
        Document document = new Document(pdfDocument);
        document.setFont(font);

        for (int i = 0; i < 500; i++) {
            Paragraph h1 = new Paragraph("Header 1");
            h1.getAccessibilityProperties().setRole(StandardRoles.H1);
            Paragraph paragraph = new Paragraph("Hello World");
            h1.add(paragraph);
            document.add(h1);
            if (i % 50 == 0) {
                document.flush();
            }
        }
        pdfDocument.close();

        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

    }

    private static void convertToUa2(PdfDocument pdfDocument) throws XMPException, IOException {
        // We can't depend on ua module in layout module so we need to do some low level operations
        // to convert the to ua2
        byte[] bytes = Files.readAllBytes(
                Paths.get("./src/test/resources/com/itextpdf/layout/PdfUA2Test/simplePdfUA2.xmp"));

        pdfDocument.getDiContainer()
                .register(ProhibitedTagRelationsResolver.class, new ProhibitedTagRelationsResolver(pdfDocument));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        pdfDocument.setXmpMetadata(xmpMeta);
        pdfDocument.setTagged();
        pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("PdfUA2 Title");
    }
}
