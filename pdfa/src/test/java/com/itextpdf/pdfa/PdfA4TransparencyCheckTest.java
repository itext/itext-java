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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.IccBased;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA4TransparencyCheckTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String CMP_FOLDER = "./src/test/resources/com/itextpdf/pdfa/cmp/PdfA4TransparencyCheckTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4TransparencyCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void textTransparencyPageOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "textTransparencyPageOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_textTransparencyPageOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfPage page1 = pdfDocument.addNewPage();
        page1.addOutputIntent(createOutputIntent());

        InputStream streamGray = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "BlackWhite.icc");
        IccBased gray = new IccBased(streamGray, new float[]{0.2f});

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);
        canvas.beginText()
                .setColor(gray, true) // required here till TODO: DEVSIX-7775 - Check Output intents and colorspaces is implemented
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page with transparency")
                .endText()
                .restoreState();

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void textTransparencyPageWrongOutputIntentTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "textTransparencyPageWrongOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfOutputIntent outputIntent = createOutputIntent();
        outputIntent.setOutputIntentSubtype(new PdfName("GTS_PDFX"));

        PdfPage page1 = pdfDoc.addNewPage();
        page1.addOutputIntent(outputIntent);

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);
        canvas.beginText()
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page with transparency")
                .endText()
                .restoreState();

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> pdfDoc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_DOCUMENT_AND_THE_PAGE_DO_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE),
                e.getMessage());
    }

    @Test
    public void transparentTextWithGroupColorSpaceTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "transparencyAndCS.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_transparencyAndCS.pdf";

        PdfDocument pdfDocument = new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4, null);
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        InputStream streamGray = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "BlackWhite.icc");
        IccBased gray = new IccBased(streamGray, new float[]{0.2f});

        PdfPage page = pdfDocument.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState();
        state.setFillOpacity(0.6f);
        canvas.setExtGState(state);
        canvas.beginText()
                .setColor(gray, true)
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page with transparency")
                .endText()
                .restoreState();

        PdfDictionary groupObj = new PdfDictionary();
        groupObj.put(PdfName.CS, new PdfCieBasedCs.CalGray(getCalGrayArray()).getPdfObject());
        groupObj.put(PdfName.Type, PdfName.Group);
        groupObj.put(PdfName.S, PdfName.Transparency);
        page.getPdfObject().put(PdfName.Group, groupObj);

        PdfPage page2 = pdfDocument.addNewPage();
        canvas = new PdfCanvas(page2);
        canvas.saveState();
        canvas.beginText()
                .setColor(gray, true)
                .moveText(36, 750)
                .setFontAndSize(font, 16)
                .showText("Page 2 without transparency")
                .endText()
                .restoreState();

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void blendModeTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent())) {
            PdfCanvas canvas = new PdfCanvas(doc.addNewPage());

            canvas.saveState();
            canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Darken));
            canvas.rectangle(100, 100, 100, 100);
            canvas.fill();
            canvas.restoreState();

            canvas.saveState();

            // Verapdf doesn't assert on PdfName.Compatible apparently but let's be strict here
            Exception e = Assert.assertThrows(PdfAConformanceException.class,
                    () -> canvas.setExtGState(new PdfExtGState().setBlendMode(PdfName.Compatible))
            );
            Assert.assertEquals(
                    PdfAConformanceException.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY,
                    e.getMessage());
        }

    }

    @Test
    public void blendModeAnnotationTest() {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null);

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(100f, 100f));
        Canvas canvas = new Canvas(formXObject, doc);
        canvas.getPdfCanvas().circle(50f, 50f, 40f);

        PdfAnnotation annotation = new PdfCircleAnnotation(new Rectangle(100f, 100f));
        annotation.setNormalAppearance(formXObject.getPdfObject());
        annotation.setContents("Circle");
        annotation.setBlendMode(PdfName.Saturation);
        annotation.setFlags(4);

        PdfPage page = doc.addNewPage();
        page.addAnnotation(annotation);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.THE_DOCUMENT_AND_THE_PAGE_DO_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE,
                e.getMessage());
    }

    @Test
    public void blendModeAnnotationOutputIntentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "blendModeAnnotationOutputIntent.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_blendModeAnnotationOutputIntent.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, null)) {
            PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(100f, 100f));
            Canvas canvas = new Canvas(formXObject, doc);
            canvas.getPdfCanvas().circle(50f, 50f, 40f);

            PdfAnnotation annotation = new PdfCircleAnnotation(new Rectangle(100f, 100f));
            annotation.setNormalAppearance(formXObject.getPdfObject());
            annotation.setContents("Circle");
            annotation.setBlendMode(PdfName.Saturation);
            annotation.setFlags(4);

            PdfPage page = doc.addNewPage();
            page.addAnnotation(annotation);
            page.addOutputIntent(createOutputIntent());
        }

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void forbiddenBlendModeAnnotationTest() throws IOException {
        PdfWriter writer = new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());

        PdfFormXObject formXObject = new PdfFormXObject(new Rectangle(0f, 0f));
        PdfAnnotation annotation = new PdfPopupAnnotation(new Rectangle(0f, 0f));
        annotation.setNormalAppearance(formXObject.getPdfObject());
        annotation.setBlendMode(new PdfName("dummy blend mode"));

        PdfPage page = doc.addNewPage();
        page.addAnnotation(annotation);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_A_GRAPHIC_STATE_AND_ANNOTATION_DICTIONARY,
                e.getMessage());
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    private PdfArray getCalGrayArray() {
        PdfDictionary dictionary = new PdfDictionary();

        dictionary.put(PdfName.Gamma, new PdfNumber(2.2));

        PdfArray whitePointArray = new PdfArray();
        whitePointArray.add(new PdfNumber(0.9505));
        whitePointArray.add(new PdfNumber(1.0));
        whitePointArray.add(new PdfNumber(1.089));
        dictionary.put(PdfName.WhitePoint, whitePointArray);

        PdfArray array = new PdfArray();
        array.add(PdfName.CalGray);
        array.add(dictionary);

        return array;
    }

    private PdfOutputIntent createOutputIntent() throws IOException {
        return new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));
    }
}
