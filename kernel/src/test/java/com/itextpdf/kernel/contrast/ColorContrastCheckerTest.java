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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.PdfPageValidationContext;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
public class ColorContrastCheckerTest extends ExtendedITextTest {

    @Test
    public void testSetMinimalPercentualCoverageValid() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        AssertUtil.doesNotThrow(() -> {
            checker.setMinimalPercentualCoverage(0.5);
        });
    }

    @Test
    public void testSetMinimalPercentualCoverageZero() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        AssertUtil.doesNotThrow(() -> {
            checker.setMinimalPercentualCoverage(0.0);
        });
    }

    @Test
    public void testSetMinimalPercentualCoverageOne() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        AssertUtil.doesNotThrow(() -> {
            checker.setMinimalPercentualCoverage(1.0);
        });
    }

    @Test
    public void testSetMinimalPercentualCoverageNegative() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> checker.setMinimalPercentualCoverage(-0.1));
        assertEquals("Minimal percentual coverage must be a value between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    public void testSetMinimalPercentualCoverageGreaterThanOne() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> checker.setMinimalPercentualCoverage(1.1));
        assertEquals("Minimal percentual coverage must be a value between 0.0 and 1.0", exception.getMessage());
    }

    @Test
    public void testSetCheckWcagAATrue() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        ColorContrastChecker result = checker.setCheckWcagAA(true);
        assertSame(checker, result);
    }

    @Test
    public void testSetCheckWcagAAFalse() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        ColorContrastChecker result = checker.setCheckWcagAA(false);
        assertSame(checker, result);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.BOTH_WCAG_AA_AND_AAA_COMPLIANCE_CHECKS_DISABLED, logLevel =
            LogLevelConstants.WARN))
    public void testSetCheckWcagAAFalseLogsWarningWhenBothDisabled() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        AssertUtil.doesNotThrow(() -> {
            checker.setCheckWcagAAA(false);
            checker.setCheckWcagAA(false);
        });
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.BOTH_WCAG_AA_AND_AAA_COMPLIANCE_CHECKS_DISABLED, logLevel =
            LogLevelConstants.WARN))
    public void testSetCheckWcagAAAFalseLogsWarningWhenBothDisabled() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        AssertUtil.doesNotThrow(() -> {
            checker.setCheckWcagAAA(false);
            checker.setCheckWcagAA(false);
        });
    }

    @Test
    public void testIsPdfObjectReadyToFlush() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        assertTrue(checker.isPdfObjectReadyToFlush(null));
    }

    @Test
    public void testValidateWithNonPdfPageContext() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        IValidationContext context = new IValidationContext() {
            @Override
            public ValidationType getType() {
                return ValidationType.PDF_DOCUMENT;
            }
        };
        AssertUtil.doesNotThrow(() -> {
            checker.validate(context);
        });
    }

    @Test
    public void testValidateWithCompliantBlackTextOnWhiteBackground() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception - black on white is compliant
        AssertUtil.doesNotThrow(() -> {
            checker.validate(context);
        });

        pdfDoc.close();
    }

    @Test
    public void testValidateWithNonCompliantTextThrowsException() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create low contrast: light gray text on white background
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        Exception exception = assertThrows(PdfException.class, () -> checker.validate(context));

        assertTrue(exception.getMessage().contains("Color contrast check failed"));
        assertTrue(exception.getMessage().contains("Page 1"));

        pdfDoc.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "Page 1: Text: 'T', ", logLevel = LogLevelConstants.WARN))
    public void testValidateWithNonCompliantTextLogsWarning() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create low contrast: light gray text on white background
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("T");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(true, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should log warning but not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithWcagAAOnlyEnabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        checker.setCheckWcagAAA(false);
        checker.setCheckWcagAA(true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithWcagAAAOnlyEnabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        checker.setCheckWcagAA(false);
        checker.setCheckWcagAAA(true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate =
            KernelLogMessageConstant.BOTH_WCAG_AA_AND_AAA_COMPLIANCE_CHECKS_DISABLED, logLevel =
            LogLevelConstants.WARN))
    public void testValidateWithBothWcagChecksDisabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create low contrast text
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.CYAN, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, true);
        checker.setCheckWcagAA(false);
        checker.setCheckWcagAAA(false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception because checks are disabled
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithIndividualGlyphsEnabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("ABC");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(true, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithLargeText() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Large text (20pt) has different WCAG requirements
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 20);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithTextOnColoredBackground() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Draw colored background
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.BLUE);
        canvas.rectangle(50, 50, 200, 100);
        canvas.fill();

        // Draw white text on blue background
        canvas.beginText();
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception - white on blue should be compliant
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithMinimalCoverageFiltering() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        checker.setMinimalPercentualCoverage(0.9); // Very high threshold
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testMethodChaining() {
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        ColorContrastChecker result = checker
                .setCheckWcagAA(true)
                .setCheckWcagAAA(false);

        assertSame(checker, result);
    }

    @Test
    public void testValidateWithTextWithoutParent() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create text without parent context
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("X");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(true, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithAACompliantButNotAAA() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create a scenario where AA passes but AAA fails
        // Using gray on white which has ~4.5:1 ratio
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY);
        canvas.rectangle(50, 50, 200, 100);
        canvas.fill();

        canvas.beginText();
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 80);
        canvas.showText("Test");
        canvas.endText();

        // Enable only AAA check
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        checker.setCheckWcagAA(false);
        checker.setCheckWcagAAA(true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception with logging mode
        AssertUtil.doesNotThrow(() -> {
            checker.validate(context);
        });

        pdfDoc.close();
    }

    @Test
    public void testValidateWithMultipleBackgrounds() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Draw multiple overlapping backgrounds
        PdfCanvas canvas = new PdfCanvas(page);

        // First background
        canvas.setFillColor(ColorConstants.RED);
        canvas.rectangle(50, 50, 150, 100);
        canvas.fill();

        // Second overlapping background
        canvas.setFillColor(ColorConstants.BLUE);
        canvas.rectangle(100, 50, 150, 100);
        canvas.fill();

        // Draw text over overlapping area
        canvas.beginText();
        canvas.setColor(ColorConstants.WHITE, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(120, 80);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithSmallFontAndLowContrast() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Small font with insufficient contrast
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 8); // Small font
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should throw exception for low contrast with small font
        assertThrows(PdfException.class, () -> checker.validate(context));

        pdfDoc.close();
    }

    @Test
    public void testValidateWithLargeFontAndModerateContrast() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Large font (>18pt) has more lenient requirements
        PdfCanvas canvas = new PdfCanvas(page);

        // Background
        canvas.setFillColor(ColorConstants.LIGHT_GRAY);
        canvas.rectangle(50, 50, 200, 100);
        canvas.fill();

        // Large text
        canvas.beginText();
        canvas.setColor(ColorConstants.BLACK, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 24);
        canvas.moveText(100, 80);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw exception - large text has lower requirements
        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateWithZeroCoverageThreshold() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        checker.setMinimalPercentualCoverage(0.0); // Include all backgrounds
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        checker.validate(context);

        pdfDoc.close();
    }

    @Test
    public void testValidateMessageIncludesParentText() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create low contrast text that will have parent context
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(true, true);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        Exception e = Assertions.assertThrows(PdfException.class, () -> checker.validate(context));
        System.out.println(e.getMessage());
        Assertions.assertTrue(e.getMessage().contains("parent text: 'Test'"));
    }

    @Test
    public void testValidateAAFailsButAAADisabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create text that fails AA but AAA is disabled
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        ColorContrastChecker checker = new ColorContrastChecker(false, true);
        checker.setCheckWcagAAA(false); // Disable AAA
        checker.setCheckWcagAA(true);   // Enable AA
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        Exception exception = assertThrows(PdfException.class, () -> checker.validate(context));
        assertTrue(exception.getMessage().contains("WCAG AA compliant"));
        assertFalse(exception.getMessage().contains("WCAG AAA compliant"));

        pdfDoc.close();
    }

    @Test
    public void testValidateAAAFailsButAADisabled() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ValidationContainer container = new ValidationContainer();
        ColorContrastChecker contrastChecker = new ColorContrastChecker(false, true);
        contrastChecker.setCheckWcagAA(false);
        contrastChecker.setCheckWcagAAA(true);
        container.addChecker(contrastChecker);

        pdfDoc.getDiContainer().register(ValidationContainer.class, container);

        PdfPage page = pdfDoc.addNewPage();

        // Create text that passes AA but fails AAA
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setFillColor(ColorConstants.LIGHT_GRAY);
        canvas.rectangle(50, 50, 200, 100);
        canvas.fill();

        canvas.beginText();
        canvas.setColor(ColorConstants.CYAN, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 80);
        canvas.showText("Test");
        canvas.endText();

        PdfPageValidationContext context = new PdfPageValidationContext(page);

        Exception exception = assertThrows(PdfException.class, () -> pdfDoc.close());
        assertTrue(exception.getMessage().contains("WCAG AAA compliant"));
        assertFalse(exception.getMessage().contains("WCAG AA compliant"));

    }

    @Test
    public void testValidateBothAAAndAAAFail() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ValidationContainer container = new ValidationContainer();
        container.addChecker(new ColorContrastChecker(false, true));

        pdfDoc.getDiContainer().register(ValidationContainer.class, container);

        PdfPage page = pdfDoc.addNewPage();

        // Create very low contrast that fails both
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setColor(ColorConstants.LIGHT_GRAY, true);
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        Exception exception = assertThrows(PdfException.class, () -> pdfDoc.close());
        assertTrue(exception.getMessage().contains("WCAG AA compliant"));
        assertTrue(exception.getMessage().contains("WCAG AAA compliant"));

    }

    @Test
    public void testConstructorSetsDefaultValues() throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfPage page = pdfDoc.addNewPage();

        // Create compliant text
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.beginText();
        canvas.setFontAndSize(PdfFontFactory.createFont(), 12);
        canvas.moveText(100, 100);
        canvas.showText("Test");
        canvas.endText();

        // Constructor should set both WCAG checks to true by default
        ColorContrastChecker checker = new ColorContrastChecker(false, false);
        PdfPageValidationContext context = new PdfPageValidationContext(page);

        // Should not throw - defaults allow compliant text
        checker.validate(context);

        pdfDoc.close();
    }
}