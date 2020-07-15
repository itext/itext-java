package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.BorderRadius;
import com.itextpdf.layout.property.ObjectFit;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ObjectFitTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ObjectFitTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ObjectFitTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_fill.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_fill.pdf";

        generateDocumentWithObjectFit(ObjectFit.FILL, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void coverObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_cover.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_cover.pdf";

        generateDocumentWithObjectFit(ObjectFit.COVER, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void containObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_contain.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_contain.pdf";

        generateDocumentWithObjectFit(ObjectFit.CONTAIN, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void scaleDownObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_scale_down.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_scale_down.pdf";

        generateDocumentWithObjectFit(ObjectFit.SCALE_DOWN, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void noneObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_none.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_none.pdf";

        generateDocumentWithObjectFit(ObjectFit.NONE, outFileName);

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void scaleDownSmallImageObjectFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_scale_down_small_image.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_scale_down_small_image.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {
            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "itis.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.SCALE_DOWN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void twoCoverObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_two_objects.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_two_objects.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.COVER);

            Image image2 = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            p.add(image2);
            doc.add(p);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void containWithEffectsObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_with_effects.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_with_effects.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setBorder(new SolidBorder(new DeviceGray(0), 5))
                    .setBorderRadius(new BorderRadius(100))
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = com.itextpdf.io.LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA),
    })
    // TODO DEVSIX-4286 object-fit property combined with rotation is not processed correctly
    public void containWithRotationObjectsFitTest() throws IOException, InterruptedException {

        String outFileName = destinationFolder + "objectFit_test_with_rotation.pdf";
        String cmpFileName = sourceFolder + "cmp_objectFit_test_with_rotation.pdf";

        try(
                PdfWriter writer = new PdfWriter(outFileName);
                Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setRotationAngle(45)
                    .setBorder(new SolidBorder(new DeviceGray(0), 1))
                    .setObjectFit(ObjectFit.CONTAIN);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void generateDocumentWithObjectFit(ObjectFit objectFit, String outFileName) throws IOException {
        try(
            PdfWriter writer = new PdfWriter(outFileName);
            Document doc = new Document(new PdfDocument(writer))
        ) {

            PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(sourceFolder + "Desert.jpg"));
            Image image = new Image(xObject)
                    .setWidth(200)
                    .setHeight(600)
                    .setObjectFit(objectFit);

            Paragraph p = new Paragraph();
            p.add(image);
            doc.add(p);
        }
    }
}
