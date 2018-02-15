package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class RegexBasedLocationExtractionStrategyTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/RegexBasedLocationExtractionStrategyTest/";

    @Test
    public void test01() throws IOException {
        System.out.println(new File(sourceFolder).getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "in01.pdf"));

        // build strategy
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy(Pattern.compile("\\{\\{Signature\\}\\}"));

        // get locations
        List<IPdfTextLocation> locationList = new ArrayList<>();
        for (int x = 1; x <= pdfDocument.getNumberOfPages(); x++) {
            new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(x));
            for(IPdfTextLocation location : extractionStrategy.getResultantLocations()) {
                if(location != null) {
                    locationList.add(location);
                }
            }
        }

        // compare
        Assert.assertEquals(locationList.size(), 1);

        IPdfTextLocation loc = locationList.get(0);

        Assert.assertEquals(loc.getText(), "{{Signature}}");
        Assert.assertEquals(23, (int) loc.getRectangle().getX());
        Assert.assertEquals(375, (int) loc.getRectangle().getY());
        Assert.assertEquals(52, (int) loc.getRectangle().getWidth());
        Assert.assertEquals(11, (int) loc.getRectangle().getHeight());

        // close
        pdfDocument.close();
    }
}
