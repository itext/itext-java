package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class PdfContentExtractionTest extends ExtendedITextTest {
    
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/PdfContentExtractionTest/";

    @Test
    //TODO: remove the expected exception construct once the issue is fixed (DEVSIX-1279)
    public void contentExtractionInDocWithBigCoordinatesTest() throws IOException {
        junitExpectedException.expect(IllegalStateException.class);

        String inputFileName = sourceFolder + "docWithBigCoordinates.pdf";
        //In this document the CTM shrinks coordinates and this coordinates are large numbers.
        // At the moment creation of this test clipper has a problem with handling large numbers
        // since internally it deals with integers and has to multuply large numbers even more
        // for internal purposes

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName));
        PdfDocumentContentParser contentParser = new PdfDocumentContentParser(pdfDocument);
        contentParser.processContent(1, new LocationTextExtractionStrategy());
    }
}