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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfPopupAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfA2AnnotationCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2AnnotationCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2AnnotationCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void annotationCheckTest01() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(rect);
        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY, e.getMessage());
    }

    @Test
    public void annotationCheckTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfPopupAnnotation(rect);

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest03() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest03.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 100, 0, 0);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.PRINT);

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest04() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.PRINT);

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY, e.getMessage());
    }

    @Test
    public void annotationCheckTest05() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(0);

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest06() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT | PdfAnnotation.INVISIBLE);

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest07() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT);

        annot.setDownAppearance(new PdfDictionary());
        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest08() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT);
        annot.getPdfObject().put(PdfName.FT, PdfName.Btn);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest09() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest10() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest10.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest10.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void annotationCheckTest11() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest11.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest11.pdf";
        PdfWriter writer = new PdfWriter(outPdf);

        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT |PdfAnnotation.NO_ZOOM | PdfAnnotation.NO_ROTATE);

        annot.setNormalAppearance(createAppearance(doc, formRect));

        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_KEY, logLevel = LogLevelConstants.WARN)
    })
    public void annotationCheckTest12() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1a_annotationCheckTest12.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA1a_annotationCheckTest12.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT);
        annot.setNormalAppearance(createAppearance(doc, new Rectangle(400, 100)));

        page.addAnnotation(annot);

        doc.close();
        compareResult(outPdf, cmpPdf);
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void annotationCheckTest13() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT);
        annot.setContents("Hello world");

        page.addAnnotation(annot);

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assertions.assertEquals(PdfaExceptionMessageConstant.EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY,
                e.getMessage());
    }

    @Test
    public void annotationCheckTest14() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2a_annotationCheckTest14.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2a_annotationCheckTest14.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));

        PdfPage page = doc.addNewPage();

        Rectangle annotRect = new Rectangle(100, 650, 400, 100);
        Rectangle formRect = new Rectangle(400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(annotRect);
        annot.setFlags(PdfAnnotation.PRINT);
        annot.setContents("Hello World");

        annot.setNormalAppearance(createAppearance(doc, formRect));
        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private PdfStream createAppearance(PdfADocument doc, Rectangle formRect) throws IOException {
        PdfFormXObject form = new PdfFormXObject(formRect);

        PdfCanvas canvas = new PdfCanvas(form, doc);
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        canvas.setFontAndSize(font, 12);
        canvas.beginText().setTextMatrix(200, 50).showText("Hello World").endText();
        return form.getPdfObject();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
