/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.contrast;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@Tag("IntegrationTest")
public class ContrastAnalyzerTest extends ExtendedITextTest {


    @Test
    public void blackTextOnNoBackGround() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.moveTo(250, 250);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.showText("Test");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        assertEquals(4, results.size());
        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
        dummyDoc.close();
    }

    @Test
    public void whiteTextOnNoBackGround() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.showText("Test");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(4, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(1, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }


    @Test
    public void whiteLetterOnBlackBackGroundWhereBackgroundCompletlyCovers() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.rectangle(30, 30, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }

    @Test
    public void whiteTextOnBlackBackGroundWhereBackgroundCompletlyCovers() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.rectangle(30, 30, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.showText("AT");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(2, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }


    @Test
    public void whiteLetterBlackBackGroundWhereBackgroundHalfCovers() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.rectangle(260, 250, 200, 200);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(2, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
            assertEquals(1, result.getOverlappingAreas().get(1).getContrastRatio(), 0.1);
        }

    }

    @Test
    public void blackLetterNoFillCovers() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        canvas.rectangle(100, 100, 500, 500).clip().endPath();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();
        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21.0, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }

    @Test
    public void textDrawnOutsideOfPageShouldNotBeAnalyzed() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.beginText();
        canvas.moveText(-100, -100); // Position text outside the page
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();
        // No text should be analyzed as it's outside the page
        assertEquals(0, results.size());
    }

    @Test
    @Disabled("DEVSIX-9718: Clipping path handling needs to be improved in the ContrastAnalyzer")
    public void clipTextShouldNotShowUp() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        // Text positioning
        float x = 50;
        float y = 700;

        canvas.saveState();
        canvas.rectangle(50, 690, 200, 60); // clip width cuts text
        canvas.clip();
        canvas.endPath();

        canvas.beginText();

        canvas.setFontAndSize(PdfFontFactory.createFont(), 48);
        canvas.moveText(x, y);
        canvas.showText("1234567890");
        canvas.endText();

        canvas.restoreState();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();
        //8 characters should be fully or partially visible
        // 9 and 0 should be clipped out
        assertEquals(8, results.size());

        for (ContrastResult result : results) {
            //will need to change this as for 8 it's partially visible
            //so there will be 2 contrast ratios
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21.0, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }


    @Test
    public void whiteLetterBlackBackGroundWhereBacgroundDoesNotIntersect() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.rectangle(20, 25, 90, 90);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.GREEN, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(1.37, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }

    }

    @Test
    public void whiteLetter2BackgroundTogetherOverlapSoDefaultBackgroundShouldBeIgnored() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.rectangle(260, 250, 200, 200);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();

        canvas.rectangle(200, 250, 60, 100);
        canvas.setColor(ColorConstants.ORANGE, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 270);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(2, result.getOverlappingAreas().size());
            assertEquals(1.553, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
            assertEquals(21, result.getOverlappingAreas().get(1).getContrastRatio(), 0.1);
        }
    }

    @Test
    public void whiteTextOnOrangegroundWithLayerOn() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        // Create a layer that is initially ON
        PdfLayer layer = new PdfLayer("Background Layer", dummyDoc);
        layer.setOn(true);

        PdfCanvas canvas = new PdfCanvas(page);

        // Draw black rectangle background inside the layer
        canvas.beginLayer(layer);
        canvas.rectangle(30, 30, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2);
        canvas.setColor(ColorConstants.ORANGE, true);
        canvas.fill();
        canvas.endLayer();

        // Draw white text on top (outside the layer)
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("Test");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        // When layer is ON, the black background should be visible
        assertEquals(4, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(1.5539, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }

    @Test
    @Disabled("DEVSIX-9719 should pass without changes after fixing layering issue")
    public void whiteTextOnOrangeBackGroundWithLayerOff() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        // Create a layer that is initially OFF
        PdfLayer layer = new PdfLayer("Background Layer", dummyDoc);
        layer.setOn(false);

        PdfCanvas canvas = new PdfCanvas(page);

        // Draw black rectangle background inside the layer
        canvas.beginLayer(layer);
        canvas.rectangle(30, 30, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2);
        canvas.setColor(ColorConstants.ORANGE, true);
        canvas.fill();
        canvas.endLayer();

        // Draw white text on top (outside the layer)
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("Test");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        // When layer is OFF, the black background should still be analyzed
        // because PdfCanvasProcessor processes all content regardless of layer state
        // The layer state only affects viewer display, not the content structure
        assertEquals(4, results.size());

        for (ContrastResult result : results) {
            // The analyzer should still detect the black background even if layer is off
            // because it analyzes the actual PDF content stream
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(1, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }

    @Test
    public void whiteLetterBlackBackgroundCircle() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.circle(300, 300, 100);
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }


    @Test
    public void whiteLetterBlackBackgroundTriangle() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.moveTo(200, 200);
        canvas.lineTo(400, 200);
        canvas.lineTo(300, 400);
        canvas.closePath();

        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(1, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
        }
    }

    @Test
    public void whiteLetterBlackBackgroundTriangleHalfIntersects() throws IOException {
        PdfDocument dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = dummyDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.moveTo(240, 200);
        canvas.lineTo(400, 200);
        canvas.lineTo(300, 400);
        canvas.closePath();

        canvas.setColor(ColorConstants.BLACK, true);
        canvas.fill();
        canvas.beginText();
        canvas.moveText(250, 250);
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 32);
        canvas.showText("T");
        canvas.endText();

        List<ContrastResult> results = new ContrastAnalyzer(true).checkPageContrast(page);
        dummyDoc.close();

        assertEquals(1, results.size());

        for (ContrastResult result : results) {
            assertEquals(2, result.getOverlappingAreas().size());
            assertEquals(21, result.getOverlappingAreas().get(0).getContrastRatio(), 0.1);
            assertEquals(1, result.getOverlappingAreas().get(1).getContrastRatio(), 0.1);
        }
    }
}