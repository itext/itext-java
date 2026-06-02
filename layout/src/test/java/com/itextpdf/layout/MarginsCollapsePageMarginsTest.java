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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class MarginsCollapsePageMarginsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/MarginsCollapsePageMarginsTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/MarginsCollapsePageMarginsTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void collapsingMarginsWithSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "collapsingMarginsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            document.add(marginedDiv("TOP SIBLING", new DeviceRgb(65, 151, 29), 40, 40));
            document.add(marginedDiv("BOTTOM SIBLING", new DeviceRgb(209, 247, 29), 60, 40));
            document.add(marginedDiv("THIRD SIBLING", new DeviceRgb(78, 151, 205), 30, 30));

            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — margins1 active; collapsing still on."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsTwoSectionBreaksTest() throws IOException, InterruptedException {
        String fileName = "collapsingTwoSectionBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            addSiblingBlock(document, "SECTION 1");

            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            addSiblingBlock(document, "SECTION 2");

            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            addSiblingBlock(document, "SECTION 3");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void elementLevelCollapsingWithSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "elemCollapsingWithSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div collapsing = new Div();
            collapsing.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            collapsing.add(marginedDiv("COLLAPSED A", new DeviceRgb(65, 151, 29), 50, 50));
            collapsing.add(marginedDiv("COLLAPSED B", new DeviceRgb(209, 247, 29), 30, 30));
            collapsing.add(marginedDiv("COLLAPSED C", new DeviceRgb(78, 151, 205), 40, 40));

            document.add(collapsing);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));

            Div nonCollapsing = new Div();
            nonCollapsing.add(marginedDiv("NON-COLLAPSED A", new DeviceRgb(65, 151, 29), 50, 50));
            nonCollapsing.add(marginedDiv("NON-COLLAPSED B", new DeviceRgb(209, 247, 29), 30, 30));
            nonCollapsing.add(marginedDiv("NON-COLLAPSED C", new DeviceRgb(78, 151, 205), 40, 40));
            document.add(nonCollapsing);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void parentChildCollapsingWithSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "parentChildCollapsingSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            Div parent = new Div()
                    .setMarginTop(60)
                    .setBackgroundColor(new DeviceRgb(220, 220, 220));
            Div child = marginedDiv("CHILD (40pt top margin)", new DeviceRgb(65, 151, 29), 40, 20);
            parent.add(child);
            parent.add(marginedDiv("SIBLING IN PARENT", new DeviceRgb(209, 247, 29), 30, 30));

            document.add(parent);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsSameSectionBreakTwiceTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingSameSectionBreakTwice";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            addSiblingBlock(document, "SECTION 1");
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            addSiblingBlock(document, "SECTION 2");
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            addSiblingBlock(document, "SECTION 3");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsAcrossAreaBreakTest() throws IOException, InterruptedException {
        String fileName = "collapsingAcrossAreaBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> Boolean.TRUE,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            addSiblingBlock(document, "PAGE 1");
            document.add(new AreaBreak());
            addSiblingBlock(document, "PAGE 2");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsAlternatingSectionAndAreaBreaksTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingAltBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            addSiblingBlock(document, "PAGE 1 — no margins");
            document.add(new AreaBreak());
            addSiblingBlock(document, "PAGE 2 — no margins");
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            addSiblingBlock(document, "PAGE 3 — margins1");
            document.add(new AreaBreak());
            addSiblingBlock(document, "PAGE 4 — still margins1");
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            addSiblingBlock(document, "PAGE 5 — margins2");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsAreaBreakWithPageSizeTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingAreaBreakPageSize";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> Boolean.TRUE,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            addSiblingBlock(document, "A4 PAGE");
            document.add(new AreaBreak(PageSize.A5));
            addSiblingBlock(document, "A5 PAGE");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsWithDocumentPageMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingDocPageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            for (int i = 0; i < 5; i++) {
                document.add(marginedDiv("BLOCK " + i, cellColor(i), 50, 50));
                document.add(new Paragraph(TestResourceUtil.getByronStanza()));
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsWithPerPageDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingPerPageDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> {
                List<PageMarginContent> margins = new ArrayList<>();
                margins.add(new PageMarginContent(MarginBoxName.TOP,
                        new Div()
                                .add(new Paragraph("Page " + pageNum))
                                .setBackgroundColor(ColorConstants.PINK)
                                .setTextAlignment(TextAlignment.CENTER)));
                return new PageMarginBoxes(margins);
            });

            for (int i = 0; i < 8; i++) {
                document.add(marginedDiv("BLOCK " + i, cellColor(i), 40, 40));
                document.add(new Paragraph(TestResourceUtil.getByronStanza()));
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsDocumentMarginsOverriddenBySectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingDocMarginsOverriddenBySectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            addSiblingBlock(document, "SECTION 1 — even-page margins2 active");
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            addSiblingBlock(document, "SECTION 2 — margins1 override");
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingMarginsWithStaticDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "collapsingStaticDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setMargins(80, 80, 80, 80);

            for (int i = 0; i < 3; i++) {
                document.add(marginedDiv("BLOCK " + i, cellColor(i), 50, 50));
                document.add(new Paragraph(TestResourceUtil.getByronStanza()));
            }

            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));

            for (int i = 3; i < 6; i++) {
                document.add(marginedDiv("BLOCK " + i, cellColor(i), 50, 50));
                document.add(new Paragraph(TestResourceUtil.getByronStanza()));
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void collapsingOnVsOffWithPageMarginsThrowsTest() throws IOException {
        String fileName = "collapsingOnVsOff";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> Boolean.TRUE,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.FALSE);
            document.add(new Paragraph("COLLAPSING OFF").setFontSize(14));
            addSiblingBlock(document, "NO COLLAPSE");

            document.add(new AreaBreak());
            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            Paragraph collapsingOn = new Paragraph("COLLAPSING ON").setFontSize(14);

            Assertions.assertThrows(NullPointerException.class,
                    () -> document.add(collapsingOn),
                    "Expected NPE when adding a new p element with changed collapse property.");
        }
    }

    @Test
    public void nestedDivsCollapsingWithSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "nestedDivsCollapsingSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);

            Div level1 = new Div().setMarginTop(60);
            Div level2 = new Div().setMarginTop(40);
            Div level3 = marginedDiv("DEEPEST CHILD", new DeviceRgb(65, 151, 29), 30, 30);
            level2.add(level3);
            level2.add(marginedDiv("SIBLING IN L2", new DeviceRgb(209, 247, 29), 20, 20));
            level1.add(level2);
            level1.add(marginedDiv("SIBLING IN L1", new DeviceRgb(78, 151, 205), 25, 25));

            document.add(level1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Page 2 — margins2 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedDivsCollapsingWithAreaBreakAndDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedDivsCollapsingAreaBreakDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div outer = new Div().setMarginTop(50);
            outer.add(marginedDiv("NESTED A", new DeviceRgb(65, 151, 29), 40, 40));
            outer.add(marginedDiv("NESTED B", new DeviceRgb(209, 247, 29), 30, 30));
            document.add(outer);

            document.add(new AreaBreak());

            Div outer2 = new Div().setMarginTop(50);
            outer2.add(marginedDiv("NESTED C", new DeviceRgb(78, 151, 205), 40, 40));
            outer2.add(marginedDiv("NESTED D", new DeviceRgb(255, 165, 0), 30, 30));
            document.add(outer2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static void addSiblingBlock(Document document, String label) {
        document.add(marginedDiv(label + " — A (50/50)", new DeviceRgb(65, 151, 29), 50, 50));
        document.add(marginedDiv(label + " — B (20/20)", new DeviceRgb(209, 247, 29), 20, 20));
        document.add(marginedDiv(label + " — C (40/40)", new DeviceRgb(78, 151, 205), 40, 40));
    }

    private static Div marginedDiv(String label, DeviceRgb color,
            float marginTop, float marginBottom) {
        return new Div()
                .add(new Paragraph(label))
                .setBackgroundColor(color)
                .setMarginTop(marginTop)
                .setMarginBottom(marginBottom)
                .setPadding(6);
    }

    private static DeviceRgb cellColor(int index) {
        DeviceRgb[] palette = {
                new DeviceRgb(65, 151, 29),
                new DeviceRgb(209, 247, 29),
                new DeviceRgb(78, 151, 205),
                new DeviceRgb(255, 165, 0),
                new DeviceRgb(200, 100, 100),
                new DeviceRgb(100, 200, 100)
        };
        return palette[index % palette.length];
    }
}