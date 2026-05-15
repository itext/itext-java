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
package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class PageMarginLayoutResultTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/PageMarginLayoutResultTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/PageMarginLayoutResultTest/";

    private static final String TEXT_BYRON =
            "When a man hath no freedom to fight for at home,\n" +
                    "    Let him combat for that of his neighbours;\n" +
                    "Let him think of the glories of Greece and of Rome,\n" +
                    "    And get knocked on the head for his labours.\n" +
                    "\n" +
                    "To do good to Mankind is the chivalrous plan,\n" +
                    "    And is always as nobly requited;\n" +
                    "Then battle for Freedom wherever you can,\n" +
                    "    And, if not shot or hanged, you'll get knighted.";

    private static final float A4_HEIGHT = PageSize.A4.getHeight();
    private static final float A4_WIDTH  = PageSize.A4.getWidth();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void shortElementFullResultWithModestMarginsAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            applyMarginBoxes(document, 100, 100, 0, 0);

            Paragraph p = new Paragraph(TEXT_BYRON);
            int status = layoutStatus(p, document, availableRect(100, 100, 0, 0));

            Assertions.assertEquals(LayoutResult.FULL, status,
                    "Short paragraph should fit fully with modest margin boxes");
        }
    }

    @Test
    public void shortElementFullResultWithLargeHorizontalMarginsAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            applyMarginBoxes(document, 0, 0, 150, 150);

            Paragraph p = new Paragraph("Short text.");
            int status = layoutStatus(p, document, availableRect(0, 0, 150, 150));

            Assertions.assertEquals(LayoutResult.FULL, status,
                    "Short paragraph should fit fully even with large horizontal margins");
        }
    }

    @Test
    public void fullResultWithSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "fullResultSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("Page 1 — no margin boxes.").add(TEXT_BYRON));
            document.add(new SectionBreak(marginBoxes(100, 80, 0, 0)));
            document.add(shortContentDiv());
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void tallElementPartialResultWithLargeMarginsAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            applyMarginBoxes(document, 250, 200, 0, 0);

            Div tall = new Div()
                    .add(new Paragraph(repeatString(TEXT_BYRON, 4)))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29));

            int status = layoutStatus(tall, document, availableRect(250, 200, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status,
                    "Tall element should split (PARTIAL) when top/bottom margin boxes are large");
        }
    }

    @Test
    public void partialResultWithLargeMarginsTest() throws IOException, InterruptedException {
        String fileName = "partialLargeMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(marginBoxes(250, 200, 0, 0)));

            Div tall = new Div().setBackgroundColor(new DeviceRgb(209, 247, 29));
            for (int i = 0; i < 6; i++) {
                tall.add(new Paragraph("PARAGRAPH " + i + "\n" + TEXT_BYRON));
            }
            document.add(tall);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void partialResultWithAreaBreakAndMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "partialAreaBreakMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> true, marginBoxes(200, 150, 0, 0));

            Div tall = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205));
            for (int i = 0; i < 4; i++) {
                tall.add(new Paragraph("BLOCK " + i + "\n" + TEXT_BYRON));
            }

            document.add(tall);
            document.add(new AreaBreak());
            document.add(new Paragraph("After AreaBreak — same large margins."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void elementTooLargeNothingResultAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float hugeMargin = (A4_HEIGHT - 20f) / 2f;
            applyMarginBoxes(document, hugeMargin, hugeMargin, 0, 0);

            Div element = new Div()
                    .add(new Paragraph(TEXT_BYRON))
                    .setHeight(100);

            int status = layoutStatus(element, document,
                    availableRect(hugeMargin, hugeMargin, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element with explicit height greater than available area should return NOTHING");
        }
    }

    @Test
    public void hugeBottomFootnoteMarginsNothingResultAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float hugeBottom = A4_HEIGHT - 30f;
            applyMarginBoxes(document, 0, hugeBottom, 0, 0);

            Div element = new Div()
                    .add(new Paragraph(TEXT_BYRON))
                    .setHeight(80);

            int status = layoutStatus(element, document,
                    availableRect(0, hugeBottom, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when huge bottom footnote margin leaves no space");
        }
    }

    @Test
    public void allFourLargeMarginsNothingResultAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.40f;
            float left   = A4_WIDTH  * 0.40f;
            float right  = A4_WIDTH  * 0.40f;

            Div element = new Div()
                    .add(new Paragraph(TEXT_BYRON))
                    .setHeight(200)
                    .setKeepTogether(true);

            int status = layoutStatus(element, document,
                    availableRect(top, bottom, left, right));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Non-splittable element (keepTogether) taller than available area should return NOTHING");
        }
    }

    @Test
    public void forcedPlacementWithExtremeMarginBoxesTest()
            throws IOException, InterruptedException {
        String fileName = "forcedPlacementExtremeMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(extremeMarginBoxes()));

            Div forced = new Div()
                    .add(new Paragraph("FORCED — margin boxes left almost no room."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            forced.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);

            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void progressivelyLargerMarginsTransitionTest()
            throws IOException, InterruptedException {
        String fileName = "marginsTransition";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(marginBoxes(50, 50, 0, 0)));
            document.add(contentDiv("SMALL MARGINS — FULL", new DeviceRgb(65, 151, 29)));

            document.add(new SectionBreak(marginBoxes(200, 180, 0, 0)));
            document.add(contentDiv("MEDIUM MARGINS — PARTIAL", new DeviceRgb(209, 247, 29)));

            document.add(new SectionBreak(extremeMarginBoxes()));
            Div forced = contentDiv("EXTREME MARGINS — FORCED", new DeviceRgb(255, 100, 100));
            forced.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void progressivelyLargerMarginsStatusAssertTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            Div element = new Div()
                    .add(new Paragraph(TEXT_BYRON))
                    .setHeight(200)
                    .setBackgroundColor(new DeviceRgb(65, 151, 29));

            int smallStatus = layoutStatus(element, document, availableRect(50, 50, 0, 0));
            Assertions.assertEquals(LayoutResult.FULL, smallStatus,
                    "Expected FULL with small margin boxes (200pt element, ~670pt area)");

            float partialTop    = 300f;
            float partialBottom = 300f;
            int mediumStatus = layoutStatus(element, document,
                    availableRect(partialTop, partialBottom, 0, 0));
            Assertions.assertEquals(LayoutResult.PARTIAL, mediumStatus,
                    "Expected PARTIAL with large margin boxes (200pt element, ~170pt area)");

            float hugeMargin = (A4_HEIGHT - 10f) / 2f;
            int nothingStatus = layoutStatus(element, document,
                    availableRect(hugeMargin, hugeMargin, 0, 0));
            Assertions.assertEquals(LayoutResult.NOTHING, nothingStatus,
                    "Expected NOTHING with extreme margin boxes (~1pt area)");
        }
    }

    @Test
    public void nothingThenAreaBreakRestoresMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nothingAreaBreakRestoresMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(extremeMarginBoxes()));
            Div forced = contentDiv("PAGE 1 — EXTREME MARGINS (FORCED)", new DeviceRgb(255, 100, 100));
            forced.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            document.add(forced);

            document.add(new AreaBreak());
            document.add(new SectionBreak(marginBoxes(80, 80, 0, 0)));
            document.add(contentDiv("PAGE 2+ — NORMAL MARGINS", new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nothingThenFullViaTwoSectionBreaksTest()
            throws IOException, InterruptedException {
        String fileName = "nothingThenFullTwoSectionBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(extremeMarginBoxes()));
            Div forced = contentDiv("EXTREME — FORCED PLACEMENT", new DeviceRgb(255, 100, 100));
            forced.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            document.add(forced);

            document.add(new SectionBreak(marginBoxes(80, 80, 0, 0)));
            document.add(contentDiv("MODEST — FITS FULLY", new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void alternatingExtremeAndNormalDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "alternatingExtremeNormalMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 != 0
                    ? extremeMarginBoxes()
                    : marginBoxes(80, 80, 0, 0));

            for (int page = 1; page <= 4; page++) {
                Div div = contentDiv("PAGE " + page, cellColor(page - 1));
                if (page % 2 != 0) {
                    div.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
                }
                document.add(div);
                if (page < 4) {
                    document.add(new AreaBreak());
                }
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static PageMarginBoxes marginBoxes(float top, float bottom,
            float left, float right) {
        List<PageMarginContent> elements = new ArrayList<>();
        if (top > 0) {
            elements.add(new PageMarginContent(MarginBoxName.TOP,
                    new Div().add(new Paragraph("TOP"))
                            .setBackgroundColor(ColorConstants.PINK)
                            .setHeight(top)));
        }
        if (bottom > 0) {
            elements.add(new PageMarginContent(MarginBoxName.BOTTOM,
                    new Div().add(new Paragraph("BOTTOM"))
                            .setBackgroundColor(ColorConstants.ORANGE)
                            .setHeight(bottom)));
        }
        if (left > 0) {
            elements.add(new PageMarginContent(MarginBoxName.LEFT,
                    new Div().add(new Paragraph("L"))
                            .setBackgroundColor(ColorConstants.BLUE)
                            .setWidth(left)));
        }
        if (right > 0) {
            elements.add(new PageMarginContent(MarginBoxName.RIGHT,
                    new Div().add(new Paragraph("R"))
                            .setBackgroundColor(ColorConstants.YELLOW)
                            .setWidth(right)));
        }
        return new PageMarginBoxes(elements);
    }

    private static PageMarginBoxes extremeMarginBoxes() {
        float topBottom = (A4_HEIGHT - 10f) / 2f;
        return marginBoxes(topBottom, topBottom, 0, 0);
    }

    private static void applyMarginBoxes(Document document,
            float top, float bottom, float left, float right) {
        document.setPageMargins(pageNum -> true, marginBoxes(top, bottom, left, right));
    }

    private static Rectangle availableRect(float top, float bottom,
            float left, float right) {
        float docMargin = 36f;
        float x = docMargin + left;
        float y = docMargin + bottom;
        float w = A4_WIDTH  - 2 * docMargin - left - right;
        float h = A4_HEIGHT - 2 * docMargin - top  - bottom;
        return new Rectangle(x, y, Math.max(w, 1f), Math.max(h, 1f));
    }

    private static int layoutStatus(Object element, Document document,
            Rectangle area) {
        IRenderer renderer;
        if (element instanceof Div) {
            renderer = ((Div) element).createRendererSubTree()
                    .setParent(document.getRenderer());
        } else if (element instanceof Paragraph) {
            renderer = ((Paragraph) element).createRendererSubTree()
                    .setParent(document.getRenderer());
        } else {
            throw new IllegalArgumentException("Unsupported element type");
        }
        LayoutResult result = renderer.layout(
                new LayoutContext(new LayoutArea(1, area)));
        return result.getStatus();
    }

    private static Div contentDiv(String label, DeviceRgb color) {
        Div div = new Div().setBackgroundColor(color);
        div.add(new Paragraph(label));
        div.add(new Paragraph(repeatString(TEXT_BYRON, 2)));
        return div;
    }

    private static Div shortContentDiv() {
        return new Div()
                .add(new Paragraph("Short content — fits fully."))
                .setBackgroundColor(new DeviceRgb(65, 151, 29));
    }

    private static DeviceRgb cellColor(int index) {
        DeviceRgb[] palette = {
                new DeviceRgb(65, 151, 29),
                new DeviceRgb(209, 247, 29),
                new DeviceRgb(78, 151, 205),
                new DeviceRgb(255, 165, 0)
        };
        return palette[index % palette.length];
    }

    private static String repeatString(String s, int n) {
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

}