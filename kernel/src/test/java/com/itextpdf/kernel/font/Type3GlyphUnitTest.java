package com.itextpdf.kernel.font;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class Type3GlyphUnitTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/font/Type3GlyphUnitTest/";

    @Test
    public void addImageWithoutMaskTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        Exception e = Assert.assertThrows(PdfException.class,
                () -> glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, false));
        Assert.assertEquals("Not colorized type3 fonts accept only mask images.", e.getMessage());
    }

    @Test
    public void addInlineImageMaskTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        img.makeMask();
        Assert.assertNull(glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, true));
    }

    @Test
    //TODO DEVSIX-5764 Display message error for non-inline images in type 3 glyph
    public void addImageMaskAsNotInlineTest() throws MalformedURLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Type3Glyph glyph = new Type3Glyph(new PdfStream(), pdfDoc);
        ImageData img = ImageDataFactory.create(SOURCE_FOLDER + "imageTest.png");
        img.makeMask();
        Assert.assertThrows(NullPointerException.class,
                () -> glyph.addImageWithTransformationMatrix(img, 100, 0, 0, 100, 0, 0, false));
    }
}
