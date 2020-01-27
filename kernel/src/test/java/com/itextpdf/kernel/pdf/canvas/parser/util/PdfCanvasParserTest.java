package com.itextpdf.kernel.pdf.canvas.parser.util;

import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class PdfCanvasParserTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfCanvasParserTest/";

    @Test
    public void innerArraysInContentStreamTest() throws IOException {
        String inputFileName = sourceFolder + "innerArraysInContentStream.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName));

        byte[] docInBytes = pdfDocument.getFirstPage().getContentBytes();

        RandomAccessSourceFactory factory = new RandomAccessSourceFactory();

        PdfTokenizer tokeniser = new PdfTokenizer(new RandomAccessFileOrArray(factory.createSource(docInBytes)));
        PdfResources resources = pdfDocument.getPage(1).getResources();
        PdfCanvasParser ps = new PdfCanvasParser(tokeniser, resources);

        List<PdfObject> actual = ps.parse(null);

        List<PdfObject> expected = new ArrayList<PdfObject>();
        expected.add(new PdfString("Cyan"));
        expected.add(new PdfArray(new int[] {1, 0, 0, 0}));
        expected.add(new PdfString("Magenta"));
        expected.add(new PdfArray(new int[] {0, 1, 0, 0}));
        expected.add(new PdfString("Yellow"));
        expected.add(new PdfArray(new int[] {0, 0, 1, 0}));

        PdfArray cmpArray = new PdfArray(expected);

        Assert.assertTrue(new CompareTool().compareArrays(cmpArray,
                (((PdfDictionary) actual.get(1)).getAsArray(new PdfName("ColorantsDef")))));
    }
}
