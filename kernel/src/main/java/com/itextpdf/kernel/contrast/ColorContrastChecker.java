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

import com.itextpdf.kernel.contrast.ContrastResult.OverlappingArea;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;
import com.itextpdf.kernel.validation.context.PdfPageValidationContext;

import java.util.List;

/**
 * A validation checker that analyzes color contrast in PDF documents to ensure compliance
 * with Web Content Accessibility Guidelines (WCAG) standards.
 * <p>
 * This checker validates the contrast ratio between text and background colors to ensure
 * readability for users with visual impairments. It supports both WCAG 2.0 Level AA and
 * Level AAA conformance levels.
 * <p>
 * Features: @see {@link ContrastAnalyzer} for details.
 * <p>
 * Current Limitations @see {@link ContrastAnalyzer} for details.
 */
public class ColorContrastChecker implements IValidationChecker {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ColorContrastChecker.class);

    /**
     * Flag indicating whether to analyze contrast at the individual glyph level.
     * When true, each glyph is analyzed separately for precise contrast checking.
     */
    private final boolean checkIndividualGlyphs;
    /**
     * Flag indicating whether to throw an exception when contrast requirements are not met.
     * When false, warnings are logged instead.
     */
    private final boolean throwExceptionOnFailure;
    private double minimalPercentualCoverage = 0.1;
    /**
     * Flag indicating whether to check for WCAG AA compliance.
     * WCAG AA requires a contrast ratio of at least 4.5:1 for normal text and 3:1 for large text.
     */
    private boolean checkWcagAA;

    /**
     * Flag indicating whether to check for WCAG AAA compliance.
     * WCAG AAA requires a contrast ratio of at least 7:1 for normal text and 4.5:1 for large text.
     */
    private boolean checkWcagAAA;

    /**
     * Creates a new ColorContrastChecker with the specified configuration.
     *
     * @param checkIndividualGlyphs   if {@code true}, contrast is checked at the individual glyph level;
     *                                if {@code false}, contrast is checked at the text block level.
     *                                Individual glyph checking is more precise but may impact performance.
     * @param throwExceptionOnFailure if {@code true}, a {@link PdfException} is thrown when contrast
     *                                requirements are not met; if {@code false}, warnings are logged instead.
     */
    public ColorContrastChecker(boolean checkIndividualGlyphs, boolean throwExceptionOnFailure) {
        this.checkIndividualGlyphs = checkIndividualGlyphs;
        this.throwExceptionOnFailure = throwExceptionOnFailure;
        setCheckWcagAA(true);
        setCheckWcagAAA(true);
        setMinimalPercentualCoverage(0.1);
    }

    /**
     * Sets the minimal percentual coverage of text area that must be covered by a background
     * element for its contrast ratio to be considered in the analysis.
     * <p>
     * For example, if set to 0.1 (10%), only background elements that cover at least 10% of the
     * text area will be included in the contrast analysis. This helps filter out insignificant backgrounds
     * that do not meaningfully affect text readability. Like underlines or small decorative elements.
     *
     * @param minimalPercentualCoverage the minimal percentual coverage (between 0.0 and 1.0)
     */
    public final void setMinimalPercentualCoverage(double minimalPercentualCoverage) {
        if (minimalPercentualCoverage < 0.0 || minimalPercentualCoverage > 1.0) {
            throw new IllegalArgumentException("Minimal percentual coverage must be a value between 0.0 and 1.0");
        }
        this.minimalPercentualCoverage = minimalPercentualCoverage;
    }

    /**
     * Sets whether to check for WCAG AA compliance.
     * WCAG AA requires a contrast ratio of at least 4.5:1 for normal text
     * and 3:1 for large text (18pt+ or 14pt+ bold).
     *
     * @param checkWcagAA true to enable WCAG AA compliance checking, false to disable
     *
     * @return this ColorContrastChecker instance for method chaining
     */
    public final ColorContrastChecker setCheckWcagAA(boolean checkWcagAA) {
        this.checkWcagAA = checkWcagAA;
        logWarningIfBothChecksDisabled();
        return this;
    }

    /**
     * Sets whether to check for WCAG AAA compliance.
     * WCAG AAA requires a contrast ratio of at least 7:1 for normal text
     * and 4.5:1 for large text (18pt+ or 14pt+ bold).
     *
     * @param checkWcagAAA true to enable WCAG AAA compliance checking, false to disable
     *
     * @return this ColorContrastChecker instance for method chaining
     */
    public final ColorContrastChecker setCheckWcagAAA(boolean checkWcagAAA) {
        this.checkWcagAAA = checkWcagAAA;
        logWarningIfBothChecksDisabled();
        return this;
    }

    /**
     * Validates the given context for color contrast compliance.
     * <p>
     * This method is called by the validation framework to check color contrast
     * when a PDF page is being validated. It only processes validation contexts
     * of type {@link ValidationType#PDF_PAGE}.
     *
     * @param validationContext the validation context containing the PDF page to validate
     */
    @Override
    public void validate(IValidationContext validationContext) {
        if (validationContext.getType() == ValidationType.PDF_PAGE) {
            PdfPageValidationContext pageContext = (PdfPageValidationContext) validationContext;
            checkContrast(pageContext.getPage());
        }
    }

    /**
     * Determines if a PDF object is ready to be flushed to the output stream.
     * <p>
     * This implementation always returns true as color contrast checking does not
     * impose any restrictions on when objects can be flushed.
     *
     * @param object the PDF object to check
     *
     * @return always {@code true}
     */
    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        return true;
    }

    /**
     * Logs a warning if both WCAG AA and AAA compliance checks are disabled.
     * This helps alert users that no contrast validation will be performed.
     */
    private void logWarningIfBothChecksDisabled() {
        if (!checkWcagAA && !checkWcagAAA) {
            LOGGER.warn(KernelLogMessageConstant.BOTH_WCAG_AA_AND_AAA_COMPLIANCE_CHECKS_DISABLED);
        }
    }

    /**
     * Performs color contrast analysis on the specified PDF page.
     * <p>
     * This method analyzes all text on the page and checks if it meets the enabled
     * WCAG compliance levels (AA and/or AAA). For each non-compliant text element,
     * it either throws a {@link PdfException} or logs a warning, depending on the
     * configuration.
     * <p>
     * The method skips processing entirely if both WCAG AA and AAA checks are disabled.
     *
     * @param page the PDF page to analyze for color contrast compliance
     *
     * @throws PdfException if throwExceptionOnFailure is true and non-compliant text is found
     */
    private void checkContrast(PdfPage page) {
        if (!checkWcagAA && !checkWcagAAA) {
            // No checks enabled, skip processing
            return;
        }
        List<ContrastResult> contrastResults = new ContrastAnalyzer(checkIndividualGlyphs).checkPageContrast(page);
        for (ContrastResult contrastResult : contrastResults) {
            TextColorInfo textContrastInformation = contrastResult.getTextRenderInfo();
            for (OverlappingArea overlappingArea : contrastResult.getOverlappingAreas()) {
                if (overlappingArea.getOverlapRatio() < minimalPercentualCoverage) {
                    continue;
                }

                // Only check compliance levels that are enabled
                boolean isCompliantAAA = !checkWcagAAA || WCagChecker.isTextWcagAAACompliant(
                        textContrastInformation.getFontSize(), overlappingArea.getContrastRatio());
                boolean isCompliantAA = isCompliantAAA && (!checkWcagAA || WCagChecker.isTextWcagAACompliant(
                        textContrastInformation.getFontSize(), overlappingArea.getContrastRatio()));

                // Report only if at least one enabled check fails
                if (!isCompliantAA || !isCompliantAAA) {
                    String message = generateMessage(isCompliantAAA, isCompliantAA, contrastResult,
                            overlappingArea.getContrastRatio());
                    if (this.throwExceptionOnFailure) {
                        message = "Color contrast check failed: " + message;
                        throw new PdfException(message);
                    } else {
                        LOGGER.warn(message);
                    }
                }
            }
        }
    }

    private String generateMessage(boolean isCompliantAAA, boolean isCompliantAA,
            ContrastResult contrastResult, double contrastRatio) {
        TextColorInfo textContrastInformation = contrastResult.getTextRenderInfo();
        StringBuilder message = new StringBuilder();
        message.append("Page ").append(contrastResult.getPageNumber()).append(": ");
        if (textContrastInformation.getText() != null) {
            message.append("Text: '");
            message.append(textContrastInformation.getText());
            message.append("', ");
        }
        if (textContrastInformation.getParent() != null) {
            message.append(" parent text: '");
            message.append(textContrastInformation.getParent());
            message.append("' ");
        }

        message.append("with font size: ").append(contrastResult.getTextRenderInfo().getFontSize()).append(" pt ");
        message.append("has contrast ratio: ").append(formatFloatWithoutStringFormat(contrastRatio)).append(". ");

        if (checkWcagAA && !isCompliantAA) {
            message.append("It is not WCAG AA compliant. ");
        }
        if (checkWcagAAA && !isCompliantAAA) {
            message.append("It is not WCAG AAA compliant. ");
        }
        return message.toString();

    }

    private String formatFloatWithoutStringFormat(double value) {
        //2 decimal places
        long intValue = (long) value;
        long decimalValue = (long) Math.round((value - intValue) * 100);
        if (decimalValue < 10) {
            return intValue + "." + "0" + decimalValue;
        }
        return intValue + "." + decimalValue;

    }
}
