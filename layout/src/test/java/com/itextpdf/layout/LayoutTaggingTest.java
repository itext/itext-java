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
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;


@Category(IntegrationTest.class)
public class LayoutTaggingTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LayoutTaggingTest/";
    public static final String imageName = "Desert.jpg";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LayoutTaggingTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void textInParagraphTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "textInParagraphTest01.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1();
        document.add(p);

        for (int i = 0; i < 26; ++i) {
            document.add(createParagraph2());
        }

        document.close();

        compareResult("textInParagraphTest01.pdf", "cmp_textInParagraphTest01.pdf");
    }

    @Test
    public void textInParagraphTestWithIds() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "textInParagraphTestWithIds.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1();
        p.getAccessibilityProperties()
                .setStructureElementId("hello".getBytes(StandardCharsets.UTF_8));
        document.add(p);

        for (int i = 0; i < 26; ++i) {
            Paragraph q = createParagraph2();
            q.getAccessibilityProperties()
                    .setStructureElementIdString("para" + i);
            document.add(q);
        }

        document.close();

        compareResult("textInParagraphTestWithIds.pdf", "cmp_textInParagraphTestWithIds.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "imageTest01.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Image image = new Image(ImageDataFactory.create(sourceFolder + imageName));
        document.add(image);

        document.close();

        compareResult("imageTest01.pdf", "cmp_imageTest01.pdf");
    }

    @Test
    public void imageTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "imageTest02.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Div div = new Div();
        div.add(new Paragraph("text before"));
        Image image = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(200);
        PdfStructureAttributes imgAttributes = new PdfStructureAttributes("Layout");
        imgAttributes.addEnumAttribute("Placement", "Block");
        image.getAccessibilityProperties().addAttributes(imgAttributes);
        div.add(image);
        div.add(new Paragraph("text after"));
        document.add(div);

        document.close();

        compareResult("imageTest02.pdf", "cmp_imageTest02.pdf");
    }

    @Test
    public void divTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "divTest01.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Div div = new Div();

        div.add(createParagraph1());
        Image image = new Image(ImageDataFactory.create(sourceFolder + imageName));
        image.setAutoScale(true);
        div.add(image);
        div.add(createParagraph2());
        div.add(image);
        div.add(createParagraph2());

        document.add(div);

        document.close();

        compareResult("divTest01.pdf", "cmp_divTest01.pdf");
    }

    @Test
    public void tableTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest01.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();
        Image image = new Image(ImageDataFactory.create(sourceFolder + imageName)).setWidth(100).setAutoScale(true);

        table.addCell(createParagraph1());
        table.addCell(image);
        table.addCell(createParagraph2());
        table.addCell(image);
        table.addCell(new Paragraph("abcdefghijklmnopqrstuvwxyz").setFontColor(ColorConstants.GREEN));
        table.addCell("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        document.add(table);

        document.close();

        compareResult("tableTest01.pdf", "cmp_tableTest01.pdf");
    }

    @Test
    public void tableTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest02.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();

        for (int i = 0; i < 5; ++i) {
            table.addCell(createParagraph2());
        }
        table.addCell("little text");

        document.add(table);

        document.close();

        compareResult("tableTest02.pdf", "cmp_tableTest02.pdf");
    }

    @Test
    public void tableTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest03.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();


        Cell cell = new Cell(1, 3).add(new Paragraph("full-width header"));
        cell.getAccessibilityProperties().setRole(StandardRoles.TH);
        table.addHeaderCell(cell);
        for (int i = 0; i < 3; ++i) {
            cell = new Cell().add(new Paragraph("header " + i));
            cell.getAccessibilityProperties().setRole(StandardRoles.TH);
            table.addHeaderCell(cell);
        }

        for (int i = 0; i < 3; ++i) {
            table.addFooterCell("footer " + i);
        }

        cell = new Cell(1, 3).add(new Paragraph("full-width paragraph"));
        table.addCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addCell(createParagraph2());
        }

        table.addCell(new Paragraph("little text"));

        document.add(table);

        document.close();

        compareResult("tableTest03.pdf", "cmp_tableTest03.pdf");
    }

    @Test
    public void tableTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest04.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 4; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}, {1}", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        compareResult("tableTest04.pdf", "cmp_tableTest04.pdf");
    }

    @Test
    public void tableTest05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest05.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(5), true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header " + (i + 1))));
        }
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        compareResult("tableTest05.pdf", "cmp_tableTest05.pdf");
    }

    @Test
    public void tableTest06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest06.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 3:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();

        compareResult("tableTest06.pdf", "cmp_tableTest06.pdf");
    }

    @Test
    public void tableTest07() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest07.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(new float[]{130, 130, 260})
                .addHeaderCell(new Cell().add(new Paragraph("hcell 1, 1")))
                .addHeaderCell(new Cell().add(new Paragraph("hcell 1, 2")))
                .addHeaderCell(new Cell().add(new Paragraph("hcell 1, 3")))

                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")))
                .addCell(new Cell().add(new Paragraph("cell 2, 3")))
                .addCell(new Cell().add(new Paragraph("cell 3, 1")))
                .addCell(new Cell().add(new Paragraph("cell 3, 2")))
                .addCell(new Cell().add(new Paragraph("cell 3, 3")))

                .addFooterCell(new Cell().add(new Paragraph("fcell 4, 1")))
                .addFooterCell(new Cell().add(new Paragraph("fcell 4, 2")))
                .addFooterCell(new Cell().add(new Paragraph("fcell 4, 3")));

        doc.add(table);

        doc.close();
        compareResult("tableTest07.pdf", "cmp_tableTest07.pdf");
    }

    @Test
    public void linkInsideTable() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(destinationFolder + "linkInsideTable.pdf"));
        pdf.setTagged();
        Document doc = new Document(pdf);

        Table table = new Table(new float[]{1, 2, 3}).setFixedLayout().setWidth(400);

        table.addCell("1x");
        table.addCell("2x");
        table.addCell("3x");
        table.setProperty(Property.LINK_ANNOTATION, new PdfLinkAnnotation(new Rectangle(0, 0)).setAction(PdfAction.createURI("http://itextpdf.com/")));
        doc.add(table);

        doc.close();
        compareResult("linkInsideTable.pdf", "cmp_linkInsideTable.pdf");
    }


    @Test
    public void tableTest08() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "tableTest08.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(new UnitValue[5], true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header " + (i + 1))));
        }
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        compareResult("tableTest08.pdf", "cmp_tableTest08.pdf");
    }

    @Test
    public void listTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "listTest01.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        List list = new List(ListNumberingType.DECIMAL);
        list.add("item 1");
        list.add("item 2");
        list.add("item 3");

        doc.add(list);
        doc.close();

        compareResult("listTest01.pdf", "cmp_listTest01.pdf");
    }

    @Test
    public void listTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "listTest02.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);
        doc.setFont(PdfFontFactory.createFont(sourceFolder + "../fonts/NotoSans-Regular.ttf", PdfEncodings.IDENTITY_H));

        PdfDictionary attributesDisc = new PdfDictionary();
        attributesDisc.put(PdfName.O, PdfName.List);
        attributesDisc.put(PdfName.ListNumbering, PdfName.Disc);

        PdfDictionary attributesSquare = new PdfDictionary();
        attributesSquare.put(PdfName.O, PdfName.List);
        attributesSquare.put(PdfName.ListNumbering, PdfName.Square);

        PdfStructureAttributes attributesCircle = new PdfStructureAttributes("List");
        attributesCircle.addEnumAttribute("ListNumbering", "Circle");

        String discSymbol = "\u2022";
        String squareSymbol = "\u25AA";
        String circleSymbol = "\u25E6";

        // setting numbering type for now
        List list = new List(ListNumberingType.ROMAN_UPPER);

        list.add("item 1");

        ListItem listItem = new ListItem("item 2");
        {
            List subList = new List().setListSymbol(discSymbol).setMarginLeft(30);
            subList.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributesDisc));

            ListItem subListItem = new ListItem("sub item 1");
            {
                List subSubList = new List().setListSymbol(squareSymbol).setMarginLeft(30);
                subSubList.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributesSquare));

                subSubList.add("sub sub item 1");
                subSubList.add("sub sub item 2");
                subSubList.add("sub sub item 3");
                subListItem.add(subSubList);
            }

            subList.add(subListItem);
            subList.add("sub item 2");
            subList.add("sub item 3");

            listItem.add(subList);
        }
        list.add(listItem);

        list.add("item 3");


        doc.add(list);
        doc.add(new LineSeparator(new SolidLine()));

        // setting circle symbol, not setting attributes
        doc.add(list.setListSymbol(circleSymbol));
        doc.add(new LineSeparator(new SolidLine()));

        list.getAccessibilityProperties().addAttributes(attributesCircle);

        // circle symbol set, setting attributes
        doc.add(list);

        doc.close();

        compareResult("listTest02.pdf", "cmp_listTest02.pdf");
    }

    @Test
    public void listTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "listTest03.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);


        PdfDictionary attributesSquare = new PdfDictionary();
        attributesSquare.put(PdfName.O, PdfName.List);
        attributesSquare.put(PdfName.ListNumbering, PdfName.Square);

        List list = new List(ListNumberingType.DECIMAL);
        // explicitly overriding ListNumbering attribute
        list.getAccessibilityProperties().addAttributes(new PdfStructureAttributes(attributesSquare));
        list.add("item 1");
        list.add("item 2");
        list.add("item 3");

        doc.add(list);
        doc.close();

        compareResult("listTest03.pdf", "cmp_listTest03.pdf");
    }

    @Test
    public void listTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "listTest04.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        List list = new List(ListNumberingType.DECIMAL);
        ListItem listItem = new ListItem();
        listItem.add(createParagraph2()).setMarginBottom(15);
        for (int i = 0; i < 10; ++i) {
            list.add(listItem);
        }

        doc.add(list);
        doc.close();

        compareResult("listTest04.pdf", "cmp_listTest04.pdf");
    }

    @Test
    public void linkTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "linkTest01.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        PdfAction action = PdfAction.createURI("http://itextpdf.com/", false);
        Link link = new Link("linked text", action);
        link.setUnderline();
        link.getLinkAnnotation().put(PdfName.Border, new PdfArray(new int[]{0, 0, 0}));

        doc.add(new Paragraph("before ").add(link).add(" after"));
        doc.close();

        compareResult("linkTest01.pdf", "cmp_linkTest01.pdf");
    }

    @Test
    public void artifactTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "artifactTest01.pdf"));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        String watermarkText = "WATERMARK";
        Paragraph watermark = new Paragraph(watermarkText);
        watermark.setFontColor(new DeviceGray(0.75f)).setFontSize(72);
        document.showTextAligned(watermark, PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 1, TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) (Math.PI / 4));

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        document.add(new Paragraph(textContent + textContent + textContent));
        document.add(new Paragraph(textContent + textContent + textContent));

        document.close();

        compareResult("artifactTest01.pdf", "cmp_artifactTest01.pdf");
    }

    @Test
    public void artifactTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "artifactTest02.pdf"));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Hello world"));

        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        for (int i = 0; i < 25; ++i) {
            table.addCell(String.valueOf(i));
        }
        table.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        document.add(table);

        document.close();

        compareResult("artifactTest02.pdf", "cmp_artifactTest02.pdf");
    }

    /**
     * Document generation and result is the same in this test as in the textInParagraphTest01, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in textInParagraphTest01.
     */
    @Test
    public void flushingTest01() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "flushingTest01.pdf"));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1();
        document.add(p);

        int pageToFlush = 1;
        for (int i = 0; i < 26; ++i) {
            if (i % 6 == 5) {
                pdfDocument.getPage(pageToFlush++).flush();
            }
            document.add(createParagraph2());
        }

        document.close();

        compareResult("flushingTest01.pdf", "cmp_flushingTest01.pdf");
    }

    /**
     * Document generation and result is the same in this test as in the tableTest05, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in tableTest05.
     */
    @Test
    public void flushingTest02() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "flushingTest02.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(5), true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addHeaderCell(new Cell().add(new Paragraph("Header " + (i + 1))));
        }
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
            table.flush();
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        compareResult("flushingTest02.pdf", "cmp_flushingTest02.pdf");
    }

    /**
     * Document generation and result is the same in this test as in the tableTest04, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in tableTest04.
     */
    @Test
    public void flushingTest03() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "flushingTest03.pdf"));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(5), true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 4; j++) {
                table.addCell(new Cell().add(new Paragraph(MessageFormatUtil.format("Cell {0}, {1}", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                pdfDocument.getTagStructureContext().flushPageTags(pdfDocument.getPage(1));

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();
        doc.add(new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.ORANGE, 2)).addCell("Is my occupied area correct?"));

        doc.close();

        compareResult("flushingTest03.pdf", "cmp_tableTest04.pdf");
    }

    @Test
    public void wordBreaksLineEndingsTest01() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "wordBreaksLineEndingsTest01.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String s = "Beaver was settled in 1856 by Mormon pioneers traveling this road.";
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            text.append(s);
            text.append(" ");
        }
        Paragraph p = new Paragraph(text.toString().trim());
        doc.add(p);

        doc.close();

        compareResult("wordBreaksLineEndingsTest01.pdf", "cmp_wordBreaksLineEndingsTest01.pdf");
    }

    @Test
    public void wordBreaksLineEndingsTest02() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "wordBreaksLineEndingsTest02.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String s = "Beaver was settled in 1856 by Mormon pioneers traveling this road.";
        Paragraph p = new Paragraph(s + " Beaver was settled in 1856 by").add(" Mormon pioneers traveling this road.");
        doc.add(p);

        doc.close();

        compareResult("wordBreaksLineEndingsTest02.pdf", "cmp_wordBreaksLineEndingsTest02.pdf");
    }

    @Test
    public void wordBreaksLineEndingsTest03() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "wordBreaksLineEndingsTest03.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String s = "Beaver was settled in 1856 by\nMormon pioneers traveling this road.";
        Paragraph p = new Paragraph(s);
        doc.add(p);

        String s1 = "Beaver was settled in 1856 by \n Mormon pioneers traveling this road.";
        Paragraph p1 = new Paragraph(s1);
        doc.add(p1);

        String s2 = "\nBeaver was settled in 1856 by Mormon pioneers traveling this road.";
        Paragraph p2 = new Paragraph(s2);
        doc.add(p2);

        String s3_1 = "Beaver was settled in 1856 by";
        String s3_2 = "\nMormon pioneers traveling this road.";
        Paragraph p3 = new Paragraph(s3_1).add(s3_2);
        doc.add(p3);

        doc.close();

        compareResult("wordBreaksLineEndingsTest03.pdf", "cmp_wordBreaksLineEndingsTest03.pdf");
    }

    @Test
    public void wordBreaksLineEndingsTest04() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "wordBreaksLineEndingsTest04.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String s = "ShortWord Beaverwassettledin1856byMormonpioneerstravelingthisroadBeaverwassettledin1856byMormonpioneerstravelingthisroad.";
        Paragraph p = new Paragraph(s);
        doc.add(p);

        String s1 = "ShortWord " +
                "                                                                                          " +
                "                                                                                          " +
                "and another short word.";
        Paragraph p1 = new Paragraph(s1);
        doc.add(p1);

        doc.close();

        compareResult("wordBreaksLineEndingsTest04.pdf", "cmp_wordBreaksLineEndingsTest04.pdf");
    }

    @Test
    public void wordBreaksLineEndingsTest05() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "wordBreaksLineEndingsTest05.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        String s = "t\n";
        Paragraph p = new Paragraph(s).add("\n").add(s);
        doc.add(p);

        Paragraph p1 = new Paragraph(s);
        doc.add(p1);

        Paragraph p2 = new Paragraph(s).add("another t");
        doc.add(p2);

        doc.close();

        compareResult("wordBreaksLineEndingsTest05.pdf", "cmp_wordBreaksLineEndingsTest05.pdf");
    }

    @Test
    public void imageAndTextNoRole01() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(
                new PdfWriter(destinationFolder + "imageAndTextNoRole01.pdf",
                        new WriterProperties().setCompressionLevel(CompressionConstants.NO_COMPRESSION)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        doc.add(new Paragraph("Set Image role to null and add to div with role \"Figure\""));
        Image img = new Image(ImageDataFactory.create(sourceFolder + imageName))
                .setWidth(200)
                .setNeutralRole();
        Div div = new Div();
        div.getAccessibilityProperties().setRole(StandardRoles.FIGURE);
        div.add(img);
        Paragraph caption = new Paragraph("Caption");
        caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
        div.add(caption);
        doc.add(div);

        doc.add(new Paragraph("Set Text role to null and add to Paragraph").setMarginTop(20));
        div = new Div();
        div.getAccessibilityProperties().setRole(StandardRoles.CODE);
        Text txt = new Text("// Prints Hello world!");
        div.add(new Paragraph(txt.setNeutralRole()).setMarginBottom(0));
        txt = new Text("System.out.println(\"Hello world!\");");
        div.add(new Paragraph(txt.setNeutralRole()).setMarginTop(0));
        doc.add(div);

        doc.close();

        compareResult("imageAndTextNoRole01.pdf", "cmp_imageAndTextNoRole01.pdf");
    }

    @Test
    public void tableWithCaption01() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder + "tableWithCaption01.pdf");
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        pdf.setTagged();
        Paragraph p;

        p = new Paragraph("We try to create a Table with a Caption by creating a Div with two children: " +
                "a Div that is a caption and a Table. " +
                "To tag this correctly, I set the outer Div role to Table, the inner Div to Caption, and the " +
                "Table to null.");
        document.add(p);

        p = new Paragraph("This table is tagged correctly.");
        document.add(p);
        document.add(createTable(false));

        p = new Paragraph("This table has a caption and is tagged incorrectly. ");
        document.add(p);
        document.add(createTable(true));

        document.close();

        compareResult("tableWithCaption01.pdf", "cmp_tableWithCaption01.pdf");
    }

    @Test
    public void emptyDivTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder + "emptyDivTest.pdf");
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        pdf.setTagged();

        // This tests that /Artifact content is properly closed in canvas
        document.add(new Div().add(new Div().setBackgroundColor(ColorConstants.RED)).setBackgroundColor(ColorConstants.RED));
        document.add(new Paragraph("Hello"));

        document.close();

        compareResult("emptyDivTest.pdf", "cmp_emptyDivTest.pdf");
    }

    @Test
    public void floatListItemTest() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder + "floatListItemTest.pdf");
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        pdf.setTagged();

        ListItem li = new ListItem("List item");
        li.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        document.add(new List().add(li));

        document.close();

        compareResult("floatListItemTest.pdf", "cmp_floatListItemTest.pdf");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_TO_CREATE_A_TAG_FOR_FINISHED_HINT)
    })
    //TODO update cmp-file after DEVSIX-3335 fixed
    public void notAsciiCharTest() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        PdfWriter writer = new PdfWriter(destinationFolder + "notAsciiCharTest.pdf");
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        pdf.setTagged();

        FontProvider sel = new FontProvider();
        sel.addFont(fontsFolder + "NotoSans-Regular.ttf");
        sel.addFont(StandardFonts.TIMES_ROMAN);

        document.setFontProvider(sel);
        Paragraph p = new Paragraph("\u0422\u043E be or not.");
        p.setFontFamily("times");
        document.add(p);
        document.close();

        compareResult("notAsciiCharTest.pdf", "cmp_notAsciiCharTest.pdf");
    }

    @Test
    //TODO update cmp-file after DEVSIX-3351 fixed
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.XOBJECT_HAS_NO_STRUCT_PARENTS)})
    public void checkParentTreeIfFormXObjectTaggedTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "checkParentTreeIfFormXObjectTaggedTest.pdf";
        String cmpPdf = sourceFolder + "cmp_checkParentTreeIfFormXObjectTaggedTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfPage page1 = pdfDoc.addNewPage();

        Text txt = new Text("Text from XObject");

        PdfFormXObject template = new PdfFormXObject(new Rectangle(150, 150));
        Canvas canvas = new Canvas(template, pdfDoc);
        canvas.enableAutoTagging(page1);
        canvas.add(new Paragraph(txt));

        PdfCanvas canvas1 = new PdfCanvas(page1);
        canvas1.addXObjectAt(template, 10, 10);

        pdfDoc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpPdf, destinationFolder, "diff"));
    }

    @Test
    public void createTaggedVersionOneDotFourTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "createTaggedVersionOneDotFourTest01.pdf", new WriterProperties().setPdfVersion(PdfVersion.PDF_1_4)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();


        Cell cell = new Cell(1, 3).add(new Paragraph("full-width header"));
        cell.getAccessibilityProperties().setRole(StandardRoles.TH);
        table.addHeaderCell(cell);
        for (int i = 0; i < 3; ++i) {
            cell = new Cell().add(new Paragraph("header " + i));
            cell.getAccessibilityProperties().setRole(StandardRoles.TH);
            table.addHeaderCell(cell);
        }

        for (int i = 0; i < 3; ++i) {
            table.addFooterCell("footer " + i);
        }

        cell = new Cell(1, 3).add(new Paragraph("full-width paragraph"));
        table.addCell(cell);
        for (int i = 0; i < 20; ++i) {
            table.addCell(createParagraph2());
        }

        table.addCell(new Paragraph("little text"));

        document.add(table);

        document.close();

        compareResult("createTaggedVersionOneDotFourTest01.pdf", "cmp_createTaggedVersionOneDotFourTest01.pdf");
    }

    @Test
    public void neutralRoleTaggingTest() throws Exception {
        String outFile = "neutralRoleTaggingTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + outFile));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        Table table = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth()
                .setWidth(UnitValue.createPercentValue(100))
                .setFixedLayout();

        Cell cell1 = new Cell()
                .add(new Paragraph(new Text("This is bare text").setNeutralRole()).setNeutralRole());
        table.addCell(cell1);

        Paragraph untaggedPara = new Paragraph()
                .setNeutralRole()
                .add(new Text("This is text in an ").setNeutralRole())
                .add(new Text("untagged"))
                .add(new Text(" paragraph").setNeutralRole());
        Div untaggedDiv = new Div().setNeutralRole().add(untaggedPara);
        table.addCell(new Cell().add(untaggedDiv));

        Div listGroup = new Div();
        listGroup.getAccessibilityProperties().setRole(StandardRoles.L);
        List list1 = new List().setNeutralRole()
                .add(new ListItem("Item 1"))
                .add(new ListItem("Item 2"));
        listGroup.add(list1);
        Paragraph filler = new Paragraph(new Text("<pretend this is an artifact>").setNeutralRole());
        filler.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        listGroup.add(filler);
        List list2 = new List().setNeutralRole()
                .add(new ListItem("More items!"))
                .add(new ListItem("Moooore items!!"));
        listGroup.add(list2);
        table.addCell(new Cell().add(listGroup));

        document.add(table);
        document.close();
        compareResult(outFile, "cmp_" + outFile);
    }

    private Paragraph createParagraph1() throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph p = new Paragraph().add("text chunk. ").add("explicitly added separate text chunk");
        Text id = new Text("text chunk with specific font").setFont(font).setFontSize(8).setTextRise(6);
        p.add(id);
        return p;
    }

    private Paragraph createParagraph2() {
        Paragraph p;
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 26; ++i) {
            longTextBuilder.append(alphabet);
        }

        String longText = longTextBuilder.toString();
        p = new Paragraph(longText);
        return p;
    }

    private IBlockElement createTable(boolean useCaption) {
        Table table = new Table(new float[3])
                .setMarginTop(10)
                .setMarginBottom(10);
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                String content = r + "," + c;
                Cell cell = new Cell();
                cell.add(new Paragraph(content));
                table.addCell(cell);
            }
        }
        if (useCaption) {
            Div div = new Div();
            div.getAccessibilityProperties().setRole(StandardRoles.TABLE);
            Paragraph p = new Paragraph("Caption").setNeutralRole();
            p.setTextAlignment(TextAlignment.CENTER).setBold();
            Div caption = new Div().add(p);
            caption.getAccessibilityProperties().setRole(StandardRoles.CAPTION);
            div.add(caption);
            div.add(table.setNeutralRole());
            return div;
        } else
            return table;
    }


    private void compareResult(String outFileName, String cmpFileName)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String outPdf = destinationFolder + outFileName;
        String cmpPdf = sourceFolder + cmpFileName;

        String contentDifferences = compareTool.compareByContent(outPdf,
                cmpPdf, destinationFolder, "diff");
        String taggedStructureDifferences = compareTool.compareTagStructures(outPdf, cmpPdf);

        String errorMessage = "";
        errorMessage += taggedStructureDifferences == null ? "" : taggedStructureDifferences + "\n";
        errorMessage += contentDifferences == null ? "" : contentDifferences;
        if (!errorMessage.isEmpty()) {
            Assert.fail(errorMessage);
        }
    }
}
