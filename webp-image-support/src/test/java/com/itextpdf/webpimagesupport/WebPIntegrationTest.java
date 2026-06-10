package com.itextpdf.webpimagesupport;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Tag("IntegrationTest")
@DisabledInNativeImage
public class WebPIntegrationTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/webpimagesupport/WebpIntegrationTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf//webpimagesupport/image/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> getWebPImages() {
        return Arrays.asList(new Object[][]{
                {"5_webp_ll"},
                {"lossless"},
                {"lossyWebPImage"},
                {"opaqueWebPImage"},
                {"animatedWebPImage"},
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getWebPImages")
    public void webpSimpleImageTest(String imageName) throws IOException, InterruptedException {
        String imageFileName = SOURCE_FOLDER + imageName + ".webp";
        String outFileName = DESTINATION_FOLDER + imageName + "Pdf.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + imageName + "Pdf.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()));

        try (InputStream fis = FileUtil.getInputStreamForFile(imageFileName)) {
            byte[] imageBytes = StreamUtil.inputStreamToArray(fis);
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            ImageData img = ImageDataFactory.create(imageBytes);
            Assertions.assertEquals(ImageType.WEBP, img.getOriginalType());
            canvas.addImageAt(img, 50, 50, false);
            canvas.release();
        }

        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void webpPSimpleImageUrlTest() throws IOException, InterruptedException {
        String imageFileName = SOURCE_FOLDER + "lossless.webp";
        String outFileName = DESTINATION_FOLDER + "losslessUrlPdf.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_losslessPdf.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName, new WriterProperties()));

        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        ImageData img = ImageDataFactory.create(UrlUtil.toURL(imageFileName));
        Assertions.assertEquals(ImageType.WEBP, img.getOriginalType());
        canvas.addImageAt(img, 50, 50, false);
        canvas.release();

        pdfDocument.close();
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }
}
