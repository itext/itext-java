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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.contrast.ColorContrastChecker;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.PdfUADocument;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUALayoutTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUALayoutTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<PdfConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    public static List<Object[]> roleData() {
        List<Object[]> data = new ArrayList<>();
        for (PdfConformance pdfConformance : UaValidationTestFramework.getConformanceList()) {
            for (Object o : new Object[] {
                    // Parent role, child role, expected exception
                    new Object[] {StandardRoles.FORM, StandardRoles.FORM, false},
                    new Object[] {StandardRoles.H1, StandardRoles.H1, true},
                    new Object[] {StandardRoles.P, StandardRoles.P, false},
                    new Object[] {StandardRoles.DIV, StandardRoles.P, false},
            }) {
                Object[] roles = (Object[]) o;
                data.add(new Object[] {pdfConformance, roles[0], roles[1], roles[2]});
            }
        }
        return data;
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleParagraphTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = loadFont();
            Document doc = new Document(pdfDoc);
            doc.add(new Paragraph("Simple layout PDF UA test").setFont(font));
        });
        framework.assertBothValid("simpleParagraph");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleParagraphWithUnderlineTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = loadFont();
            Document doc = new Document(pdfDoc);
            doc.add(new Paragraph("Simple layout PDF UA with underline test").setFont(font).setUnderline());
        });
        framework.assertBothValid("simpleParagraphWithUnderline");

    }

    @ParameterizedTest
    @MethodSource("roleData")
    public void testOfIllegalRelations(PdfConformance conformance, String parentRole, String childRole,
            boolean expectException)
            throws IOException {
        if (conformance.getUAConformance() == PdfUAConformance.PDF_UA_1) {
            return;
        }
        //expectException should take into account repair mechanism
        // in example P:P will be replaced as P:Span so no exceptions should be thrown
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addSuppliers( document -> {
            Div div1 = new Div();
            div1.getAccessibilityProperties().setRole(parentRole);
            Div div2 = new Div();
            div2.getAccessibilityProperties().setRole(childRole);

            div1.add(div2);
            return div1;
        });
        if (expectException) {
            framework.assertBothFail("testOfIllegalRelation_" + parentRole + "_" + childRole, false);
        } else {
            framework.assertBothValid("testOfIllegalRelation_" + parentRole + "_" + childRole);
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleBorderTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas canvas = new PdfCanvas(page);

            canvas.openTag(new CanvasTag(PdfName.Artifact));
            new DottedBorder(DeviceRgb.GREEN, 5).draw(canvas, new Rectangle(350, 700, 100, 100));
            canvas.closeTag();
        });
        framework.assertBothValid("simpleBorder");
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleTableTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDocument -> {
            Document doc = new Document(pdfDocument);

            PdfFont font = loadFont();
            Table table = new Table(new float[] {50, 50})
                    .addCell(new Cell().add(new Paragraph("cell 1, 1").setFont(font)))
                    .addCell(new Cell().add(new Paragraph("cell 1, 2").setFont(font)));
            doc.add(table);
        });
        framework.assertBothValid("simpleTable");
    }


    @ParameterizedTest
    @MethodSource("data")
    public void simpleParagraphBadContrastThrowsWcagAAAU(PdfConformance conformance) {
        if (!conformance.isPdfUA()) {
            return;
        }
        PdfDocument pdfDoc = new PdfUADocument(new PdfWriter(new ByteArrayOutputStream()),
                new com.itextpdf.pdfua.PdfUAConfig(conformance.getUAConformance(), "Hello", "en-US")) {
            @Override
            protected List<IValidationChecker> createCheckers(PdfUAConformance conformance) {
                ColorContrastChecker contrastChecker = new ColorContrastChecker(false, true);
                contrastChecker.setCheckWcagAA(false);
                List<IValidationChecker> validationCheckers = new ArrayList<>();
                validationCheckers.add(contrastChecker);
                return validationCheckers;
            }
        };
        PdfFont font = loadFont();
        Document doc = new Document(pdfDoc);
        Paragraph p = new Paragraph("Simple layout PDF UA test").setFont(font);
        p.setBackgroundColor(ColorConstants.RED);
        doc.add(p);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            pdfDoc.close();
        });
        Assertions.assertTrue(e.getMessage().contains("not WCAG AAA compliant"));
        Assertions.assertTrue(e.getMessage().contains("has contrast ratio: 5"));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleParagraphBadContrastThrowsWcagAA(PdfConformance conformance) {
        if (!conformance.isPdfUA()) {
            return;
        }
        PdfUADocument pdfDoc = new PdfUADocument(new PdfWriter(new ByteArrayOutputStream()),
                new com.itextpdf.pdfua.PdfUAConfig(conformance.getUAConformance(), "Hello", "en-US")) {

            @Override
            protected List<IValidationChecker> createCheckers(PdfUAConformance uaConformance) {
                ColorContrastChecker contrastChecker = new ColorContrastChecker(false, true);
                contrastChecker.setCheckWcagAAA(false);
                contrastChecker.setCheckWcagAA(true);
                List<IValidationChecker> validationCheckers = new ArrayList<>();
                validationCheckers.add(contrastChecker);
                return validationCheckers;
            }

        };
        PdfFont font = loadFont();
        Document doc = new Document(pdfDoc);
        Paragraph p = new Paragraph("Simple layout PDF UA test").setFont(font);
        p.setFontColor(ColorConstants.PINK);
        p.setBackgroundColor(ColorConstants.RED);
        doc.add(p);
        Exception e = Assertions.assertThrows(PdfException.class, () -> {
            pdfDoc.close();
        });
        Assertions.assertTrue(e.getMessage().contains("not WCAG AA compliant"));
        Assertions.assertTrue(e.getMessage().contains("has contrast ratio: 2"));
    }

    @ParameterizedTest
    @MethodSource("data")
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Page 1: Text: 'Simple layout PDF UA test', with font size: {0} pt "
                    + "has contrast ratio: {1}. It is not WCAG AAA compliant. ", count = 2)
    })
    public void simpleParagraphBadContrastLogsByDefaultTest(PdfConformance conformance) throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER, conformance);
        framework.addBeforeGenerationHook(pdfDoc -> {
            PdfFont font = loadFont();
            Document doc = new Document(pdfDoc);
            Paragraph p = new Paragraph("Simple layout PDF UA test").setFont(font);
            p.setBackgroundColor(ColorConstants.RED);
            doc.add(p);
        });
        framework.assertBothValid("simpleParagraphAbc");
    }


    private static PdfFont loadFont() {
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new PdfException(e.getMessage());
        }
    }
}
