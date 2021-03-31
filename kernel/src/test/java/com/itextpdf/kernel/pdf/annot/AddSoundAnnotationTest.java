package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AddSoundAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder =
            "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddSoundAnnotationTest/";
    public static final String destinationFolder =
            "./target/test/com/itextpdf/kernel/pdf/annot/AddSoundAnnotationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }
    @Test
    public void soundTestAif() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation02.pdf";
        String audioFile = sourceFolder + "sample.aif";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String string = "";
        for (int i = 0; i < 4; i++) {
            string = string + (char) is.read();
        }
        if (string.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream sound1 = new PdfStream(pdfDoc, is);
        sound1.put(PdfName.R, new PdfNumber(32117));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestAiff() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation03.pdf";
        String audioFile = sourceFolder + "sample.aiff";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String string = "";
        for (int i = 0; i < 4; i++) {
            string = string + (char) is.read();
        }
        if (string.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream sound1 = new PdfStream(pdfDoc, is);
        sound1.put(PdfName.R, new PdfNumber(44100));
        sound1.put(PdfName.E, PdfName.Signed);
        sound1.put(PdfName.B, new PdfNumber(16));
        sound1.put(PdfName.C, new PdfNumber(1));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), sound1);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestSnd() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation04.pdf";
        String audioFile = sourceFolder + "sample.snd";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);

        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc, new Rectangle(100, 100, 100, 100), is, 44100, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation04.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation01.pdf";
        String audioFile = sourceFolder + "sample.wav";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        PdfSoundAnnotation sound = new PdfSoundAnnotation(pdfDoc, new Rectangle(100, 100, 100, 100), is, 48000, PdfName.Signed, 2, 16);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void soundTestWav01() throws IOException, InterruptedException {
        String filename = destinationFolder + "soundAnnotation05.pdf";
        String audioFile = sourceFolder + "sample.wav";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        InputStream is = new FileInputStream(audioFile);
        String header = "";
        for (int i = 0; i < 4; i++) {
            header = header + (char) is.read();
        }
        if (header.equals("RIFF")) {
            is = new FileInputStream(audioFile);
            is.read();
        } else {
            is = new FileInputStream(audioFile);
        }

        PdfStream soundStream = new PdfStream(pdfDoc, is);

        soundStream.put(PdfName.R, new PdfNumber(48000));
        soundStream.put(PdfName.E, PdfName.Signed);
        soundStream.put(PdfName.B, new PdfNumber(16));
        soundStream.put(PdfName.C, new PdfNumber(2));

        PdfSoundAnnotation sound = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), soundStream);

        page1.addAnnotation(sound);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_soundAnnotation05.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
