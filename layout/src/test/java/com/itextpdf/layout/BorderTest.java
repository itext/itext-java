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
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.GrooveBorder;
import com.itextpdf.layout.borders.InsetBorder;
import com.itextpdf.layout.borders.OutsetBorder;
import com.itextpdf.layout.borders.RidgeBorder;
import com.itextpdf.layout.borders.RoundDotsBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;

@Category(IntegrationTest.class)
public class BorderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/BorderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/BorderTest/";
    public static final String cmpPrefix = "cmp_";

    String fileName;
    String outFileName;
    String cmpFileName;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleBordersTest() throws IOException, InterruptedException {
        fileName = "simpleBordersTest.pdf";
        Document doc = createDocument();

        List list = new List();

        ListItem solidBorderItem = new ListItem("solid");
        solidBorderItem.setBorder(new SolidBorder(ColorConstants.RED, 6)).setMarginBottom(5);
        solidBorderItem.setBorderTop(new SolidBorder(ColorConstants.BLUE, 10));
        list.add(solidBorderItem);

        ListItem doubleBorderItem = new ListItem("double");
        doubleBorderItem.setBorder(new DoubleBorder(ColorConstants.RED, 10)).setMarginBottom(5);
        doubleBorderItem.setBorderRight(new DoubleBorder(ColorConstants.BLUE, 6));
        list.add(doubleBorderItem);

        ListItem dashedBorderItem = new ListItem("dashed");
        dashedBorderItem.setBorder(new DashedBorder(ColorConstants.GRAY, 2)).setMarginBottom(5);
        dashedBorderItem.setBorderBottom(new DashedBorder(ColorConstants.BLACK, 4));
        list.add(dashedBorderItem);

        ListItem dottedBorderItem = new ListItem("dotted");
        dottedBorderItem.setBorder(new DottedBorder(ColorConstants.BLACK, 3)).setMarginBottom(5);
        dottedBorderItem.setBorderLeft(new DottedBorder(ColorConstants.GRAY, 6));
        list.add(dottedBorderItem);

        ListItem roundDotsBorderItem = new ListItem("round dots");
        roundDotsBorderItem.setBorder(new RoundDotsBorder(ColorConstants.LIGHT_GRAY, 3)).setMarginBottom(5);
        roundDotsBorderItem.setBorderLeft(new RoundDotsBorder(ColorConstants.BLUE, 5));
        list.add(roundDotsBorderItem);

        doc.add(list);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borders3DTest() throws IOException, InterruptedException {
        fileName = "borders3DTest.pdf";
        Document doc = createDocument();

        List list = new List();

        ListItem grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(2)).setMarginBottom(5).setWidth(100);
        list.add(grooveBorderItem);

        ListItem ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(2)).setMarginBottom(5).setWidth(100);
        list.add(ridgeBorderItem);

        ListItem insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(1)).setMarginBottom(5).setWidth(100);
        list.add(insetBorderItem);

        ListItem outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(1)).setMarginBottom(5).setWidth(100);
        list.add(outsetBorderItem);

        doc.add(list);

        Paragraph emptyParagraph = new Paragraph("\n");
        doc.add(emptyParagraph);


        DeviceRgb blueRgb = new DeviceRgb(0, 0, 200);
        DeviceRgb greenRgb = new DeviceRgb(0, 255, 0);
        DeviceCmyk magentaCmyk = new DeviceCmyk(0, 100, 0, 0);
        DeviceCmyk yellowCmyk = new DeviceCmyk(0, 0, 100, 0);

        list = new List();

        grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(blueRgb, 2)).setMarginBottom(5).setWidth(100);
        list.add(grooveBorderItem);

        ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(greenRgb, 2)).setMarginBottom(5).setWidth(100);
        list.add(ridgeBorderItem);

        insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(magentaCmyk, 1)).setMarginBottom(5).setWidth(100);
        list.add(insetBorderItem);

        outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(yellowCmyk, 1)).setMarginBottom(5).setWidth(100);
        list.add(outsetBorderItem);

        doc.add(list);

        emptyParagraph = new Paragraph("\n");
        doc.add(emptyParagraph);


        list = new List();

        grooveBorderItem = new ListItem("groove");
        grooveBorderItem.setBorder(new GrooveBorder(yellowCmyk, 8)).setMarginBottom(5);
        list.add(grooveBorderItem);

        ridgeBorderItem = new ListItem("ridge");
        ridgeBorderItem.setBorder(new RidgeBorder(magentaCmyk, 8)).setMarginBottom(5);
        list.add(ridgeBorderItem);

        insetBorderItem = new ListItem("inset");
        insetBorderItem.setBorder(new InsetBorder(greenRgb, 8)).setMarginBottom(5);
        list.add(insetBorderItem);

        outsetBorderItem = new ListItem("outset");
        outsetBorderItem.setBorder(new OutsetBorder(blueRgb, 8)).setMarginBottom(5);
        list.add(outsetBorderItem);

        doc.add(list);

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderSidesTest() throws IOException, InterruptedException {
        fileName = "borderSidesTest.pdf";
        Document doc = createDocument();

        String text =
                "<p class=\"none\"  >No border.</p>\n" +
                        "<p class=\"dotted\">A dotted border.</p>\n" +
                        "<p class=\"dashed\">A dashed border.</p>\n" +
                        "<p class=\"solid\" >A solid border.</p>\n" +
                        "<p class=\"double\">A double border.</p>\n" +
                        "<p class=\"groove\">A groove border.</p>\n" +
                        "<p class=\"ridge\" >A ridge border.</p>\n" +
                        "<p class=\"inset\" >An inset border.</p>\n" +
                        "<p class=\"outset\">An outset border.</p>\n" +
                        "<p class=\"hidden\">A hidden border.</p>";
        Paragraph p = new Paragraph(text);

        p.setBorderTop(new SolidBorder(DeviceCmyk.MAGENTA, 4));
        p.setBorderRight(new DoubleBorder(ColorConstants.RED, 6));
        p.setBorderBottom(new RoundDotsBorder(DeviceCmyk.CYAN, 2));
        p.setBorderLeft(new DashedBorder(DeviceGray.BLACK, 3));

        doc.add(p);

        doc.add(new Paragraph(text).setBorderTop(new SolidBorder(DeviceCmyk.MAGENTA, 8)));
        doc.add(new Paragraph(text).setBorderRight(new DoubleBorder(ColorConstants.RED, 4)));
        doc.add(new Paragraph(text).setBorderBottom(new RoundDotsBorder(DeviceCmyk.CYAN, 3)));
        doc.add(new Paragraph(text).setBorderLeft(new DashedBorder(DeviceGray.BLACK, 5)));
        doc.add(new Paragraph(text).setBorder(new DottedBorder(DeviceGray.BLACK, 1)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    public void borderBoxTest() throws IOException, InterruptedException {
        fileName = "borderBoxTest.pdf";
        Document doc = createDocument();

        String textBefore = "At the mid-oceanic ridges, two tectonic plates diverge from one another as new oceanic crust is formed by the cooling and " +
                "solidifying of hot molten rock. Because the crust is very thin at these ridges due to the pull of the tectonic plates, the release of " +
                "pressure leads to adiabatic expansion and the partial melting of the mantle, causing volcanism and creating new oceanic crust. Most divergent " +
                "plate boundaries are at the bottom of the oceans; therefore, most volcanic activity is submarine, forming new seafloor. Black smokers (also " +
                "known as deep sea vents) are an example of this kind of volcanic activity. Where the mid-oceanic ridge is above sea-level, volcanic islands are " +
                "formed, for example, Iceland.";

        String text = "Earth's volcanoes occur because its crust is broken into 17 major, rigid tectonic plates that float on a hotter," +
                " softer layer in its mantle. Therefore, on Earth, volcanoes are generally found where tectonic plates are diverging or converging. " +
                "For example, a mid-oceanic ridge, such as the Mid-Atlantic Ridge, has volcanoes caused by divergent tectonic plates pulling apart;" +
                " the Pacific Ring of Fire has volcanoes caused by convergent tectonic plates coming together. Volcanoes can also form where there is " +
                "stretching and thinning of the crust's interior plates, e.g., in the East African Rift and the Wells Gray-Clearwater volcanic field and " +
                "Rio Grande Rift in North America. This type of volcanism falls under the umbrella of \"plate hypothesis\" volcanism. Volcanism away " +
                "from plate boundaries has also been explained as mantle plumes. These so-called \"hotspots\", for example Hawaii, are postulated to arise " +
                "from upwelling diapirs with magma from the core-mantle boundary, 3,000 km deep in the Earth. Volcanoes are usually not created where two " +
                "tectonic plates slide past one another.";

        String textAfter = "Subduction zones are places where two plates, usually an oceanic plate and a continental plate, collide. In this case, the oceanic " +
                "plate subducts, or submerges under the continental plate forming a deep ocean trench just offshore. In a process called flux melting, water released" +
                " from the subducting plate lowers the melting temperature of the overlying mantle wedge, creating magma. This magma tends to be very viscous due to " +
                "its high silica content, so often does not reach the surface and cools at depth. When it does reach the surface, a volcano is formed. Typical examples" +
                " of this kind of volcano are Mount Etna and the volcanoes in the Pacific Ring of Fire.";

        doc.add(new Paragraph(textBefore).setMargins(25, 60, 70, 80));

        Paragraph p = new Paragraph(text).setBackgroundColor(ColorConstants.GRAY);
        p.setMargins(25, 60, 70, 80);
        p.setBorderLeft(new DoubleBorder(ColorConstants.RED, 25));
        p.setBorder(new DoubleBorder(ColorConstants.BLACK, 6));
        doc.add(p);

        doc.add(new Paragraph(textAfter).setBorder(new DottedBorder(ColorConstants.BLACK, 3)).setBorderRight(new DottedBorder(ColorConstants.BLACK, 12)));

        closeDocumentAndCompareOutputs(doc);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void rotatedBordersTest() throws IOException, InterruptedException {
        fileName = "rotatedBordersTest.pdf";
        Document doc = createDocument();
        doc.setMargins(0, 0, 0, 0);

        Paragraph p = new Paragraph("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n");
        p.setBorder(new SolidBorder(50));
        p.setRotationAngle(Math.PI / 6);
        doc.add(p);
        Image img = new Image(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
        img.setBorder(new SolidBorder(50));
        img.setRotationAngle(Math.PI / 6);
        doc.add(img);
        doc.close();

        closeDocumentAndCompareOutputs(doc);
    }

    private Document createDocument() throws FileNotFoundException {
        outFileName = destinationFolder + fileName;
        cmpFileName = sourceFolder + cmpPrefix + fileName;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        return new Document(pdfDocument);
    }

    private void closeDocumentAndCompareOutputs(Document document) throws IOException, InterruptedException {
        document.close();
        String compareResult = new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff");
        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }


    //When 7.2 release is in progress, remove the underlying code. It's here to pass A SQ line coverage quality gate and tests deprecated protected methods
    public class TestDashedBorder extends DashedBorder {
        public TestDashedBorder(float width) {
            super(width);
        }
        public float publicGetDotsGap(double distance,float initialGap){
            return getDotsGap(distance, initialGap);
        }
    }

    public class TestDottedBorder extends DottedBorder {
        public TestDottedBorder(float width) {
            super(width);
        }
        public float publicGetDotsGap(double distance,float initialGap){
            return getDotsGap(distance, initialGap);
        }
    }

    public class TestRoundDotsBorder extends RoundDotsBorder {
        public TestRoundDotsBorder(float width) {
            super(width);
        }
        public float publicGetDotsGap(double distance,float initialGap){
            return getDotsGap(distance, initialGap);
        }
    }

    @Test
    public void getDotsGapTest(){
        float expected = 0.2f;
        double distance = 0.2;
        float initialGap = 0.2f;

        TestDashedBorder db = new TestDashedBorder(1f);
        TestDottedBorder dotb = new TestDottedBorder(1f);
        TestRoundDotsBorder rdb = new TestRoundDotsBorder(1f);

        float dbActual = db.publicGetDotsGap(distance,initialGap);
        float dotbActual = dotb.publicGetDotsGap(distance,initialGap);
        float rdbActual = rdb.publicGetDotsGap(distance,initialGap);

        Assert.assertEquals(expected,dbActual,0.0001f);
        Assert.assertEquals(expected,dotbActual,0.0001f);
        Assert.assertEquals(expected,rdbActual,0.0001f);
    }
}
