package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotationAppearance;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Map;

@Category(IntegrationTest.class)
public class PdfNameTreeTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfNameTreeTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfNameTreeTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void EmbeddedFileAndJavascriptTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "FileWithSingleAttachment.pdf"));
        PdfNameTree embeddedFilesNameTree = pdfDocument.getCatalog().getNameTree(PdfName.EmbeddedFiles);
        Map<String, PdfObject> objs = embeddedFilesNameTree.getNames();
        PdfNameTree javascript = pdfDocument.getCatalog().getNameTree(PdfName.JavaScript);
        Map<String, PdfObject> objs2 = javascript.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
        Assert.assertEquals(1, objs2.size());
    }

    @Test
    public void AnnotationAppearanceTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destinationFolder + "AnnotationAppearanceTest.pdf"));
        PdfPage page = pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.MAGENTA).beginText().setFontAndSize(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN), 30)
                .setTextMatrix(25, 500).showText("This file has AP key in Names dictionary").endText();
        PdfArray array = new PdfArray();
        array.add(new PdfString("normalAppearance"));
        array.add(new PdfAnnotationAppearance().setState(PdfName.N, new PdfFormXObject(new Rectangle(50, 50 , 50, 50))).getPdfObject());

        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Names, array);
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.AP, dict);
        pdfDocument.getCatalog().getPdfObject().put(PdfName.Names, dictionary);

        PdfNameTree appearance = pdfDocument.getCatalog().getNameTree(PdfName.AP);
        Map<String, PdfObject> objs = appearance.getNames();
        pdfDocument.close();
        Assert.assertEquals(1, objs.size());
    }
}
