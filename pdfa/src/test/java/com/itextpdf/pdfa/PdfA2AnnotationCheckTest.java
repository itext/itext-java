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
package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA2AnnotationCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2AnnotationCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2AnnotationCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void annotationCheckTest01() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfFileAttachmentAnnotation(rect);
        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
    public void annotationCheckTest04() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlag(PdfAnnotation.PRINT);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest05() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlag(0);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest06() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfTextAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT | PdfAnnotation.INVISIBLE);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest07() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
        doc.close();
    }

    @Test
    public void annotationCheckTest08() throws IOException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
        doc.close();
    }

    @Test
    public void annotationCheckTest09() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfWidgetAnnotation(rect);
        annot.setContents(new PdfString(""));
        annot.setFlags(PdfAnnotation.PRINT);

        annot.setNormalAppearance(new PdfDictionary());

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest10() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_annotationCheckTest10.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_annotationCheckTest10.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
    public void annotationCheckTest12() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(PdfAConformanceException.ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_KEY, PdfName.Stamp.getValue()));

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT);

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest13() throws FileNotFoundException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setTagged();
        doc.getCatalog().setLang(new PdfString("en-US"));
        PdfPage page = doc.addNewPage();

        Rectangle rect = new Rectangle(100, 650, 400, 100);
        PdfAnnotation annot = new PdfStampAnnotation(rect);
        annot.setFlags(PdfAnnotation.PRINT);
        annot.setContents("Hello world");

        page.addAnnotation(annot);
        doc.close();
    }

    @Test
    public void annotationCheckTest14() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2a_annotationCheckTest14.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2a_annotationCheckTest14.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
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
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
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
