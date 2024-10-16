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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfA4Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;

import org.slf4j.LoggerFactory;

/**
 * This class extends {@link PdfDocument} and is in charge of creating files
 * that comply with the PDF/A standard.
 *
 * <p>
 * Client code is still responsible for making sure the file is actually PDF/A
 * compliant: multiple steps must be undertaken (depending on the
 * {@link PdfConformance}) to ensure that the PDF/A standard is followed.
 *
 * <p>
 * This class will throw exceptions, mostly {@link PdfAConformanceException},
 * and thus refuse to output a PDF/A file if at any point the document does not
 * adhere to the PDF/A guidelines specified by the {@link PdfConformance}.
 */
public class PdfADocument extends PdfDocument {
    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param aConformance the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     */
    public PdfADocument(PdfWriter writer, PdfAConformance aConformance, PdfOutputIntent outputIntent) {
        this(writer, aConformance, outputIntent, new DocumentProperties());
    }

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param aConformance the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     * @param properties a {@link com.itextpdf.kernel.pdf.DocumentProperties}
     */
    public PdfADocument(PdfWriter writer, PdfAConformance aConformance, PdfOutputIntent outputIntent, DocumentProperties properties) {
        super(configureWriterProperties(writer, aConformance), properties);

        PdfAChecker checker = getCorrectCheckerFromConformance(getConformance().getAConformance());
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(checker);
        getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfAPageFactory(checker);
        this.documentInfoHelper = new PdfADocumentInfoHelper(this);
        this.defaultFontStrategy = new PdfADefaultFontStrategy(this);
        addOutputIntent(outputIntent);
    }

    /**
     * Opens a PDF/A document in the stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfADocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param properties properties of the stamping process
     */
    public PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties);
        if (!getConformance().isPdfA()) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.
                            DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);

        }

        PdfAChecker checker = getCorrectCheckerFromConformance(getConformance().getAConformance());
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(checker);
        getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfAPageFactory(checker);
        this.documentInfoHelper = new PdfADocumentInfoHelper(this);
        this.defaultFontStrategy = new PdfADefaultFontStrategy(this);
    }

    /**
     * Gets correct {@link PdfAChecker} for specified PDF/A conformance.
     *
     * @param aConformance the conformance for which checker is needed
     *
     * @return the correct PDF/A checker
     */
    public static PdfAChecker getCorrectCheckerFromConformance(PdfAConformance aConformance) {
        PdfAChecker checker;
        switch (aConformance.getPart()) {
            case "1":
                checker = new PdfA1Checker(aConformance);
                break;
            case "2":
                checker = new PdfA2Checker(aConformance);
                break;
            case "3":
                checker = new PdfA3Checker(aConformance);
                break;
            case "4":
                checker = new PdfA4Checker(aConformance);
                break;
            default:
                throw new IllegalArgumentException(PdfaExceptionMessageConstant
                        .CANNOT_FIND_PDFA_CHECKER_FOR_SPECIFIED_NAME);
        }
        return checker;
    }

    private static PdfVersion getPdfVersionAccordingToConformance(PdfAConformance aConformance) {
        switch (aConformance.getPart()) {
            case "1":
                return PdfVersion.PDF_1_4;
            case "2":
            case "3":
                return PdfVersion.PDF_1_7;
            case "4":
                return PdfVersion.PDF_2_0;
            default:
                return PdfVersion.PDF_1_7;
        }
    }

    private static PdfWriter configureWriterProperties(PdfWriter writer, PdfAConformance aConformance) {
        writer.getProperties().addPdfAXmpMetadata(aConformance);
        final PdfVersion aConformancePdfVersion = getPdfVersionAccordingToConformance(aConformance);
        if (writer.getPdfVersion() != null && !writer.getPdfVersion().equals(aConformancePdfVersion)) {
            LoggerFactory.getLogger(PdfADocument.class).warn(MessageFormatUtil.format(
                    PdfALogMessageConstant.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN, aConformancePdfVersion));
        }
        writer.getProperties().setPdfVersion(aConformancePdfVersion);
        return writer;
    }
}
