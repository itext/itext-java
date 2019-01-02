/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.RoundDotsBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class BlockTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/BlockTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/BlockTest/";

    private static final String textByronNarrow =
            "When a man hath no freedom to fight for at home, " +
                    "Let him combat for that of his neighbours; " +
                    "Let him think of the glories of Greece and of Rome, " +
                    "And get knocked on the head for his labours. " +
                    "\n" +
                    "To do good to Mankind is the chivalrous plan, " +
                    "And is always as nobly requited; " +
                    "Then battle for Freedom wherever you can, " +
                    "And, if not shot or hanged, you'll get knighted.";

    private static final String textByron =
            "When a man hath no freedom to fight for at home,\n" +
                    "    Let him combat for that of his neighbours;\n" +
                    "Let him think of the glories of Greece and of Rome,\n" +
                    "    And get knocked on the head for his labours.\n" +
                    "\n" +
                    "To do good to Mankind is the chivalrous plan,\n" +
                    "    And is always as nobly requited;\n" +
                    "Then battle for Freedom wherever you can,\n" +
                    "    And, if not shot or hanged, you'll get knighted.";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    @Test
    public void blockWithSetHeightProperties01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockWithSetHeightProperties01.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithSetHeightProperties01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph(textByron);
        for (int i = 0; i < 10; i++) {
            p.add(textByron);
        }
        p.setBorder(new SolidBorder(0.5f));

        doc.add(new Paragraph("Default layout:"));
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set shorter than needed:"));
        p.setHeight(1300);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's min height is set shorter than needed:"));
        p.deleteOwnProperty(Property.HEIGHT);
        p.setMinHeight(1300);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's max height is set shorter than needed:"));
        p.deleteOwnProperty(Property.MIN_HEIGHT);
        p.setMaxHeight(1300);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set bigger than needed:"));
        p.deleteOwnProperty(Property.MAX_HEIGHT);
        p.setHeight(2500);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's min height is set bigger than needed:"));
        p.deleteOwnProperty(Property.HEIGHT);
        p.setMinHeight(2500);
        doc.add(p);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's max height is set bigger than needed:"));
        p.deleteOwnProperty(Property.MIN_HEIGHT);
        p.setMaxHeight(2500);
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 2)
    })
    @Test
    public void blockWithSetHeightProperties02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockWithSetHeightProperties02.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithSetHeightProperties02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph(textByron);
        Div div = new Div();
        div.setBorder(new SolidBorder(ColorConstants.RED, 2));
        for (int i = 0; i < 5; i++) {
            div.add(p);
        }
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        Image image = new Image(xObject, 300);
        div.add(image);

        doc.add(new Paragraph("Default layout:"));
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's height is set shorter than needed:"));
        div.setHeight(1000);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's min height is set shorter than needed:"));
        div.deleteOwnProperty(Property.HEIGHT);
        div.setMinHeight(1000);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's max height is set shorter than needed:"));
        div.deleteOwnProperty(Property.MIN_HEIGHT);
        div.setMaxHeight(1000);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's height is set bigger than needed:"));
        div.deleteOwnProperty(Property.MAX_HEIGHT);
        div.setHeight(2500);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's min height is set bigger than needed:"));
        div.deleteOwnProperty(Property.HEIGHT);
        div.setMinHeight(2500);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Div's max height is set bigger than needed:"));
        div.deleteOwnProperty(Property.MIN_HEIGHT);
        div.setMaxHeight(2500);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 3)
    })
    @Test
    public void blockWithSetHeightProperties03() throws IOException, InterruptedException {
        //Relative height declaration tests
        String outFileName = destinationFolder + "blockWithSetHeightProperties03.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithSetHeightProperties03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        float parentHeight = 650;

        Div d = new Div();
        d.add(new Paragraph(textByron));
        d.setBorder(new SolidBorder(0.5f));


        doc.add(new Paragraph("Default layout:"));
        Div parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 80% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.HEIGHT, UnitValue.createPercentValue(80f));
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 150% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.HEIGHT, UnitValue.createPercentValue(150f));
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 10% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.HEIGHT, UnitValue.createPercentValue(10f));
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 40% of the parent and two paragraphs are added"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.HEIGHT, UnitValue.createPercentValue(40f));
        parent.add(d);
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 50% of the parent and two paragraphs are added"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.HEIGHT, UnitValue.createPercentValue(50f));
        parent.add(d);
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's min height is set to 80% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.setProperty(Property.MIN_HEIGHT, UnitValue.createPercentValue(80f));
        parent.add(d);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's max height is set to 30% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        d.deleteOwnProperty(Property.MIN_HEIGHT);//Min-height trumps max-height, so we have to remove it when re-using the div
        d.setProperty(Property.MAX_HEIGHT, UnitValue.createPercentValue(30f));
        parent.add(d);
        doc.add(parent);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT, count = 3)
    })
    @Test
    public void blockWithSetHeightProperties04() throws IOException, InterruptedException {
        //Relative height declaration tests
        String outFileName = destinationFolder + "blockWithSetHeightProperties04.pdf";
        String cmpFileName = sourceFolder + "cmp_blockWithSetHeightProperties04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        float parentHeight = 650;

        Paragraph p = new Paragraph();
        p.add(new Text(textByron));
        p.setBorder(new SolidBorder(0.5f));

        doc.add(new Paragraph("Default layout:"));
        Div parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 80% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.HEIGHT, UnitValue.createPercentValue(80f));
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 150% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.HEIGHT, UnitValue.createPercentValue(150f));
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 10% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.HEIGHT, UnitValue.createPercentValue(10f));
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 40% of the parent and two paragraphs are added"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.HEIGHT, UnitValue.createPercentValue(40f));
        parent.add(p);
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's height is set to 50% of the parent and two paragraphs are added"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.HEIGHT, UnitValue.createPercentValue(50f));
        parent.add(p);
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());


        doc.add(new Paragraph("Paragraph's min height is set to 80% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.MIN_HEIGHT, UnitValue.createPercentValue(80f));
        parent.add(p);
        doc.add(parent);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("Paragraph's max height is set to 30% of the parent"));
        parent = new Div();
        parent.setHeight(parentHeight);
        parent.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.deleteOwnProperty(Property.MIN_HEIGHT);//Min-height trumps max, so we have to remove it when re-using the paragraph
        p.setProperty(Property.MAX_HEIGHT, UnitValue.createPercentValue(30f));
        parent.add(p);
        doc.add(parent);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    @Test
    // TODO DEVSIX-1373
    public void overflowTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph explanation = new Paragraph("In this sample iText will not try to fit text in container's width, because overflow property is set. However no text is hidden.");
        doc.add(explanation);

        Paragraph p = new Paragraph(textByronNarrow);
        p.setWidth(200);
        p.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.HIDDEN);

        Div div = new Div();
        div.setWidth(100);
        div.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        div.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);

        div.add(p);

        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        p.setWidth(200);
        p.setHeight(100);

        p.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setBackgroundColor(ColorConstants.YELLOW);
        for (int i = 0; i < 10; i++) {
            p.add(textByronNarrow);
        }
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

        doc.add(p);

        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void overflowTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        p.setWidth(1400);
        p.setHeight(1400);
        p.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setBackgroundColor(ColorConstants.YELLOW);
        for (int i = 0; i < 100; i++) {
            p.add(textByronNarrow);
        }
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);

        doc.add(p);

        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Ignore("DEVSIX-1375")
    @Test
    public void overflowTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Image image = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        image.setWidth(200);

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        p.setRotationAngle(Math.PI / 2);
        p.setWidth(100);
        p.setHeight(100);
        p.setBorder(new SolidBorder(ColorConstants.BLUE, 1));
        p.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        p.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);

        p.add(image);

        doc.add(p);

        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Ignore("DEVSIX-1373")
    @Test
    public void overflowTest05() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        div.setWidth(100);
        div.setHeight(150);
        div.setBackgroundColor(ColorConstants.GREEN);
        div.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
        div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);


        List list = new List();
        list.add("Make Greeeeeeeeeetzky Great Again");
        list.add("Greeeeeeeeeetzky Great Again Make");
        list.add("Great Again Make Greeeeeeeeeetzky");
        list.add("Again Make Greeeeeeeeeetzky Great");


        div.add(list);
        doc.add(div);

        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Ignore("DEVSIX-1373")
    @Test
    public void overflowTest06() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "overflowTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_overflowTest06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        div.setWidth(100);
        div.setHeight(100);
        div.setBackgroundColor(ColorConstants.GREEN);
        div.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

        div.add(new Paragraph(textByron));

        doc.add(div);

        doc.add(new Paragraph("Hello!!!").setBackgroundColor(ColorConstants.RED));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void blockFillAvailableArea01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "blockFillAvailableArea01.pdf";
        String cmpFileName = sourceFolder + "cmp_blockFillAvailableArea01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted." +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted." +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";
        textByron = textByron + textByron;

        Document doc = new Document(pdfDocument);

        DeviceRgb blue = new DeviceRgb(80, 114, 153);
        Div text = new Div().add(new Paragraph(textByron));
        Div image = new Div().add(new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg")).setHeight(500).setAutoScaleWidth(true));


        doc.add(new Div().add(new Paragraph("Fill on split").setFontSize(30).setFontColor(blue).setTextAlignment(TextAlignment.CENTER))
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setFillAvailableArea(true))
                .add(new AreaBreak());

        doc.add(new Paragraph("text").setFontSize(18).setFontColor(blue));
        Div div = createDiv(text, textByron, blue, true,
                false, true);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("image").setFontSize(18).setFontColor(blue));
        div = createDiv(image, textByron, blue, false,
                false, true);
        doc.add(div);
        doc.add(new AreaBreak());


        doc.add(new Div().add(new Paragraph("Fill always").setFontSize(30).setFontColor(blue).setTextAlignment(TextAlignment.CENTER))
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setFillAvailableArea(true))
                .add(new AreaBreak());

        doc.add(new Paragraph("text").setFontSize(18).setFontColor(blue));
        div = createDiv(text, textByron, blue, true,
                true, false);
        doc.add(div);

        doc.add(new Paragraph("image").setFontSize(18).setFontColor(blue));
        div = createDiv(image, textByron, blue, false,
                true, false);
        doc.add(div);


        doc.add(new Div().add(new Paragraph("No fill").setFontSize(30).setFontColor(blue).setTextAlignment(TextAlignment.CENTER))
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setFillAvailableArea(true))
                .add(new AreaBreak());

        doc.add(new Paragraph("text").setFontSize(18).setFontColor(blue));
        div = createDiv(text, textByron, blue, true,
                false, false);
        doc.add(div);
        doc.add(new AreaBreak());

        doc.add(new Paragraph("image").setFontSize(18).setFontColor(blue));
        div = createDiv(image, textByron, blue, false,
                false, false);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore("DEVSIX-1092")
    public void marginsBordersPaddingOverflow01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "marginsBordersPaddingOverflow01.pdf";
        String cmpFileName = sourceFolder + "cmp_marginsBordersPaddingOverflow01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        div.setHeight(760).setBackgroundColor(ColorConstants.DARK_GRAY);
        doc.add(div);

        // TODO overflow of this div on second page is of much bigger height than 1pt
        Div div1 = new Div().setMarginTop(42).setMarginBottom(42)
                .setBackgroundColor(ColorConstants.BLUE).setHeight(1);
        doc.add(div1);


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore("DEVSIX-1092")
    public void marginsBordersPaddingOverflow02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "marginsBordersPaddingOverflow02.pdf";
        String cmpFileName = sourceFolder + "cmp_marginsBordersPaddingOverflow02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);


        // TODO div with fixed height is bigger than 60pt
        Div div = new Div();
        div.setHeight(60).setBackgroundColor(ColorConstants.DARK_GRAY);
        Div div1 = new Div()
                .setMarginTop(200).setMarginBottom(200)
                .setBorder(new SolidBorder(6));
        div.add(div1);
        doc.add(div);


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore("DEVSIX-1092")
    public void marginsBordersPaddingOverflow03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "marginsBordersPaddingOverflow03.pdf";
        String cmpFileName = sourceFolder + "cmp_marginsBordersPaddingOverflow03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        div.setHeight(710).setBackgroundColor(ColorConstants.DARK_GRAY);
        doc.add(div);

        // TODO this element is below first page visible area
        Div div1 = new Div()
                .setMarginTop(200).setMarginBottom(200)
                .setBorder(new SolidBorder(6));
        doc.add(div1);

        doc.add(new AreaBreak());
        // TODO same with this one the second page
        SolidBorder border = new SolidBorder(400);
        Div div2 = new Div()
                .setBorderTop(border)
                .setBorderBottom(border);

        doc.add(div);
        doc.add(div2);

        doc.add(new AreaBreak());
        // TODO same with this one the third page
        Div div3 = new Div()
                .setBorder(new SolidBorder(6))
                .setPaddingTop(400).setPaddingBottom(400);

        doc.add(div);
        doc.add(div3);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();

        Style divStyle = new Style()
                .setHeight(500)
                .setWidth(500)
                .setBackgroundColor(ColorConstants.BLUE);
        divStyle.setBorderRadius(new BorderRadius(50));

        // solid
        div.addStyle(divStyle);
        div
                .setBorderTop(new SolidBorder(ColorConstants.RED, 20))
                .setBorderRight(new SolidBorder(ColorConstants.YELLOW, 20));
        doc.add(div);
        doc.add(new AreaBreak());

        // dashed
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderTop(new DashedBorder(ColorConstants.RED, 20))
                .setBorderRight(new DashedBorder(ColorConstants.YELLOW, 20));
        doc.add(div);
        doc.add(new AreaBreak());

        // dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderTop(new DottedBorder(ColorConstants.RED, 20))
                .setBorderRight(new DottedBorder(ColorConstants.YELLOW, 20));
        doc.add(div);
        doc.add(new AreaBreak());

        // round dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderTop(new RoundDotsBorder(ColorConstants.RED, 20))
                .setBorderRight(new RoundDotsBorder(ColorConstants.YELLOW, 20));
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        // width and height > 2 * radius
        Div div = new Div();
        div.setHeight(500).setWidth(500)
                .setBackgroundColor(ColorConstants.GREEN)
                .setBorderRadius(new BorderRadius(100));
        doc.add(div);
        doc.add(new AreaBreak());

        // 2 * radius > width and height > radius
        div = new Div();
        div.setHeight(150).setWidth(150)
                .setBackgroundColor(ColorConstants.GREEN)
                .setBorderRadius(new BorderRadius(100));
        doc.add(div);
        doc.add(new AreaBreak());

        // radius > width and height

        div = new Div();
        div.setHeight(50).setWidth(50)
                .setBackgroundColor(ColorConstants.GREEN)
                .setBorderRadius(new BorderRadius(100));
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        Style divStyle = new Style()
                .setHeight(500)
                .setWidth(500)
                .setBackgroundColor(ColorConstants.GREEN);
        divStyle.setBorderRadius(new BorderRadius(200));

        // solid
        div.addStyle(divStyle);
        div
                .setBorderLeft(new SolidBorder(ColorConstants.MAGENTA, 100))
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 100))
                .setBorderTop(new SolidBorder(ColorConstants.RED, 100))
                .setBorderRight(new SolidBorder(ColorConstants.BLUE, 100));
        doc.add(div);
        doc.add(new AreaBreak());

        // dashed
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderLeft(new DashedBorder(ColorConstants.MAGENTA, 100))
                .setBorderBottom(new DashedBorder(ColorConstants.BLACK, 100))
                .setBorderTop(new DashedBorder(ColorConstants.RED, 100))
                .setBorderRight(new DashedBorder(ColorConstants.BLUE, 100));
        doc.add(div);
        doc.add(new AreaBreak());

        // dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderLeft(new DottedBorder(ColorConstants.MAGENTA, 100))
                .setBorderBottom(new DottedBorder(ColorConstants.BLACK, 100))
                .setBorderTop(new DottedBorder(ColorConstants.RED, 100))
                .setBorderRight(new DottedBorder(ColorConstants.BLUE, 100));
        doc.add(div);
        doc.add(new AreaBreak());

        // round dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderLeft(new RoundDotsBorder(ColorConstants.MAGENTA, 100))
                .setBorderBottom(new RoundDotsBorder(ColorConstants.BLACK, 100))
                .setBorderTop(new RoundDotsBorder(ColorConstants.RED, 100))
                .setBorderRight(new RoundDotsBorder(ColorConstants.BLUE, 100))
        ;
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();
        Style divStyle = new Style()
                .setHeight(120)
                .setWidth(120)
                .setBackgroundColor(ColorConstants.MAGENTA);
        divStyle
                .setBorderRadius(new BorderRadius(90));

        // solid
        div.addStyle(divStyle);
        div
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 30))
                .setBorderLeft(new SolidBorder(ColorConstants.GREEN, 15))
                .setBorderTop(new SolidBorder(ColorConstants.BLACK, 60))
                .setBorderRight(new SolidBorder(ColorConstants.BLUE, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dashed
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderBottom(new DashedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DashedBorder(ColorConstants.GREEN, 15))
                .setBorderTop(new DashedBorder(ColorConstants.BLACK, 60))
                .setBorderRight(new DashedBorder(ColorConstants.BLUE, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderBottom(new DottedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DottedBorder(ColorConstants.GREEN, 15))
                .setBorderTop(new DottedBorder(ColorConstants.BLACK, 60))
                .setBorderRight(new DottedBorder(ColorConstants.BLUE, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // round dotted
        div = new Div();
        div.addStyle(divStyle);
        div
                .setBorderBottom(new RoundDotsBorder(ColorConstants.RED, 30))
                .setBorderLeft(new RoundDotsBorder(ColorConstants.GREEN, 15))
                .setBorderTop(new RoundDotsBorder(ColorConstants.BLACK, 60))
                .setBorderRight(new RoundDotsBorder(ColorConstants.BLUE, 150));

        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest05() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();

        Style divStyle = new Style()
                .setHeight(460)
                .setWidth(360)
                .setBackgroundColor(ColorConstants.MAGENTA);
        divStyle.setBorderRadius(new BorderRadius(100));

        // solid
        div.addStyle(divStyle);
        div.setBorderBottom(new SolidBorder(ColorConstants.RED, 30))
                .setBorderLeft(new SolidBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new SolidBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new SolidBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dashed
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new DashedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DashedBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new DashedBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new DashedBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dotted
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new DottedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DottedBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new DottedBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new DottedBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // round dotted
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new RoundDotsBorder(ColorConstants.RED, 30))
                .setBorderLeft(new RoundDotsBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new RoundDotsBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new RoundDotsBorder(ColorConstants.YELLOW, 150));
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void borderRadiusTest06() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderRadiusTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_borderRadiusTest06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Div div = new Div();

        Style divStyle = new Style()
                .setHeight(460)
                .setWidth(360)
                .setBackgroundColor(ColorConstants.MAGENTA);
        divStyle.setBorderRadius(new BorderRadius(40, 120));

        // solid
        div.addStyle(divStyle);
        div.setBorderBottom(new SolidBorder(ColorConstants.RED, 30))
                .setBorderLeft(new SolidBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new SolidBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new SolidBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dashed
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new DashedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DashedBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new DashedBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new DashedBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // dotted
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new DottedBorder(ColorConstants.RED, 30))
                .setBorderLeft(new DottedBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new DottedBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new DottedBorder(ColorConstants.YELLOW, 150));
        doc.add(div);
        doc.add(new AreaBreak());

        // round dotted
        div = new Div();
        div.addStyle(divStyle);
        div.setBorderBottom(new RoundDotsBorder(ColorConstants.RED, 30))
                .setBorderLeft(new RoundDotsBorder(ColorConstants.BLUE, 15))
                .setBorderTop(new RoundDotsBorder(ColorConstants.GREEN, 60))
                .setBorderRight(new RoundDotsBorder(ColorConstants.YELLOW, 150));
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore("DEVSIX-1897")
    public void paragraphVerticalAlignmentTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "paragraphVerticalAlignmentTest01.pdf";
        String cmpFileName = sourceFolder + "paragraphVerticalAlignmentTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);
        FontProvider fontProvider = new FontProvider();
        fontProvider.addStandardPdfFonts();
        doc.setFontProvider(fontProvider);

        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        doc.add(new Paragraph(loremIpsum).setHeight(100).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(new SolidBorder(3)).setFontFamily(StandardFonts.TIMES_ROMAN));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    private Div createDiv(Div innerOverflowDiv, String text, DeviceRgb backgroundColor, boolean keepTogether, boolean fillAlways, boolean fillOnSplit) {
        Div div = new Div().setBorder(new DoubleBorder(10)).setBackgroundColor(new DeviceRgb(216, 243, 255)).setFillAvailableAreaOnSplit(fillOnSplit).setFillAvailableArea(fillAlways);
        div.add(new Paragraph(text));
        div.add(innerOverflowDiv
                .setKeepTogether(keepTogether));

        if (backgroundColor != null) {
            innerOverflowDiv.setBackgroundColor(backgroundColor);
        }
        return div;
    }
}
