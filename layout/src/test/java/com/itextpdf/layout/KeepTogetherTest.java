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


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.ClearPropertyValue;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class KeepTogetherTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/KeepTogetherTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/KeepTogetherTest/";

    private static final String BIG_TEXT = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n"
            + " sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n"
            + " sed diam voluptua.\n\n At vero eos et accusam et justo duo dolores et ea rebum.\n\n "
            + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n"
            + " sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n"
            + " sed diam voluptua.\n\n At vero eos et accusam et justo duo dolores et ea rebum.\n\n "
            + "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n sed diam nonumy eirmod tempor"
            + " invidunt ut labore et dolore magna aliquyam erat,\n sed diam voluptua.\n\n"
            + " At vero eos et accusam et justo duo dolores et ea rebum.\n\n ";
    private static final String MEDIUM_TEXT = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr"
            + " sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
            + " At vero eos et accusam et justo duo dolores et ea rebum.\n ";
    private static final String SMALL_TEXT = "Short text";


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void keepTogetherParagraphTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherParagraphTest01.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherParagraphTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);


        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanasdadasdadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        Paragraph p1 = new Paragraph(str);
        p1.setKeepTogether(true);
        doc.add(p1);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void skipKeepTogetherInCaseOfAreaBreak() throws IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document doc = new Document(pdfDoc);

        Div keptTogetherDiv = new Div();
        keptTogetherDiv.setKeepTogether(true);

        AreaBreak areaBreak = new AreaBreak();
        keptTogetherDiv.add(areaBreak);

        doc.add(keptTogetherDiv);

        // If this line is not triggered, then an NPE occurred
        Assertions.assertTrue(true);

        doc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherParagraphTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherParagraphTest02.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherParagraphTest02.pdf";

        PdfWriter writer = new PdfWriter(outFile);


        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanasdadasdadaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        for (int i = 0; i < 5; i++) {
            str += str;
        }

        Paragraph p1 = new Paragraph(str);
        p1.setKeepTogether(true);
        doc.add(p1);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void keepTogetherListTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherListTest01.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherListTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        for (int i = 0; i < 28; i++) {
            doc.add(new Paragraph("String number" + i));
        }

        List list = new List();
        list.add("firstItem").add("secondItem").add("thirdItem").setKeepTogether(true).setListSymbol(ListNumberingType.DECIMAL);
        doc.add(list);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void keepTogetherDivTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherDivTest01.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherDivTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph("Test String");

        for (int i = 0; i < 28; i++) {
            doc.add(p);
        }

        Div div = new Div();
        div.add(new Paragraph("first paragraph"));
        div.add(new Paragraph("second paragraph"));
        div.add(new Paragraph("third paragraph"));
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void keepTogetherMinHeightTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherMinHeightTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherMinHeightTest.pdf";

        PdfWriter writer = new PdfWriter(outFile);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph("Test String");

        for (int i = 0; i < 15; i++) {
            doc.add(p);
        }

        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setMinHeight(500);
        div.setKeepTogether(true);
        div.add(new Paragraph("Hello"));
        doc.add(div);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherDivTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherDivTest02.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherDivTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        Rectangle[] columns = {new Rectangle(100, 100, 100, 500), new Rectangle(400, 100, 100, 500)};
        doc.setRenderer(new ColumnDocumentRenderer(doc, columns));
        Div div = new Div();
        doc.add(new Paragraph("first string"));
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("String number " + i));
        }
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void keepTogetherDivWithInnerClearDiv() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherDivWithInnerClearDiv.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherDivWithInnerClearDiv.pdf";

        try (PdfWriter pdfWriter = new PdfWriter(outFile);
                PdfDocument pdfDoc = new PdfDocument(pdfWriter);
                Document doc = new Document(pdfDoc)) {

            Div keepTogetherDiv = new Div();
            keepTogetherDiv.setKeepTogether(true);
            keepTogetherDiv.setBackgroundColor(ColorConstants.BLUE);

            Div shortFloat = new Div();
            shortFloat.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            shortFloat.setWidth(UnitValue.createPercentValue(30));
            shortFloat.setBackgroundColor(ColorConstants.GREEN);
            shortFloat.add(new Paragraph("Short text"));
            keepTogetherDiv.add(shortFloat);

            Div longFloat = new Div();
            longFloat.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            longFloat.setWidth(UnitValue.createPercentValue(70));
            longFloat.setBackgroundColor(ColorConstants.ORANGE);
            longFloat.add(new Paragraph("Lorem ipsum dolor sit amet, consetetur sadipscing "
                    + "elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
                    + "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo "
                    + "dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est "
                    + "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur "
                    + "sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et "
                    + "dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et "
                    + "justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata "
                    + "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
                    + "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut "
                    + "labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et "
                    + "accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea "
                    + "takimata sanctus est Lorem ipsum dolor sit amet.\n"
                    + "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse "
                    + "molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero "
                    + "eros et accumsan et iusto odio dignissim qui blandit praesent luptatum "
                    + "zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum "
                    + "dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod "
                    + "tincidunt ut laoreet dolore magna aliquam erat volutpat.\n"
                    + "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper "
                    + "suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel "
                    + "eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, "
                    + "vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et "
                    + "iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis "
                    + "dolore te feugait nulla facilisi."));
            keepTogetherDiv.add(longFloat);

            Div clearDiv = new Div();
            clearDiv.setProperty(Property.CLEAR, ClearPropertyValue.BOTH);
            keepTogetherDiv.add(clearDiv);

            // on first add we could see how the div should be rendered when it fits the page area
            doc.add(keepTogetherDiv);
            // on second add the div should not fit the left space and, since we have keep together
            // property set, should be fully placed on the second page with the same appearance
            // as the first add
            doc.add(keepTogetherDiv);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherDefaultTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherDefaultTest01.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherDefaultTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        Div div = new KeepTogetherDiv();
        doc.add(new Paragraph("first string"));
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("String number " + i));
        }

        doc.add(div);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @Disabled("DEVSIX-1837: NPE")
    public void keepTogetherInlineDiv01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherInlineDiv01.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherInlineDiv01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);


        doc.add(new Paragraph("first string"));

        Div div = new Div().setWidth(200);
        for (int i = 0; i < 130; i++) {
            div.add(new Paragraph("Part of inline div; string number " + i));
        }
        div.setKeepTogether(true);

        doc.add(new Paragraph().add(div));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherInlineDiv02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherInlineDiv02.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherInlineDiv02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);


        doc.add(new Paragraph("first string"));

        Div div = new Div().setWidth(200);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 130; i++) {
            buffer.append("Part #" + i + " of inline div");
        }
        div.add(new Paragraph(buffer.toString()));
        div.setKeepTogether(true);

        doc.add(new Paragraph().add(div));
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 8)
    })
    public void narrowPageTest01() throws IOException, InterruptedException {
        String testName = "narrowPageTest01.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table tbl = new Table(UnitValue.createPointArray(new float[]{30.0F, 30.0F, 30.0F, 30.0F}));
        tbl.setWidth(120.0F);
        tbl.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
        tbl.setFontSize(8.0F);

        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 4; y++) {
                Cell cell = new Cell();
                cell.add(new Paragraph("row " + x));
                cell.setHeight(10.5f);
                cell.setMaxHeight(10.5f);
                cell.setKeepTogether(true);
                tbl.addCell(cell);
            }
        }

        doc.add(tbl);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    public void narrowPageTest02() throws IOException, InterruptedException {
        String testName = "narrowPageTest02.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new SpecialOddPagesDocumentRenderer(doc, new PageSize(102.0F, 132.0F)));

        Paragraph p = new Paragraph("row 10");
        Div div = new Div();
        div.add(p);
        div.setKeepTogether(true);

        doc.add(new Paragraph("a"));
        doc.add(div);
        doc.add(new AreaBreak());

        div.setHeight(30);
        doc.add(new Paragraph("a"));
        doc.add(div);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        div.deleteOwnProperty(Property.HEIGHT);
        doc.add(div);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        div.setHeight(30);
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, testName + "_diff"));
    }

    @Test
    public void narrowPageTest02A() throws IOException, InterruptedException {
        String testName = "narrowPageTest02A.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        doc.setRenderer(new SpecialOddPagesDocumentRenderer(doc, new PageSize(102.0F, 102.0F)));

        Paragraph p = new Paragraph("row 10");
        p.setKeepTogether(true);

        doc.add(new Paragraph("a"));
        doc.add(p);
        doc.add(new AreaBreak());

        p.setHeight(30);
        doc.add(new Paragraph("a"));
        doc.add(p);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        p.deleteOwnProperty(Property.HEIGHT);
        doc.add(p);
        doc.add(new AreaBreak());
        doc.add(new AreaBreak());

        p.setHeight(30);
        doc.add(p);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void updateHeightTest01() throws IOException, InterruptedException {
        String testName = "updateHeightTest01.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setDefaultPageSize(new PageSize(102.0F, 102.0F));
        Document doc = new Document(pdfDoc);


        Div div = new Div();
        div.setBackgroundColor(ColorConstants.RED);
        div.add(new Paragraph("row"));
        div.add(new Paragraph("row 10"));

        div.setKeepTogether(true);
        div.setHeight(100);

        doc.add(new Paragraph("a"));
        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 1),
            @LogMessage(messageTemplate = IoLogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, count = 22),

    })
    //TODO DEVSIX-1977
    public void partialTest01() throws IOException, InterruptedException {
        String testName = "partialTest01.pdf";
        String outFileName = DESTINATION_FOLDER + testName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setDefaultPageSize(PageSize.A7);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setBackgroundColor(ColorConstants.RED);
        div.setKeepTogether(true);
        div.setHeight(200);

        for (int i = 0; i < 30; i++) {
            div.add(new Paragraph("row " + i));
        }

        doc.add(div);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void fixedHeightOverflowTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_fixedHeightOverflowTest01.pdf";
        String outFile = DESTINATION_FOLDER + "fixedHeightOverflowTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("first string"));

        // specifying height definitely bigger than page height
        int divHeight = 1000;

        // test keep-together processing on height-only overflow for blocks
        Div div = new Div()
                .setHeight(divHeight)
                .setBorder(new SolidBorder(3));
        div.setKeepTogether(true);

        doc.add(div);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void marginCollapseKeptTogetherDivGoesBackTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_marginCollapseKeptTogetherDivGoesBackTest01.pdf";
        String outFile = DESTINATION_FOLDER + "marginCollapseKeptTogetherDivGoesBackTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        Div div1 = new Div()
                .setMarginBottom(100)
                .setBackgroundColor(ColorConstants.RED)
                .setHeight(300)
                .add(new Paragraph("Bottom margin: 100"));
        doc.add(div1);

        Div div2 = new Div()
                .setMarginTop(300)
                .setHeight(1000)
                .setBackgroundColor(ColorConstants.RED)
                .add(new Paragraph("Top margin: 300"));
        div2.setKeepTogether(true);

        doc.add(div2);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    // TODO DEVSIX-3995 The margin between the divs occupies 100 points instead of 300. After a fix the cmp should be updated
    public void marginCollapseKeptTogetherDivGoesBackTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_marginCollapseKeptTogetherDivGoesBackTest02.pdf";
        String outFile = DESTINATION_FOLDER + "marginCollapseKeptTogetherDivGoesBackTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        Div div1 = new Div()
                .setMarginBottom(300)
                .setBackgroundColor(ColorConstants.RED)
                .setHeight(300)
                .add(new Paragraph("Bottom margin: 300"));
        doc.add(div1);
        Div div2 = new Div()
                .setMarginTop(100)
                .setHeight(1000)
                .setBackgroundColor(ColorConstants.RED)
                .add(new Paragraph("Top margin: 100"));
        div2.setKeepTogether(true);

        doc.add(div2);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherNotEmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherNotEmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherNotEmptyPageTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        // Make page not empty to trigger KEEP_TOGETHER actual processing
        doc.add(new Paragraph("Just some content to make this page not empty."));

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        Div innerDiv = new Div();
        innerDiv.setBackgroundColor(ColorConstants.RED);
        innerDiv.setHeight(innerDivHeight);

        // Set KEEP_TOGETHER on inner div
        innerDiv.setKeepTogether(true);

        doc.add(innerDiv);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherOnFirstInnerElementNotEmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherOnFirstInnerElementNotEmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherOnFirstInnerElementNotEmptyPageTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // Make page not empty to trigger KEEP_TOGETHER actual processing
        doc.add(new Paragraph("Just some content to make this page not empty."));

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        Div innerDiv = new Div();
        innerDiv.setBackgroundColor(ColorConstants.RED);
        innerDiv.setHeight(innerDivHeight);

        // Set KEEP_TOGETHER on inner div
        innerDiv.setKeepTogether(true);

        Div outerDiv = new Div();
        outerDiv.add(innerDiv);

        outerDiv.add(new Div().setHeight(200).setBackgroundColor(ColorConstants.BLUE));

        doc.add(outerDiv);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginCollapseKeptTogetherGoesOnNextAreaTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_marginCollapseKeptTogetherGoesOnNextAreaTest01.pdf";
        String outFile = DESTINATION_FOLDER + "marginCollapseKeptTogetherGoesOnNextAreaTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        Div div1 = new Div()
                .setMarginBottom(300)
                .setBackgroundColor(ColorConstants.RED)
                .setHeight(300)
                .add(new Paragraph("Bottom margin: 300"));
        doc.add(div1);

        Div div2 = new Div()
                .setMarginTop(100)
                .setHeight(300)
                .setBackgroundColor(ColorConstants.RED)
                .add(new Paragraph("Top margin: 100"));
        div2.setKeepTogether(true);

        doc.add(div2);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginCollapseKeptTogetherGoesOnNextAreaTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_marginCollapseKeptTogetherGoesOnNextAreaTest02.pdf";
        String outFile = DESTINATION_FOLDER + "marginCollapseKeptTogetherGoesOnNextAreaTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        Div div1 = new Div()
                .setMarginBottom(100)
                .setBackgroundColor(ColorConstants.RED)
                .setHeight(300)
                .add(new Paragraph("Bottom margin: 100"));
        doc.add(div1);

        Div div2 = new Div()
                .setMarginTop(300)
                .setHeight(300)
                .setBackgroundColor(ColorConstants.RED)
                .add(new Paragraph("Top margin: 300"));
        div2.setKeepTogether(true);

        doc.add(div2);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    // TODO DEVSIX-4023 cmp should be updated
    public void keepTogetherOnSecondInnerElementNotEmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherOnSecondInnerElementNotEmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherOnSecondInnerElementNotEmptyPageTest.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // Make page not empty to trigger KEEP_TOGETHER actual processing
        doc.add(new Paragraph("Just some content to make this page not empty."));

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        Div innerDiv = new Div();
        innerDiv.setBackgroundColor(ColorConstants.RED);
        innerDiv.setHeight(innerDivHeight);

        // Set KEEP_TOGETHER on inner div
        innerDiv.setKeepTogether(true);

        Div outerDiv = new Div();
        outerDiv.add(new Div().setHeight(200).setBackgroundColor(ColorConstants.BLUE));

        outerDiv.add(innerDiv);

        doc.add(outerDiv);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherDivTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherDivTest01.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherDivTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // specifying height definitely bigger than page height
        int divHeight = 1000;
        doc.add(createKeptTogetherDivWithSmallFloat(divHeight));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherDivTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherDivTest02.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherDivTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // add some content, so that the following kept together div will be forced to move forward (and then forced to move back)
        doc.add(new Paragraph("Hello"));

        // specifying height definitely bigger than page height
        int divHeight = 1000;
        doc.add(createKeptTogetherDivWithSmallFloat(divHeight));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherParagraphTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherParagraphTest01.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherParagraphTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // specifying height definitely bigger than page height
        int paragraphHeight = 1000;
        doc.add(createKeptTogetherParagraphWithSmallFloat(paragraphHeight));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherParagraphTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherParagraphTest02.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherParagraphTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // add some content, so that the following kept together div will be forced to move forward (and then forced to move back)
        doc.add(new Paragraph("Hello"));

        // specifying height definitely bigger than page height
        int paragraphHeight = 1000;
        doc.add(createKeptTogetherParagraphWithSmallFloat(paragraphHeight));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    // TODO DEVSIX-4023 cmp should be updated
    public void keepTogetherOnInnerElementTestEmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherOnInnerElementTestEmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherOnInnerElementTestEmptyPageTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        boolean first = false;

        addDivs(doc, 200, new Style(), new Style(), first);

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        addDivs(doc, innerDivHeight, new Style(), new Style(), first);
        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    // TODO DEVSIX-4023 cmp should be updated
    public void keepTogetherOnInnerElementMargin01EmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherOnInnerElementMargin01EmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherOnInnerElementMargin01EmptyPageTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        boolean first = false;
        Style inner = new Style().setMargin(40);
        Style predefined = new Style().setMargin(20);

        addDivs(doc, 200, inner, predefined, first);

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        addDivs(doc, innerDivHeight, inner, predefined, first);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    // TODO DEVSIX-4023 cmp should be updated
    public void keepTogetherOnInnerElementMargin02EmptyPageTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherOnInnerElementMargin02EmptyPageTest.pdf";
        String outFile = DESTINATION_FOLDER + "keepTogetherOnInnerElementMargin02EmptyPageTest.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        boolean first = false;
        Style inner = new Style().setMargin(20);
        Style predefined = new Style().setMargin(40);

        addDivs(doc, 200, inner, predefined, first);

        // Specifying height definitely bigger than page height
        float innerDivHeight = pdfDoc.getDefaultPageSize().getHeight() + 200;

        addDivs(doc, innerDivHeight, inner, predefined, first);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherTableTest01() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherTableTest01.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherTableTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // specifying num of rows which will definitely occupy more space than page height
        int numOfRows = 20;
        doc.add(createKeptTogetherTableWithSmallFloat(numOfRows));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void smallFloatInsideKeptTogetherTableTest02() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_smallFloatInsideKeptTogetherTableTest02.pdf";
        String outFile = DESTINATION_FOLDER + "smallFloatInsideKeptTogetherTableTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        Document doc = new Document(pdfDoc);

        // add some content, so that the following kept together div will be forced to move forward (and then forced to move back)
        doc.add(new Paragraph("Hello"));

        // specifying num of rows which will definitely occupy more space than page height
        int numOfRows = 20;
        doc.add(createKeptTogetherTableWithSmallFloat(numOfRows));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherTreeWithParentNotFitOnDocumentTest() throws IOException, InterruptedException {
        String filename = "keepTogetherTreeWithParentNotFitOnDocument.pdf";
        String outFile = DESTINATION_FOLDER + filename;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename;

        try (Document doc = new Document(new PdfDocument(new PdfWriter(outFile)))) {
            doc.getPdfDocument().addNewPage(PageSize.A5.rotate());

            Div main = new Div();

            Div child1 = createChildDivWithText(main, null).setKeepTogether(true);
            createChildDivWithText(child1, BIG_TEXT).setKeepTogether(true);

            Div div1_2 = createChildDivWithText(child1, null).setKeepTogether(true);
            createChildDivWithText(div1_2, "Section A");
            createChildDivWithText(div1_2, null).add(new Paragraph(MEDIUM_TEXT).setFirstLineIndent(20));

            Div child2 = createChildDivWithText(main, null).setKeepTogether(true);
            createChildDivWithText(child2, "Section B");
            createChildDivWithText(child2, null);
            createChildDivWithText(child2, "Lorem ipsum dolor sit amet!");

            doc.add(main);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherSubTreeWithParentNotFitOnDocumentTest() throws IOException, InterruptedException {
        String filename = "keepTogetherSubTreeWithParentNotFitOnDocument.pdf";
        String outFile = DESTINATION_FOLDER + filename;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename;

        try (Document doc = new Document(new PdfDocument(new PdfWriter(outFile)))) {
            doc.getPdfDocument().addNewPage(PageSize.A5.rotate());

            Div main = new Div();

            Div child1 = createChildDivWithText(main, null).setKeepTogether(true);
            createChildDivWithText(child1, BIG_TEXT);

            Div div1_2 = createChildDivWithText(child1, null).setKeepTogether(true);
            createChildDivWithText(div1_2, "Section A");

            createChildDivWithText(div1_2, null).add(new Paragraph(MEDIUM_TEXT).setFirstLineIndent(20));

            // KEEP_TOGETHER is not set here
            Div child2 = createChildDivWithText(main, null);
            createChildDivWithText(child2, "Section B");
            createChildDivWithText(child2, null);
            createChildDivWithText(child2, "Lorem ipsum dolor sit amet!");

            doc.add(main);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherSubTreeWithChildKeepTogetherFalseAndParentNotFitOnDocumentTest()
            throws IOException, InterruptedException {
        String filename = "keepTogetherSubTreeWithChildKeepTogetherFalseAndParentNotFitOnDocument.pdf";
        String outFile = DESTINATION_FOLDER + filename;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename;

        try (Document doc = new Document(new PdfDocument(new PdfWriter(outFile)))) {
            doc.getPdfDocument().addNewPage(PageSize.A5.rotate());

            Div main = new Div();

            Div child1 = createChildDivWithText(main, null).setKeepTogether(true);
            createChildDivWithText(child1, BIG_TEXT);

            Div div1_2 = createChildDivWithText(child1, null).setKeepTogether(false);
            createChildDivWithText(div1_2, "Section A");

            createChildDivWithText(div1_2, null).add(new Paragraph(MEDIUM_TEXT).setFirstLineIndent(20));

            // KEEP_TOGETHER is not set here
            Div child2 = createChildDivWithText(main, null);
            createChildDivWithText(child2, "Section B");
            createChildDivWithText(child2, null);
            createChildDivWithText(child2, "Lorem ipsum dolor sit amet!");

            doc.add(main);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void keepTogetherTreeWithParentNotFitOnPageCanvasTest() throws IOException, InterruptedException {
        String filename = "keepTogetherTreeWithParentNotFitOnPageCanvas.pdf";
        String outFile = DESTINATION_FOLDER + filename;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename;

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile))) {
            PdfPage page = pdfDoc.addNewPage(PageSize.A5.rotate());
            Rectangle rectangle = new Rectangle(10, 10, 500, 350);
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            try (Canvas canvas = new Canvas(pdfCanvas, rectangle)) {
                Div main = new Div();

                Div child1 = createChildDivWithText(main, null).setKeepTogether(true);

                createChildDivWithText(child1, BIG_TEXT).setKeepTogether(true);
                createChildDivWithText(child1, "Section A")
                        .setKeepTogether(true)
                        .add(new Paragraph(MEDIUM_TEXT).setFirstLineIndent(20));

                Div child2 = createChildDivWithText(main, null).setKeepTogether(true);

                createChildDivWithText(child2, "Section B");
                createChildDivWithText(child2, null);
                createChildDivWithText(child2, "Lorem ipsum dolor sit amet!");

                canvas.add(main);
            }
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    //TODO: DEVSIX-4720 (invalid positioning of child element)
    public void keepTogetherInDivWithKidsFloatTest() throws IOException, InterruptedException {
        String filename = "keepTogetherInDivWithKidsFloat.pdf";
        String outFile = DESTINATION_FOLDER + filename;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename;

        try (Document doc = new Document(new PdfDocument(new PdfWriter(outFile)))) {
            doc.getPdfDocument().addNewPage(PageSize.A5.rotate());

            Div main = new Div().setKeepTogether(true);
            main.setBackgroundColor(ColorConstants.LIGHT_GRAY);

            Div child1 = createChildDivWithText(main, SMALL_TEXT);
            child1
                    .setBackgroundColor(ColorConstants.YELLOW)
                    .setWidth(UnitValue.createPercentValue(30))
                    .setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

            Div child2 = createChildDivWithText(main, BIG_TEXT);
            child2
                    .setBackgroundColor(ColorConstants.GREEN)
                    .setWidth(UnitValue.createPercentValue(70))
                    .setProperty(Property.FLOAT, FloatPropertyValue.LEFT);

            Div child3 = createChildDivWithText(main, "Test");
            child3.setBackgroundColor(ColorConstants.ORANGE);

            Div child4 = createChildDivWithText(main, MEDIUM_TEXT);
            child4.setBackgroundColor(ColorConstants.ORANGE);

            doc.add(main);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    //TODO: update cmp file when DEVSIX-4681 will be fixed
    public void floatingElementsInDivAndKeepTogetherElemTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_floatingElementsInDivAndKeepTogetherElem.pdf";
        String outFile = DESTINATION_FOLDER + "floatingElementsInDivAndKeepTogetherElem.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        pdfDoc.addNewPage();

        Document doc = new Document(pdfDoc);

        Div mainDiv = new Div();

        Image first = new Image(ImageDataFactory.create(SOURCE_FOLDER + "1.png"));
        first.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        first.setHeight(350);

        Image second = new Image(ImageDataFactory.create(SOURCE_FOLDER + "2.png"));
        second.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        second.setHeight(350);

        mainDiv.add(first);
        mainDiv.add(second);

        doc.add(mainDiv);
        doc.add(new Paragraph("Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! ")
                .setKeepTogether(true).setFontSize(24));

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    //TODO: update cmp file when DEVSIX-4681 will be fixed
    public void floatingEmptyElementsInDivAndKeepTogetherElemTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_floatingEmptyElementsInDivAndKeepTogetherElem.pdf";
        String outFile = DESTINATION_FOLDER + "floatingEmptyElementsInDivAndKeepTogetherElem.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        pdfDoc.addNewPage(PageSize.A5.rotate());

        Document doc = new Document(pdfDoc);

        Div mainDiv = new Div();

        Paragraph p1 = new Paragraph();
        p1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph p2 = new Paragraph();
        p2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph ktp = new Paragraph("Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
        ).setKeepTogether(true).setFontSize(20);

        mainDiv.add(p1);
        mainDiv.add(p2);

        doc.add(mainDiv);
        doc.add(ktp);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void floatingEmptyElementsAndKeepTogetherElemTest() throws IOException, InterruptedException {
        String cmpFileName = SOURCE_FOLDER + "cmp_floatingEmptyElementsAndKeepTogetherElem.pdf";
        String outFile = DESTINATION_FOLDER + "floatingEmptyElementsAndKeepTogetherElem.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFile));
        pdfDoc.addNewPage(PageSize.A5.rotate());

        Document doc = new Document(pdfDoc);

        Paragraph p1 = new Paragraph();
        p1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph p2 = new Paragraph();
        p2.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph ktp = new Paragraph("Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
                + "Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! Hello, iText! "
        ).setKeepTogether(true).setFontSize(20);

        doc.add(p1);
        doc.add(p2);
        doc.add(ktp);

        doc.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2, logLevel = LogLevelConstants.WARN)
    })
    public void pWithKeepTogetherPlusHugeImgChildTest() throws IOException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "pWithKeepTogetherPlusHugeImgChild.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_pWithKeepTogetherPlusHugeImgChild.pdf";

        try(PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            Document doc = new Document(pdfDoc);

            Paragraph p = new Paragraph();
            p.setKeepTogether(true);
            p.add(new Paragraph("Short text, after will be huge image"));
            p.add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "huge.png")));
            doc.add(p);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 3, logLevel = LogLevelConstants.WARN)
    })
    public void verifyThatDisablingKeepTogetherDoesntChangeRelayoutTest() throws IOException, InterruptedException {
        generateCmpWithKeepTogetherAndCheckResult(true);
        generateCmpWithKeepTogetherAndCheckResult(false);
    }

    private void generateCmpWithKeepTogetherAndCheckResult(boolean doRelayout) throws IOException, InterruptedException {
        String fileName = doRelayout ? "keepTogetherWithRelayout.pdf" : "keepTogetherWithoutRelayout.pdf";
        String outFile = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherNotDependingOnLayout.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFile))) {
            Document doc = new Document(pdfDoc, pdfDoc.getDefaultPageSize(), false);
            doc.add(new Paragraph(BIG_TEXT));
            Div divParent = new Div();
            divParent.add(new Paragraph(SMALL_TEXT));
            Div div = new Div();
            div.setBorder(new SolidBorder(3));
            div.setKeepTogether(true);
            div.add(new Paragraph(BIG_TEXT));
            div.add(new Paragraph(BIG_TEXT));
            div.add(new Paragraph(BIG_TEXT));
            divParent.add(div);
            doc.add(divParent);

            doc.flush();
            if (doRelayout) {
                doc.relayout();
            }
            doc.close();
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private Div createChildDivWithText(Div parent, String text) {
        Div child = new Div();
        if (text != null) {
            child.add(new Paragraph(text));
        }
        parent.add(child);

        return child;
    }

    private static Div createKeptTogetherDivWithSmallFloat(int divHeight) {
        // test keep-together processing on height-only overflow for blocks
        Div div = new Div()
                .setHeight(divHeight)
                .setBorder(new SolidBorder(3));
        div.setKeepTogether(true);

        Div floatDiv = new Div();
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        floatDiv.setHeight(50);
        floatDiv.setWidth(50);
        floatDiv.setBackgroundColor(ColorConstants.RED);

        div.add(floatDiv);

        return div;
    }

    private static Paragraph createKeptTogetherParagraphWithSmallFloat(int paragraphHeight) {
        // test keep-together processing on height-only overflow for blocks
        Paragraph paragraph = new Paragraph()
                .setHeight(paragraphHeight)
                .setBorder(new SolidBorder(3));
        paragraph.setKeepTogether(true);

        Div floatDiv = new Div();
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        floatDiv.setHeight(50);
        floatDiv.setWidth(50);
        floatDiv.setBackgroundColor(ColorConstants.RED);

        paragraph.add(floatDiv);

        return paragraph;
    }

    private static Table createKeptTogetherTableWithSmallFloat(int numOfRows) {
        // test keep-together processing on height-only overflow for blocks
        Table table = new Table(1)
                .setBorder(new SolidBorder(3))
                .useAllAvailableWidth();
        table.setKeepTogether(true);

        Div floatDiv = new Div();
        floatDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        floatDiv.setHeight(50);
        floatDiv.setWidth(50);
        floatDiv.setBackgroundColor(ColorConstants.RED);

        for (int i = 0; i < numOfRows; i++) {
            table.addCell(new Cell().add(floatDiv));
        }

        return table;
    }

    private static void addDivs(Document doc, float innerDivHeight, Style inner, Style predefined, boolean first) {
        // Make page not empty to trigger KEEP_TOGETHER actual processing
        doc.add(new Paragraph("Just some content to make this page not empty."));

        Div innerDiv = new Div();
        innerDiv.setBackgroundColor(ColorConstants.RED);
        innerDiv.setHeight(innerDivHeight);

        // Set KEEP_TOGETHER on inner div
        innerDiv.setKeepTogether(true);

        innerDiv.setHeight(innerDivHeight);

        innerDiv.addStyle(inner);

        Div outerDiv = new Div();
        outerDiv.setBorder(new SolidBorder(50));

        if (first) {
            outerDiv.add(innerDiv);
        }

        outerDiv.add(new Div().setHeight(200).setBackgroundColor(ColorConstants.BLUE).addStyle(predefined));

        if (!first) {
            outerDiv.add(innerDiv);
        }

        doc.add(outerDiv);

        doc.add(new AreaBreak());
    }

    private static class KeepTogetherDiv extends Div {
        @Override
        public <T1> T1 getDefaultProperty(int property) {
            if (property == Property.KEEP_TOGETHER) {
                return (T1) (Object) true;
            }
            return super.<T1>getDefaultProperty(property);
        }
    }

    private static class SpecialOddPagesDocumentRenderer extends DocumentRenderer {
        private PageSize firstPageSize;

        public SpecialOddPagesDocumentRenderer(Document document, PageSize firstPageSize) {
            super(document);
            this.firstPageSize = new PageSize(firstPageSize);
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize newPageSize = null;
            switch (document.getPdfDocument().getNumberOfPages() % 2) {
                case 0:
                    newPageSize = firstPageSize;
                    break;
                case 1:
                default:
                    newPageSize = PageSize.A4;
                    break;
            }
            return super.addNewPage(newPageSize);
        }
    }
}
