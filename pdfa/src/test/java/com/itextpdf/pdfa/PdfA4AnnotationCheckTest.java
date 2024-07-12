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
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.Pdf3DAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfScreenAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSoundAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfA4AnnotationCheckTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA4AnnotationCheckTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4AnnotationCheckTest/";

    @BeforeClass
    public static void beforeClass() throws FileNotFoundException {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfA4ForbiddenAnnotations1Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new PdfFileAttachmentAnnotation(new Rectangle(100, 100, 100, 100));
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.FileAttachment.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenAnnotations2Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.Sound.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenAnnotations3Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new Pdf3DAnnotation(new Rectangle(100, 100, 100, 100), new PdfArray());
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName._3D.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4AllowedAnnotations1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AllowedAnnotations1Test.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AllowedAnnotations1Test.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            PdfAnnotation annot = new PdfLinkAnnotation(new Rectangle(100, 100, 100, 100));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setContents("Hello world");
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4eForbiddenAnnotations1Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new PdfFileAttachmentAnnotation(new Rectangle(100, 100, 100, 100));
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.FileAttachment.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4eForbiddenAnnotations2Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.Sound.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4eAllowedAnnotations1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4eAllowedAnnotations1Test.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4eAllowedAnnotations1Test.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            PdfAnnotation annot = new PdfLinkAnnotation(new Rectangle(100, 100, 100, 100));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setContents("Hello world");
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4eAllowedAnnotations2Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4eAllowedAnnotations2Test.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4eAllowedAnnotations2Test.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4E, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            PdfStream stream3D = new PdfStream(doc, FileUtil.getInputStreamForFile(CMP_FOLDER + "teapot.u3d"));
            stream3D.put(PdfName.Type, PdfName._3D);
            stream3D.put(PdfName.Subtype, new PdfName("U3D"));
            stream3D.setCompressionLevel(CompressionConstants.UNDEFINED_COMPRESSION);

            Pdf3DAnnotation annot = new Pdf3DAnnotation(new Rectangle(100, 100, 100, 100), stream3D);

            PdfDictionary dict3D = new PdfDictionary();
            dict3D.put(PdfName.Type, PdfName._3DView);
            dict3D.put(new PdfName("XN"), new PdfString("Default"));
            dict3D.put(new PdfName("IN"), new PdfString("Unnamed"));
            dict3D.put(new PdfName("MS"), PdfName.M);
            dict3D.put(new PdfName("C2W"),
                    new PdfArray(new float[]{1, 0, 0, 0, 0, -1, 0, 1, 0, 3, -235, 28}));
            dict3D.put(PdfName.CO, new PdfNumber(235));

            annot.setDefaultInitialView(dict3D);
            annot.setContents(new PdfString("3D Model"));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setAppearance(PdfName.N, new PdfStream());
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4fForbiddenAnnotations1Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfSoundAnnotation(new Rectangle(100, 100, 100, 100), new PdfStream());
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.Sound.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4fForbiddenAnnotations2Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new Pdf3DAnnotation(new Rectangle(100, 100, 100, 100), new PdfArray());
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName._3D.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4fForbiddenAnnotations3Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfScreenAnnotation(new Rectangle(100, 100, 100, 100));
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.Screen.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4fForbiddenAnnotations4Test() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfTextAnnotation(new Rectangle(100, 100, 100, 100));
        annot.getPdfObject().put(PdfName.Subtype, PdfName.RichMedia);
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ANNOTATION_TYPE_0_IS_NOT_PERMITTED,
                PdfName.RichMedia.getValue()), e.getMessage());
    }

    @Test
    public void pdfA4fAllowedAnnotations1Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fAllowedAnnotations1Test.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4fAllowedAnnotations1Test.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            addSimpleEmbeddedFile(doc);

            PdfAnnotation annot = new PdfFileAttachmentAnnotation(new Rectangle(100, 100, 100, 100));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setContents("Hello world");
            annot.setAppearance(PdfName.N, new PdfStream());
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4fAllowedAnnotations2Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4fAllowedAnnotations2Test.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4fAllowedAnnotations2Test.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            addSimpleEmbeddedFile(doc);

            PdfAnnotation annot = new PdfLinkAnnotation(new Rectangle(100, 100, 100, 100));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setContents("Hello world");
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4AllowedAnnotWithoutApTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AllowedAnnotWithoutApTest.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AllowedAnnotWithoutApTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            PdfAnnotation annot = new PdfProjectionAnnotation(new Rectangle(100, 100, 100, 100));
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setContents("Hello world");
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4ForbiddenAKeyWidgetAnnotationTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfWidgetAnnotation(new Rectangle(100, 100, 100, 100));
        annot.getPdfObject().put(PdfName.A, (new PdfAction()).getPdfObject());
        annot.setFlag(PdfAnnotation.PRINT);
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_ENTRY,
                e.getMessage());
    }

    @Test
    public void pdfA4AllowedAAKeyWidgetAnnotationTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AllowedAAKeyWidgetAnnotationTest.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AllowedAAKeyWidgetAnnotationTest.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        try (PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent())) {
            PdfPage page = doc.addNewPage();

            PdfAnnotation annot = new PdfWidgetAnnotation(new Rectangle(100, 100, 100, 100));
            annot.getPdfObject().put(PdfName.AA, (new PdfAction()).getPdfObject());
            annot.setFlag(PdfAnnotation.PRINT);
            annot.setAppearance(PdfName.N, new PdfStream());
            page.addAnnotation(annot);
        }
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4BtnAppearanceContainsNStreamWidgetAnnotationTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfWidgetAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setAppearance(PdfName.N, new PdfStream());
        annot.getPdfObject().put(PdfName.FT, PdfName.Btn);
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE,
                e.getMessage());
    }

    @Test
    public void pdfA4AppearanceContainsNDictWidgetAnnotationTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfWidgetAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setAppearance(PdfName.N, new PdfDictionary());
        annot.getPdfObject().put(PdfName.FT, PdfName.Tx);
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE,
                e.getMessage());
    }

    @Test
    public void pdfA4AppearanceContainsOtherKeyWidgetAnnotationTest() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4F, createOutputIntent());
        PdfPage page = doc.addNewPage();

        addSimpleEmbeddedFile(doc);

        PdfAnnotation annot = new PdfWidgetAnnotation(new Rectangle(100, 100, 100, 100));
        annot.setFlag(PdfAnnotation.PRINT);
        annot.setAppearance(PdfName.A, new PdfStream());
        annot.getPdfObject().put(PdfName.FT, PdfName.Btn);
        page.addAnnotation(annot);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(PdfaExceptionMessageConstant.APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenAAKeyAnnotationTest() throws IOException, InterruptedException {

        String outPdf = DESTINATION_FOLDER + "pdfA4ForbiddenAAKeyAnnotationTest.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4ForbiddenAAKeyAnnotationTest.pdf";
        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, createOutputIntent());
        PdfPage page = doc.addNewPage();

        PdfAnnotation annot = new PdfLinkAnnotation(new Rectangle(100, 100, 100, 100));
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Fo, new PdfName("bingbong"));
        annot.getPdfObject().put(PdfName.AA, dict);
        annot.setFlag(PdfAnnotation.PRINT);
        page.addAnnotation(annot);
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assert.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            Assert.fail(result);
        }
    }

    private void addSimpleEmbeddedFile(PdfDocument doc) {
        PdfFileSpec fs = PdfFileSpec.createEmbeddedFileSpec(
                doc, "file".getBytes(), "description", "file.txt", null, null, null);
        doc.addFileAttachment("file.txt", fs);
    }

    private PdfOutputIntent createOutputIntent() throws IOException {
        return new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm"));
    }

    private static final class PdfProjectionAnnotation extends PdfAnnotation {
        public PdfProjectionAnnotation(Rectangle rect) {
            super(rect);
        }

        @Override
        public PdfName getSubtype() {
            return PdfName.Projection;
        }
    }
}
