package com.itextpdf.kernel.utils;

import com.itextpdf.commons.utils.PlaceHolderTextUtil;
import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.annotationsflattening.DefaultAnnotationFlattener;
import com.itextpdf.kernel.utils.annotationsflattening.IAnnotationFlattener;
import com.itextpdf.kernel.utils.annotationsflattening.PdfAnnotationFlattenFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAnnotationFlattenerTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/utils/flatteningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/utils/flatteningTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void testNullAnnotations() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            PdfPage page = pdfDoc.getFirstPage();
            Assert.assertThrows(PdfException.class, () -> {
                flattener.flatten(null, page);
            });
        }
    }

    @Test
    public void testNullPage() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            List<PdfAnnotation> annotations = new ArrayList<>();
            Assert.assertThrows(PdfException.class, () -> {
                flattener.flatten(annotations, null);
            });
        }
    }

    @Test
    public void testNullPageFlatten() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            Assert.assertThrows(PdfException.class, () -> {
                flattener.flatten(null);
            });
        }
    }

    @Test
    public void testNullPageDrawAppearanceWorker() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            IAnnotationFlattener flattener = new DefaultAnnotationFlattener();
            PdfPage page = pdfDoc.getFirstPage();
            Assert.assertThrows(PdfException.class, () -> {
                flattener.flatten(null, page);
            });
        }
    }

    @Test
    public void testNullAnnotationDrawAppearanceWorker() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            IAnnotationFlattener flattener = new DefaultAnnotationFlattener();
            Assert.assertThrows(PdfException.class, () -> {
                flattener.flatten(new PdfLinkAnnotation(new Rectangle(20, 20)), null);
            });
        }
    }

    @Test
    public void testEmptyAnnotations() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            flattener.flatten(new ArrayList<>(), pdfDoc.getFirstPage());
            Assert.assertEquals(0, pdfDoc.getFirstPage().getAnnotsSize());
        }
    }

    @Test
    public void defaultAppearanceGetsRendered() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "default_annotations_app.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(resultFile))) {
            PdfFormXObject formN = new PdfFormXObject(new Rectangle(179, 530, 122, 21));
            PdfCanvas canvasN = new PdfCanvas(formN, pdfDoc);
            PdfAnnotation annotation = new PdfLinkAnnotation(new Rectangle(100, 540, 300, 50)).
                    setAction(PdfAction.createURI("http://itextpdf.com/node"));
            canvasN
                    .saveState()
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(1.5f)
                    .rectangle(180, 531, 120, 48)
                    .fill()
                    .restoreState();
            canvasN.saveState()
                    .beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 12)
                    .setColor(ColorConstants.YELLOW, true)
                    .moveText(180, 531)
                    .showText("Hello appearance")
                    .endText()
                    .restoreState();
            ;
            annotation.setNormalAppearance(formN.getPdfObject());
            pdfDoc.addNewPage();
            pdfDoc.getFirstPage().addAnnotation(annotation);
            DefaultAnnotationFlattener worker =
                    new DefaultAnnotationFlattener();
            worker.flatten(annotation, pdfDoc.getFirstPage());
        }

        Assert.assertNull(
                new CompareTool().compareByContent(resultFile,
                        sourceFolder + "cmp_default_annotations_app.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED)})
    public void unknownAnnotationsDefaultImplementation() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDictionary unknownAnnot = new PdfDictionary();
            unknownAnnot.put(PdfName.Subtype, new PdfName("Unknown"));
            unknownAnnot.put(PdfName.Rect, new PdfArray(new int[] {100, 100, 200, 200}));
            PdfAnnotation unknownAnnotation = PdfAnnotation.makeAnnotation(unknownAnnot);
            pdfDoc.addNewPage();
            pdfDoc.getFirstPage().addAnnotation(unknownAnnotation);
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            flattener.flatten(Collections.singletonList(unknownAnnotation), pdfDoc.getFirstPage());
            //Annotation is not removed in default implementation
            Assert.assertEquals(1, pdfDoc.getFirstPage().getAnnotsSize());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED)})
    public void nullTypeAnnotationsDefaultImplementation() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfDictionary unknownAnnot = new PdfDictionary();
            unknownAnnot.put(PdfName.Rect, new PdfArray(new int[] {100, 100, 200, 200}));
            PdfAnnotation unknownAnnotation = PdfAnnotation.makeAnnotation(unknownAnnot);
            pdfDoc.addNewPage();
            pdfDoc.getFirstPage().addAnnotation(unknownAnnotation);
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            flattener.flatten(Collections.singletonList(unknownAnnotation), pdfDoc.getFirstPage());
            //Annotation is not removed in default implementation
            Assert.assertEquals(1, pdfDoc.getFirstPage().getAnnotsSize());
        }
    }

    @Test
    public void overwriteDefaultImplementation() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            int[] borders = {0, 0, 1};
            pdfDoc.addNewPage();
            pdfDoc.getFirstPage().addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                    setAction(PdfAction.createURI("http://itextpdf.com/node")).
                    setBorder(new PdfArray(borders)));
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener(new CustomPdfAnnotationFlattenFactory());
            flattener.flatten(pdfDoc.getFirstPage().getAnnotations(), pdfDoc.getFirstPage());
            Assert.assertEquals(0, pdfDoc.getFirstPage().getAnnotsSize());
        }
    }

    @Test
    public void removeQuadPoints() throws IOException, InterruptedException {
        String fileToFlatten = destinationFolder + "file_to_quadpoints.pdf";
        String resultFile = destinationFolder + "flattened_quadpoints.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileToFlatten))) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            float x = 50;
            float y = 750;
            float textLength = 350;
            float[] points = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};

            PdfAnnotation annot = createTextAnnotation(canvas, x, y, points, PdfName.StrikeOut, ColorConstants.RED);
            annot.getPdfObject().remove(PdfName.QuadPoints);
            page.addAnnotation(annot);

        }
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(fileToFlatten), new PdfWriter(resultFile))) {
            new PdfAnnotationFlattener()
                    .flatten(pdfDoc.getFirstPage().getAnnotations(), pdfDoc.getFirstPage());
        }
        //it is expected that the line is the middle of the page because the annotation whole rectangle is the
        // size of the page, it's also expected that underline will not show up as it is at the bottom of the page
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_text_quadpoints.pdf",
                        destinationFolder, "diff_"));
    }


    @Test
    public void invalidQuadPoints() throws IOException, InterruptedException {
        String fileToFlatten = destinationFolder + "file_to_invalid_quadpoints.pdf";
        String resultFile = destinationFolder + "flattened_invalid_quadpoints.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileToFlatten))) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            float x = 50;
            float y = 750;
            float textLength = 350;
            float[] points = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};

            PdfAnnotation annot = createTextAnnotation(canvas, x, y, points, PdfName.StrikeOut, ColorConstants.RED);
            annot.getPdfObject().put(PdfName.QuadPoints, new PdfArray(new float[] {0, 0, 0, 0, 0, 0}));
            page.addAnnotation(annot);

        }
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(fileToFlatten), new PdfWriter(resultFile))) {
            new PdfAnnotationFlattener()
                    .flatten(pdfDoc.getFirstPage().getAnnotations(), pdfDoc.getFirstPage());
        }
        //it is expected that the line is the middle of the page because the annotation whole rectangle is the
        // size of the page, it's also expected that underline will not show up as it is at the bottom of the page
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_invalid_quadpoints.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    public void testEmptyParamListDoesntDeleteAnyAnnots() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            int[] borders = {0, 0, 1};
            pdfDoc.getFirstPage().addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                    setAction(PdfAction.createURI("http://itextpdf.com/node")).
                    setBorder(new PdfArray(borders)));
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            flattener.flatten(new ArrayList<>(), pdfDoc.getFirstPage());
            Assert.assertEquals(1, pdfDoc.getFirstPage().getAnnotsSize());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED)})
    public void testListFromDifferentPageDoesntDeleteAnyAnnotsButWarnsUser() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.addNewPage();
            pdfDoc.addNewPage();
            int[] borders = {0, 0, 1};
            pdfDoc.getPage(1).addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                    setAction(PdfAction.createURI("http://itextpdf.com/node")).
                    setBorder(new PdfArray(borders)));
            pdfDoc.getPage(2).addAnnotation(new PdfLinkAnnotation(new Rectangle(100, 540, 300, 25)).
                    setAction(PdfAction.createURI("http://itextpdf.com/node")).
                    setBorder(new PdfArray(borders)));
            PdfAnnotationFlattener flattener = new PdfAnnotationFlattener();
            flattener.flatten(pdfDoc.getPage(2).getAnnotations(), pdfDoc.getFirstPage());
            Assert.assertEquals(1, pdfDoc.getFirstPage().getAnnotsSize());
            Assert.assertEquals(1, pdfDoc.getPage(2).getAnnotsSize());
        }
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.FLATTENING_IS_NOT_YET_SUPPORTED)})
    public void flattenPdfLink() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "flattened_pdf_link.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "simple_link_annotation.pdf"),
                new PdfWriter(resultFile))) {
            new PdfAnnotationFlattener().flatten(pdfDoc.getFirstPage());
        }
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_flattened_pdf_link.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    public void flattenPdfLinkWithDefaultAppearance() throws IOException, InterruptedException {
        String resultFile = destinationFolder + "flattened_DA_pdf_link.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "simple_link_annotation.pdf"),
                new PdfWriter(resultFile))) {
            PdfAnnotation annot = pdfDoc.getFirstPage().getAnnotations().get(0);
            annot.setNormalAppearance(new PdfDictionary());
            PdfFormXObject formN = new PdfFormXObject(new Rectangle(179, 530, 122, 21));
            PdfCanvas canvasN = new PdfCanvas(formN, pdfDoc);
            canvasN
                    .saveState()
                    .setColor(ColorConstants.RED, true)
                    .setLineWidth(1.5f)
                    .rectangle(180, 531, 120, 48)
                    .fill()
                    .restoreState();
            annot.setNormalAppearance(formN.getPdfObject());
            new PdfAnnotationFlattener().flatten(pdfDoc.getFirstPage());
        }
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_flattened_DA_pdf_link.pdf",
                        destinationFolder, "diff_"));
    }

    @Test
    public void flattenTextMarkupAnnotations()
            throws IOException, InterruptedException {
        String fileToFlatten = destinationFolder + "file_to_flatten_markup_text.pdf";
        String resultFile = destinationFolder + "flattened_markup_text.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fileToFlatten))) {
            PdfPage page = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);
            float x = 50;
            float y = 750;
            float textLength = 350;
            float[] points = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};
            page.addAnnotation(createTextAnnotation(canvas, x, y, points, PdfName.Underline, ColorConstants.RED));
            y -= 50;
            float[] points2 = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};
            page.addAnnotation(createTextAnnotation(canvas, x, y, points2, PdfName.StrikeOut, ColorConstants.BLUE));
            y -= 50;
            float[] points3 = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};
            page.addAnnotation(createTextAnnotation(canvas, x, y, points3, PdfName.Squiggly, ColorConstants.RED));
            y -= 50;
            float[] points4 = {x, y + 15, x + textLength, y + 15, x, y - 4, x + textLength, y - 4};
            page.addAnnotation(createTextAnnotation(canvas, x, y, points4, PdfName.Highlight, ColorConstants.YELLOW));

        }
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(fileToFlatten), new PdfWriter(resultFile))) {
            new PdfAnnotationFlattener()
                    .flatten(pdfDoc.getFirstPage().getAnnotations(), pdfDoc.getFirstPage());
        }
        Assert.assertNull(
                new CompareTool().compareByContent(resultFile, sourceFolder + "cmp_text_markup_flatten.pdf",
                        destinationFolder, "diff_"));
    }

    private PdfTextMarkupAnnotation createTextAnnotation(PdfCanvas canvas, float x, float y, float[] quadPoints,
            PdfName type,
            Color color)
            throws IOException {
        canvas
                .saveState()
                .beginText()
                .moveText(x, y)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5))
                .endText()
                .restoreState();

        PdfTextMarkupAnnotation markup = null;
        if (PdfName.Underline.equals(type)) {
            markup = PdfTextMarkupAnnotation.createUnderline(PageSize.A4, quadPoints);
        }
        if (PdfName.StrikeOut.equals(type)) {
            markup = PdfTextMarkupAnnotation.createStrikeout(PageSize.A4, quadPoints);
        }
        if (PdfName.Highlight.equals(type)) {
            markup = PdfTextMarkupAnnotation.createHighLight(PageSize.A4, quadPoints);
        }
        if (PdfName.Squiggly.equals(type)) {
            markup = PdfTextMarkupAnnotation.createSquiggly(PageSize.A4, quadPoints);
        }
        if (markup == null) {
            throw new IllegalArgumentException();
        }

        markup.setContents(new PdfString("TextMarkup"));
        markup.setColor(color.getColorValue());
        return markup;
    }

    static class CustomPdfAnnotationFlattenFactory extends PdfAnnotationFlattenFactory {

        @Override
        public IAnnotationFlattener getAnnotationFlattenWorker(PdfName name) {
            if (PdfName.Link.equals(name)) {
                return new IAnnotationFlattener() {
                    @Override
                    public boolean flatten(PdfAnnotation annotation, PdfPage page) {
                        page.removeAnnotation(annotation);
                        return true;
                    }
                };
            }
            return super.getAnnotationFlattenWorker(name);
        }
    }
}