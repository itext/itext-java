/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.pdfua;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.pdfua.checkers.PdfUA1Checker;
import com.itextpdf.pdfua.checkers.PdfUA2Checker;
import com.itextpdf.pdfua.checkers.PdfUAChecker;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a Pdf/UA document.
 * This class is an extension of PdfDocument and adds the necessary configuration for PDF/UA conformance.
 * It will add necessary validation to guide the user to create a PDF/UA compliant document.
 */
public class PdfUADocument extends PdfDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfUADocument.class);

    /**
     * Creates a PdfUADocument instance.
     *
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfWriter writer, PdfUAConfig config) {
        this(writer, new DocumentProperties(), config);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfWriter writer, DocumentProperties properties, PdfUAConfig config) {
        super(configureWriterProperties(writer, config.getConformance()), properties);
        this.pdfConformance = new PdfConformance(config.getConformance());

        setupUAConfiguration(config);
        final ValidationContainer validationContainer = new ValidationContainer();
        final PdfUAChecker checker = getCorrectCheckerFromConformance(config.getConformance());
        validationContainer.addChecker(checker);
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfUAPageFactory(checker);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param reader The reader to read the PDF document.
     * @param writer The writer to write the PDF document.
     * @param config The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfReader reader, PdfWriter writer, PdfUAConfig config) {
        this(reader, writer, new StampingProperties(), config);
    }

    /**
     * Creates a PdfUADocument instance.
     *
     * @param reader     The reader to read the PDF document.
     * @param writer     The writer to write the PDF document.
     * @param properties The properties for the PDF document.
     * @param config     The configuration for the PDF/UA document.
     */
    public PdfUADocument(PdfReader reader, PdfWriter writer, StampingProperties properties, PdfUAConfig config) {
        super(reader, writer, properties);
        if (!getConformance().isPdfUA()) {
            LOGGER.warn(PdfUALogMessageConstants.PDF_TO_PDF_UA_CONVERSION_IS_NOT_SUPPORTED);
        }

        setupUAConfiguration(config);

        final ValidationContainer validationContainer = new ValidationContainer();
        final PdfUAChecker checker = getCorrectCheckerFromConformance(config.getConformance());
        validationContainer.addChecker(checker);
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
        this.pdfPageFactory = new PdfUAPageFactory(checker);
    }

    private static PdfWriter configureWriterProperties(PdfWriter writer, PdfUAConformance uaConformance) {
        writer.getProperties().addPdfUaXmpMetadata(uaConformance);
        if (writer.getPdfVersion() != null) {
            if (uaConformance == PdfUAConformance.PDF_UA_1 && !writer.getPdfVersion().equals(PdfVersion.PDF_1_7)) {
                LOGGER.warn(MessageFormatUtil.format(
                        PdfUALogMessageConstants.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN, PdfVersion.PDF_1_7));
                writer.getProperties().setPdfVersion(PdfVersion.PDF_1_7);
            }
            if (uaConformance == PdfUAConformance.PDF_UA_2 && !writer.getPdfVersion().equals(PdfVersion.PDF_2_0)) {
                LOGGER.warn(MessageFormatUtil.format(
                        PdfUALogMessageConstants.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN, PdfVersion.PDF_2_0));
                writer.getProperties().setPdfVersion(PdfVersion.PDF_2_0);
            }
        }
        return writer;
    }

    private void setupUAConfiguration(PdfUAConfig config) {
        // Basic configuration.
        this.setTagged();
        this.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        this.getCatalog().setLang(new PdfString(config.getLanguage()));
        final PdfDocumentInfo info = this.getDocumentInfo();
        info.setTitle(config.getTitle());
    }

    /**
     * Gets correct {@link PdfUAChecker} for specified PDF/UA conformance.
     *
     * @param uaConformance the conformance for which checker is needed
     *
     * @return the correct PDF/UA checker
     */
    private PdfUAChecker getCorrectCheckerFromConformance(PdfUAConformance uaConformance) {
        PdfUAChecker checker;
        switch (uaConformance.getPart()) {
            case "1":
                checker = new PdfUA1Checker(this);
                break;
            case "2":
                checker = new PdfUA2Checker(this);
                break;
            default:
                throw new IllegalArgumentException(
                        PdfUAExceptionMessageConstants.CANNOT_FIND_PDF_UA_CHECKER_FOR_SPECIFIED_CONFORMANCE);
        }
        return checker;
    }
}
