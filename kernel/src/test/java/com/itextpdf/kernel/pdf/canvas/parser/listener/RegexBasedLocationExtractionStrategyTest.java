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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
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
        Assertions.assertEquals(1, locationList.size());

        IPdfTextLocation loc = locationList.get(0);

        Assertions.assertEquals("{{Signature}}", loc.getText());
        Assertions.assertEquals(23, (int) loc.getRectangle().getX());
        Assertions.assertEquals(375, (int) loc.getRectangle().getY());
        Assertions.assertEquals(55, (int) loc.getRectangle().getWidth());
        Assertions.assertEquals(11, (int) loc.getRectangle().getHeight());

        // close
        pdfDocument.close();
    }


    // https://jira.itextsupport.com/browse/DEVSIX-1940
    // text is 'calligraphy' and 'll' is composing a ligature

    @Test
    public void testLigatureBeforeLigature() throws IOException {
        System.out.println(new File(sourceFolder).getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "ligature.pdf"));

        // build strategy
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("ca");

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
        Assertions.assertEquals(1, locationList.size());

        IPdfTextLocation loc = locationList.get(0);

        Assertions.assertEquals("ca", loc.getText());
        Rectangle rect = loc.getRectangle();
        Assertions.assertEquals(36, rect.getX(), 0.0001);
        Assertions.assertEquals(655.4600, rect.getY(), 0.0001);
        Assertions.assertEquals(25.1000, rect.getWidth(), 0.0001);
        Assertions.assertEquals(20, rect.getHeight(), 0.0001);

        pdfDocument.close();
    }

    @Test
    public void testLigatureCrossLigature() throws IOException {
        System.out.println(new File(sourceFolder).getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "ligature.pdf"));

        // build strategy
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("al");

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
        Assertions.assertEquals(1, locationList.size());

        IPdfTextLocation loc = locationList.get(0);

        Assertions.assertEquals("al", loc.getText());
        Rectangle rect = loc.getRectangle();
        Assertions.assertEquals(48.7600, rect.getX(), 0.0001);
        Assertions.assertEquals(655.4600, rect.getY(), 0.0001);
        Assertions.assertEquals(25.9799, rect.getWidth(), 0.0001);
        Assertions.assertEquals(20, rect.getHeight(), 0.0001);

        pdfDocument.close();
    }

    @Test
    public void testLigatureInLigature() throws IOException {
        System.out.println(new File(sourceFolder).getAbsolutePath());

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "ligature.pdf"));

        // build strategy
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("l");

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
        Assertions.assertEquals(1, locationList.size());

        IPdfTextLocation loc = locationList.get(0);

        Assertions.assertEquals("l", loc.getText());
        Rectangle rect = loc.getRectangle();
        Assertions.assertEquals(61.0999, rect.getX(), 0.0001);
        Assertions.assertEquals(655.4600, rect.getY(), 0.0001);
        Assertions.assertEquals(13.6399, rect.getWidth(), 0.0001);
        Assertions.assertEquals(20, rect.getHeight(), 0.0001);

        pdfDocument.close();
    }

    @Test
    public void testRotatedText() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "rotatedText.pdf"));

        // build strategy
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("abc");

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
        Assertions.assertEquals(2, locationList.size());
        Assertions.assertTrue(locationList.get(0).getRectangle().equalsWithEpsilon(new Rectangle(188.512f, 450f, 14.800003f, 25.791992f)));
        Assertions.assertTrue(locationList.get(1).getRectangle().equalsWithEpsilon(new Rectangle(36f, 746.688f, 25.792f, 14.799988f)));

        pdfDocument.close();
    }

    @Test
    public void regexStartedWithWhiteSpaceTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexStartedWithWhiteSpaceTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("\\sstart");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals(" start", locations.get(0).getText());
        Assertions.assertTrue(
                new Rectangle(92.3f, 743.3970f, 20.6159f, 13.2839f).equalsWithEpsilon(locations.get(0).getRectangle()));
    }

    @Test
    public void regexStartedWithNewLineTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexStartedWithNewLineTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("\\nstart");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals("\nstart", locations.get(0).getText());
        Assertions.assertTrue(
                new Rectangle(56.8f, 729.5970f, 20.6159f, 13.2839f).equalsWithEpsilon(locations.get(0).getRectangle()));
    }

    @Test
    public void regexWithWhiteSpacesTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexWithWhiteSpacesTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy(
                "\\sstart\\s");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals(" start ", locations.get(0).getText());
        Assertions.assertTrue(
                new Rectangle(92.3f, 743.3970f, 20.6159f, 13.2839f).equalsWithEpsilon(locations.get(0).getRectangle()));
    }

    @Test
    public void regexWithNewLinesTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexWithNewLinesTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy(
                "\\nstart\\n");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(1, locations.size());
        Assertions.assertEquals("\nstart\n", locations.get(0).getText());
        Assertions.assertTrue(
                new Rectangle(56.8f, 729.5970f, 20.6159f, 13.2839f).equalsWithEpsilon(locations.get(0).getRectangle()));
    }


    @Test
    public void regexWithNewLineBetweenWordsTest() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexWithNewLineBetweenWordsTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy(
                "hello\\nworld");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(2, locations.size());
        Assertions.assertEquals("hello\nworld", locations.get(0).getText());
        Assertions.assertEquals("hello\nworld", locations.get(1).getText());
        Assertions.assertTrue(
                new Rectangle(56.8f, 729.5970f, 27.8999f, 13.2839f).equalsWithEpsilon(locations.get(0).getRectangle()));
        Assertions.assertTrue(
                new Rectangle(56.8f, 743.3970f, 23.9039f, 13.2839f).equalsWithEpsilon(locations.get(1).getRectangle()));
    }


    @Test
    public void regexWithOnlyNewLine() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexWithNewLinesTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("\\n");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(0, locations.size());
    }

    @Test
    public void regexWithOnlyWhiteSpace() throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "regexWithWhiteSpacesTest.pdf"));
        RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy(" ");
        new PdfCanvasProcessor(extractionStrategy).processPageContent(pdfDocument.getPage(1));
        List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
        pdfDocument.close();

        Assertions.assertEquals(0, locations.size());
    }

    @Test
    public void sortCompareTest() throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "sortCompare.pdf"))) {
            RegexBasedLocationExtractionStrategy extractionStrategy = new RegexBasedLocationExtractionStrategy("a");
            PdfCanvasProcessor pdfCanvasProcessor = new PdfCanvasProcessor(extractionStrategy);
            pdfCanvasProcessor.processPageContent(pdfDocument.getPage(1));
            pdfCanvasProcessor.processPageContent(pdfDocument.getPage(2));
            List<IPdfTextLocation> locations = new ArrayList<>(extractionStrategy.getResultantLocations());
            Assertions.assertEquals(13, locations.size());
        }
    }
}
